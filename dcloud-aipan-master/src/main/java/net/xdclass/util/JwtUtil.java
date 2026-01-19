package net.xdclass.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.dto.AccountDTO;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@Slf4j
public class JwtUtil {

    // JWT的主题
    private static final String LOGIN_SUBJECT = "XDCLASS";


    //分享主题
    private static final String SHARE_SUBJECT = "XDCLASS_SHARE";

    //分享ID
    public static final String CLAIM_SHARE_KEY = "SHARE_ID";

    //分享token过期时间，1小时
    private static final long SHARE_TOKEN_EXPIRE = 1000 * 60 * 60;


    //注意这个密钥长度需要足够长, 推荐：JWT的密钥，从环境变量中获取
    private final static String SECRET_KEY = "xdclass.net168xdclass.net168xdclass.net168xdclass.net168";
    // 签名算法
    private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;
    // 使用密钥
    private final static SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    // token过期时间，30天
    private static final long EXPIRED = 1000 * 60 * 60 * 24 * 7;

    /**
     * 生成JWT
     * @param accountDTO 登录账户信息
     * @return 生成的JWT字符串
     * @throws NullPointerException 如果传入的accountDTO为空
     */
    public static String geneLoginJWT(AccountDTO accountDTO) {
        if (accountDTO == null) {
            throw new NullPointerException("对象为空");
        }

        // 创建 JWT token
        String token = Jwts.builder()
                .subject(LOGIN_SUBJECT)
                .claim("accountId", accountDTO.getId())
                .claim("username", accountDTO.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRED))
                .signWith(KEY, ALGORITHM)  // 直接使用KEY即可
                .compact();

        // 添加自定义前缀
        return addPrefix(token,LOGIN_SUBJECT);
    }

    /**
     * 校验JWT
     * @param token JWT字符串
     * @return JWT的Claims部分
     * @throws IllegalArgumentException 如果传入的token为空或只包含空白字符
     * @throws RuntimeException 如果JWT签名验证失败、JWT已过期或JWT解密失败
     */
    public static Claims checkLoginJWT(String token) {
        try {
            log.debug("开始校验 JWT: {}", token);
            // 校验 Token 是否为空
            if (token == null || token.trim().isEmpty()) {
                log.error("Token 不能为空");
                throw new IllegalArgumentException("Token 不能为空");
            }
            token = token.trim();
            // 移除前缀
            token = removePrefix(token,LOGIN_SUBJECT);
            log.debug("移除前缀后的 Token: {}", token);
            // 解析 JWT
            Claims payload = Jwts.parser()
                    .verifyWith(KEY)  //设置签名的密钥, 使用相同的 KEY
                    .build()
                    .parseSignedClaims(token).getPayload();

            log.info("JWT 解密成功，Claims: {}", payload);
            return payload;
        } catch (IllegalArgumentException e) {
            log.error("JWT 校验失败: {}", e.getMessage(), e);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("JWT 签名验证失败: {}", e.getMessage(), e);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("JWT 已过期: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("JWT 解密失败: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 给token添加前缀
     * @param token 原始token字符串
     * @return 添加前缀后的token字符串
     */
    private static String addPrefix(String token,String prefix) {
        return  prefix+token;
    }

    /**
     * 移除token的前缀
     * @param token 带前缀的token字符串
     * @return 移除前缀后的token字符串
     */
    private static String removePrefix(String token,String prefix) {
        if (token.startsWith(prefix)) {
            return token.replace(prefix, "").trim();
        }
        return token;
    }



    /**
     * 创建分享的令牌
     */
    public static String geneShareJWT(  Object claimValue) {

        // 创建 JWT token
        String compact = Jwts.builder()
                .subject(SHARE_SUBJECT)
                .claim(CLAIM_SHARE_KEY, claimValue)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + SHARE_TOKEN_EXPIRE))
                .signWith(KEY, ALGORITHM)  // 直接使用KEY即可
                .compact();
        return compact;
    }

    /**
     * 创建分享的令牌
     */
    public static Claims checkShareJWT(String token) {
        try {
            log.debug("开始校验 Share JWT: {}", token);
            // 校验 Token 是否为空
            if (token == null || token.trim().isEmpty()) {
                log.error("Share Token 不能为空");
                return null;
            }
            token = token.trim();
            // 解析 JWT
            Claims payload = Jwts.parser()
                    .verifyWith(KEY)  //设置签名的密钥, 使用相同的 KEY
                    .build()
                    .parseSignedClaims(token).getPayload();

            log.info("Share JWT 解密成功，Claims: {}", payload);
            return payload;
        } catch (IllegalArgumentException e) {
            log.error("JWT 校验失败: {}", e.getMessage(), e);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("JWT 签名验证失败: {}", e.getMessage(), e);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("JWT 已过期: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("JWT 解密失败: {}", e.getMessage(), e);
        }
        return null;
    }





}