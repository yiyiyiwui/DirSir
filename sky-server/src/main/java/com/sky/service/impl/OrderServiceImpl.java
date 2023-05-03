package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.ThreadLocalUtil;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.BusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.AddressBookService;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor // lombok 代替 @Autowired
@Service
public class OrderServiceImpl implements OrderService {

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
}
