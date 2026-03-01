package xinhao.foodshare.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 工具类
 */
@Component
public class JwtUtil {

    /** Token 有效期：1 小时 */
    /* public static final long JWT_TTL = 60 * 60 * 1000L; */
    public static final long JWT_TTL = 60 * 60 * 100000L;

    /**
     * ⚠️ HS256 要求至少 32 字节密钥
     * 实际项目建议放在配置文件 / 环境变量
     */
    private static final String JWT_SECRET =
            "lixinhao123lixinhao123lixinhao123";

    /** 生成安全密钥 */
    private static final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成 JWT（推荐）
     */
    public static String createToken(String claims) {
        return createToken(claims, JWT_TTL);
    }

    /**
     * 生成 JWT（自定义过期时间）
     */
    public static String createToken(String claims, long ttlMillis) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer("foodshare")
                .setSubject(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(nowMillis + ttlMillis))
                .signWith(SECRET_KEY)
                .compact();

    }

    /**
     * 解析 JWT
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 判断 Token 是否过期
     */
/*    public static boolean isExpired(String token) {
        try {
            Date expiration = parseToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }*/

    /**
     * 示例
     */
/*    public static void main(String[] args) throws  Exception{
        Claims claims = parseToken("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlOTY0NDBjZi05ZTRhLTRmM2EtOWZjMC1lZjA4NWEyY2UwNzciLCJpc3MiOiJmb29kc2hhcmUiLCJzdWIiOiIxIiwiaWF0IjoxNzY3NTM4ODk4LCJleHAiOjE3Njc1NDI0OTh9.EwpO-DNXDsJNAY8SQfgzPLAdbYETAmpVTjXoELhM4XU");
        String subject = claims.getSubject();
        System.out.println(subject);
    }*/


}
