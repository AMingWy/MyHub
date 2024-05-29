package com.nowcoder.community.config;

import com.nowcoder.community.controller.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ClassName: WebMvcConfig
 * Package: com.nowcoder.community.config
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaInterceptor alphaInterceptor;

    // 处理登陆凭证的拦截器
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    // 废弃拦截器，改用Spring Security 做权限控制
    //@Autowired
    //private LoginRequiredInterceptor loginRequiredInterceptor;

    // 消息拦截器，处理消息相关逻辑
    @Autowired
    private MessageInterceptor messageInterceptor;

    // 数据拦截器，处理数据相关逻辑
    @Autowired
    private DataInterceptor dataInterceptor;

    // 重写了WebMvcConfigurer中方法，用于注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // registry.addInterceptor(alphaInterceptor)
        //        .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")
        //        .addPathPatterns("/register", "/login");

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        //registry.addInterceptor(loginRequiredInterceptor)
        //        .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");



        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");


        registry.addInterceptor(dataInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

    }
}
