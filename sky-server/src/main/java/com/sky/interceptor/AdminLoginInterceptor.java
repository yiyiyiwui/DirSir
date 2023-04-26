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
@Component
@Slf4j
public class AdminLoginInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("拦截资源路径：{}", request.getRequestURI());
        //1 从指令头中获取令牌
        String token = request.getHeader("token");
        //2 校验令牌
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecret(), token);
            Long empId = Long.valueOf(claims.get("empId").toString());
            //把empId绑定到线程上
            ThreadLocalUtil.setCurrentId(empId);
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
        //从线程中一处empId
        ThreadLocalUtil.removeCurrentId();
    }
}
