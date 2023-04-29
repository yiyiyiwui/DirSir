package com.sky.interceptor;

import com.sky.context.ThreadLocalUtil;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*JWT令牌登录校验的拦截器（小程序）*/
@Component
@Slf4j
public class UserLoginInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    /*校验JWT*/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("拦截资源路径：{}", request.getRequestURI());
        //1 从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());
        //2 校验令牌
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecret(), token);
            Long userId = Long.valueOf(claims.get("userId").toString());
            //2.1 绑定便令到线程上
            ThreadLocalUtil.setCurrentId(userId);
            //3 通过，放行
            return true;
        } catch (Exception e) {
            //4 不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //移除线程上绑定的变量
        ThreadLocalUtil.removeCurrentId();
    }
}
