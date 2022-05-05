package com.francin.linux.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.francin.linux.pojo.User;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service("TokenService")
public class TokenService {
    public String getToken(User user) {
        String token="";

        Calendar nowTime = Calendar.getInstance();
//        nowTime.add(Calendar.SECOND,120);
        nowTime.add(Calendar.MINUTE,120);
        Date expiresDate = nowTime.getTime();

        token= JWT.create().withAudience(user.getId() + "")// 将 user id 保存到 token 里面
                .withIssuedAt(new Date())       // 发行时间
                .withExpiresAt(expiresDate)     // 有效期
                .sign(Algorithm.HMAC256(user.getPassword()));// 以 password 作为 token 的密钥
        return token;
    }

    /**
     * 获取签发对象
     */
    public static String getAudience(String token) {
        String audience = null;
        try {
            audience = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new RuntimeException("token错误，请重新登录");
        }
        return audience;
    }
}