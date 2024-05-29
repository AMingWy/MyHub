package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * ClassName: KaptchaConfig
 * Package: com.nowcoder.community.config
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Configuration
public class KaptchaConfig {

    @Bean
    // kaptchaProducer()方法返回一个Producer类型的bean，用于生成验证码
    public Producer kaptchaProducer() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        properties.setProperty("kaptcha.textproducer.color", "0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");

        // 创建一个DefaultKaptcha对象，这是一个验证码生成器
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        // 创建一个Config对象，将设置好的属性传入
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }

}
