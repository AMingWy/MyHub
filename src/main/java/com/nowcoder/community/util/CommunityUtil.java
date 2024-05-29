package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ClassName: CommunityUtil
 * Package: com.nowcoder.community.util
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
public class CommunityUtil {

    // 生成随机字符串 用于验证
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll(" ", "");
    }

    // MD5算法加密
    // hello -> abc123def456
    // hello + 3e4a8 -> abc123def456abc 加盐salt
    public static String md5(String key) {
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 使用Bcrypt来进行加密，能防止彩虹表
     * @param key
     * @return
     */
    public static String Bcrypt(String key) {
        String idForEncode = "bcrypt";
        Map encoders = new HashMap<>();
        encoders.put(idForEncode, new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        encoders.put("pbkdf2", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_5());
        encoders.put("pbkdf2@SpringSecurity_v5_8", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("scrypt", SCryptPasswordEncoder.defaultsForSpringSecurity_v4_1());
        encoders.put("scrypt@SpringSecurity_v5_8", SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("argon2", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_2());
        encoders.put("argon2@SpringSecurity_v5_8", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("sha256", new StandardPasswordEncoder());

        PasswordEncoder passwordEncoder =
                new DelegatingPasswordEncoder(idForEncode, encoders);
        return passwordEncoder.encode(key);
    }

    //
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if(map != null) {
            for(String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

}
