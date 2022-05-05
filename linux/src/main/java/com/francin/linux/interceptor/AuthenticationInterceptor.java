package com.francin.linux.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.francin.linux.annotation.PassToken;
import com.francin.linux.annotation.UserLoginToken;
import com.francin.linux.common.TokenService;
import com.francin.linux.mapper.StudentMapper;
import com.francin.linux.mapper.UserMapper;
import com.francin.linux.pojo.User;
import com.francin.linux.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) {
        String token = httpServletRequest.getHeader("Authorization");// 从 http 请求头中取出 token
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if (userLoginToken.required()) {
                // 执行认证
                if (token == null) {
                    throw new RuntimeException("无token，请重新登录");
                }

                // 获取 token 中的 stuNo
                String id = TokenService.getAudience(token);
                User user = userMapper.queryById(Integer.parseInt(id));

                if (user == null) {
                    throw new RuntimeException("用户不存在，请重新登录");
                }
                // 验证 token
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
                try {
                    jwtVerifier.verify(token);
                } catch (TokenExpiredException e) {
                    throw new RuntimeException("token过期，请重新登录");
                } catch (JWTVerificationException e) {
                    throw new RuntimeException("token错误，请重新登录");
                }
                httpServletRequest.setAttribute("currentUser", user);
                return true;
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}