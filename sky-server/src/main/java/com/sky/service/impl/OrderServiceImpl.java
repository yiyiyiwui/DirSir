package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.ThreadLocalUtil;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.BusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor // lombok 代替 @Autowired
@Service

public class OrderServiceImpl implements OrderService {

    @Value("${sky.shop.address}")
    private String shopAddress;

    @Value("${sky.baidu.ak}")
    private String ak;


    private final OrderMapper orderMapper;

    private final OrderDetailMapper orderDetailMapper;

    private final ShoppingCartService shoppingCartService;

    private final AddressBookMapper addressBookMapper;

    private final ShoppingCartMapper shoppingCartMapper;

    // 提交订单
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        // 1.查询收货地址信息
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new BusinessException("请选择正确收货地址下单");
        }
        // 2.dto->entity
        Orders orders = BeanUtil.copyProperties(ordersSubmitDTO, Orders.class);
        // 补充信息
        orders.setNumber(RandomUtil.randomNumbers(8)); // 订单号
        orders.setStatus(Orders.PENDING_PAYMENT); // 订单状态 1待付款
        orders.setUserId(ThreadLocalUtil.getCurrentId()); // 下单用户
        orders.setOrderTime(LocalDateTime.now());// 下单时间
        orders.setPayStatus(Orders.UN_PAID);// 支付状态 0未支付
        orders.setPhone(addressBook.getPhone()); // 手机号
        orders.setAddress(addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());// 地址信息
        orders.setConsignee(addressBook.getConsignee()); // 收货人
        // 3.保存订单
        orderMapper.insert(orders);
        // 检查用户的收获地址是否超出配送范围
        checkOutOfRange(addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());

        // 4.查询购物车列表，遍历
        List<ShoppingCart> cartList = shoppingCartService.getList();
        if (CollUtil.isEmpty(cartList)) {
            throw new BusinessException("请选择菜品后进行下单");
        }
        for (ShoppingCart shoppingCart : cartList) {
            // 4-1 购物项转订单明细
            OrderDetail orderDetail = BeanUtil.copyProperties(shoppingCart, OrderDetail.class, "id");
            // 4-2 订单明细设置订单id
            orderDetail.setOrderId(orders.getId());
            // 4-3 保存订单明细
            orderDetailMapper.insert(orderDetail);
        }

        // 4-4 清空购物车
        shoppingCartService.clean();

        // 5.组装并返回vo
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .orderTime(orders.getOrderTime())
                .build();
        return orderSubmitVO;
    }

    // 订单支付
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {

        // 1.直接修改订单状态
        Orders ordersDB = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber());

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);

        // 2. 返回一个空结果
        OrderPaymentVO vo = OrderPaymentVO.builder()
                .nonceStr("52640818796650160489223277005653")
                .paySign("iHYG8l90s5nIXMWgkmN6PX2+3e4mW4spWMOLnvdQZTePZiMy/CDiP3CfvsByp65PpnVcmG1Br1EY7f46xeToKOlmK2qe00IFBaXUtNH/6+k5Ij7fXRKNRQxQuODegkWSvX+fw2FKo8qKT1clJd5/T/Hkwu6cSDZGqHIaW3eqha14HRpsT5siHlwoHw04X5wVvnktAx4Koko/tsMtI/t/dkCDvIbCve1ut7/FVVtlgNJKMR6rzY0wiyroseSy3qjbw6BUL+HPnxlLqF2PNbk9jkimyxJrwzxk2NFxjHM87tybMBMTITCuIuH9hZCFFbJTFsG9BYsL2H7GcsaYmzIoig==")
                .timeStamp("1683009626")
                .signType("RSA")
                .packageStr("prepay_id=wx02144025953621ab23eaa2fc334f2a0000")
                .build();
        return vo;
    }

    /*历史订单*/
    @Override
    public PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        //1 dto补充用户id
        ordersPageQueryDTO.setUserId(ThreadLocalUtil.getCurrentId());
        //2 开启分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        //3 条件查询
        List<OrderVO> voList = orderMapper.getList(ordersPageQueryDTO);
        Page<OrderVO> page = (Page<OrderVO>) voList;
        //4 遍历orderVOList
        for (OrderVO orderVO : voList) {
        //4.1 根据订单id查询订单明细列表
           List<OrderDetail> detailList =  orderDetailMapper.getByOrderId(orderVO.getId());
        //4.2 设置到VO中
            orderVO.setOrderDetailList(detailList);
        }
        //5 返回分页对象
        return new PageResult(page.getTotal(),voList);
    }

    /*订单详情*/
    @Override
    public OrderVO orderDetail(Long id) {
        //1 根据订单id查询订单数据
        Orders orders = orderMapper.getById(id);
        //2 根据订单id查询明细数据
        List<OrderDetail> byOrderId = orderDetailMapper.getByOrderId(id);
        //3 封装orderVO=ordes+orderDetail
        OrderVO orderVO = BeanUtil.copyProperties(orders, OrderVO.class);
        orderVO.setOrderDetailList(byOrderId);
        return orderVO;
    }

    /*取消订单*/
    @Override
    public void cacel(Long id) {
        //1 根据id查询订单信息
        Orders orders = orderMapper.getById(id);
        //2 订单状态，如果已经做出来了就不可取消
        if (orders.getStatus() > 2) {
            throw new BusinessException("请联系商家取消订单");
        }
        //3 构建取消订单实体
        Orders orders1 = Orders.builder()
                .id(orders.getId())
                .status(Orders.CANCELLED)
                .cancelReason("用户取消订单")
                .cancelTime(LocalDateTime.now())
                .build();
        //4 如果为待接单，需要微信退款，然后修改订单支付状态
        //5 调用mapper修改订单信息
        orderMapper.update(orders1);
    }

    /*再来一单*/
    @Override
    public void repetition(Long id) {
        //1 根据订单id查询明细列表
        List<OrderDetail> detailList = orderDetailMapper.getByOrderId(id);
        //2 遍历明细列表
        for (OrderDetail orderDetail : detailList) {
        //2.1 明细封装到购物项
            ShoppingCart shoppingCart = BeanUtil.copyProperties(orderDetail, ShoppingCart.class,"id");
        //2.2 补充用户id到添加时间
        shoppingCart.setUserId(ThreadLocalUtil.getCurrentId());
        shoppingCart.setCreateTime(LocalDateTime.now());
        //2.3 调用购物车mapper保存数据库
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /*催单*/
    @Override
    public void reminder(Long id) {
    }



    /*订单搜索*/
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        //1 开启分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        //2 条件查询
//        List<OrderVO> voList = orderMapper.getList(ordersPageQueryDTO);
//        Page<OrderVO> page = (Page<OrderVO>) voList;
         Page<OrderVO> voList = (Page<OrderVO>) orderMapper.getList(ordersPageQueryDTO);//相当于上面两步
        //3 遍历list
        for (OrderVO orderVO : voList) {
        //3.1 根据订单id查询明细列表
            List<OrderDetail> detailList = orderDetailMapper.getByOrderId(orderVO.getId());
        //3.2 拼接字符串
            StringBuilder builder = new StringBuilder();
            for (OrderDetail orderDetail : detailList) {
                builder.append(orderDetail.getName() + "*" + orderDetail.getNumber() + ";");
            }
        //3.3 设置到vo的orederDishes属性中
            orderVO.setOrderDishes(builder.substring(0,builder.length()-1));
        }
        //4 返回分页对象
        return new PageResult(voList.getTotal(),voList.getResult());
    }

    /*订单数量统计*/
    @Override
    public OrderStatisticsVO statistics() {
        Integer confirmed = orderDetailMapper.findOrderStatistics(Orders.CONFIRMED);//订单状态为已接单
        Integer delivery_in_progress =orderDetailMapper.findOrderStatistics(Orders.DELIVERY_IN_PROGRESS);//订单状态-派送中
        Integer to_be_confirmed =orderDetailMapper.findOrderStatistics(Orders.TO_BE_CONFIRMED);// 订单状态- 待接单
//       Integer pending_payment =orderDetailMapper.findOrderStatistics(Orders.PENDING_PAYMENT);// 订单状态- 待付款
//       Integer cancelled =orderDetailMapper.findOrderStatistics(Orders.CANCELLED);// 订单状态- 已取消
//       Integer completed =orderDetailMapper.findOrderStatistics(Orders.COMPLETED);// 订单状态- 已完成
        //封装vo
        OrderStatisticsVO vo = OrderStatisticsVO.builder()
                .toBeConfirmed(to_be_confirmed)
                .confirmed(confirmed)
                .deliveryInProgress(delivery_in_progress)
                .build();
        return vo;
    }

    /*接单*/
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        //把状态修改为已结单
        Orders orders= Orders.builder().id(ordersConfirmDTO.getId()).status(Orders.CONFIRMED).build();
        //调用mapper修改
        orderMapper.update(orders);
    }

    /*拒单*/
    @Override
    public void finm(OrdersConfirmDTO ordersConfirmDTO) {
        //1获取订单id
        Orders orders = orderMapper.getById(ordersConfirmDTO.getId());
        //2判断订单是否处于待接单状态，如果不是，不可以拒单
        //商家拒单就是把订单状态改为已取消，记录拒单原因，拒单时间
        //2.1 商家拒单时，如果用户支付状态为已支付的话,给用户退款
        if (orders.getPayStatus().equals(Orders.PAID)) {
            orders.setPayStatus(Orders.REFUND);
        }
        // 判断订单是否处于待接单状态
//        if (orders.getStatus() != Orders.TO_BE_CONFIRMED) {
//            throw new BusinessException("您已接单，拒单会影响信誉");
//        }
        //2.2 订单拒绝原因
        orders.setRejectionReason(null);
        //2.3 取消订单
        orders.setStatus(Orders.CANCELLED);
        //2.4 订单取消原因
        orders.setCancelReason(null);
        //2.5 订单取消时间
        orders.setCancelTime(LocalDateTime.now());
        //3 修改执行操作
        orderMapper.update(orders);
    }

    /*取消订单*/
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        //1 获取订单id
        Orders byId = orderMapper.getById(ordersCancelDTO.getId());
        //2 取消订单
        byId.setStatus(Orders.CANCELLED);
        //2.1订单取消原因
        byId.getCancelReason();
        //2.2 订单取消时间
        byId.setCancelTime(LocalDateTime.now());
        //3 商家拒单时，如果用户支付状态为已支付的话,给用户退款
        if (byId.getPayStatus().equals(Orders.PAID)) {
            byId.setPayStatus(Orders.REFUND);
        }
        //4 修改订单状态
        orderMapper.update(byId);
    }

    /*派送订单*/
    @Override
    public void delivery(Long id) {
        //1获取订单id
        Orders orders = orderMapper.getById(id);
        //2 只有状态为“待派送”的订单可以执行派送订单操作
        if (orders.getStatus().equals(Orders.CONFIRMED)) {
            //把当前订单状态修改为派送中
            Orders build = Orders.builder()
                    .id(id)
                    .status(Orders.DELIVERY_IN_PROGRESS)
                    .build();
            orderMapper.update(build);
        }
    }

    /*完成订单*/
    @Override
    public void complete(Long id) {
        //1 获取订单id
        Orders orders = orderMapper.getById(id);
        //2 判断只有状态为“派送中”的订单可以执行订单完成操作
        if (orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
        //3 把当前状态修改为已完成
        Orders build = Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        orderMapper.update(build);
        }
    }


    /**
     * 检查客户的收货地址是否超出配送范围
     * @param address
     */
    private void checkOutOfRange(String address) {
        Map map = new HashMap();
        map.put("address",shopAddress);
        map.put("output","json");
        map.put("ak",ak);

        //获取店铺的经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new BusinessException("店铺地址解析失败");
        }

        //数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        //店铺经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address",address);
        //获取用户收货地址的经纬度坐标
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        jsonObject = JSON.parseObject(userCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new BusinessException("收货地址解析失败");
        }

        //数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        //用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin",shopLngLat);
        map.put("destination",userLngLat);


        //路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);

        jsonObject = JSON.parseObject(json);
        if(!jsonObject.getString("status").equals("0")){
            throw new BusinessException("配送路线规划失败");
        }

        //数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");

        if(distance > 5000){
            //配送距离超过5000米
            throw new BusinessException("超出配送范围");
        }
    }




}
