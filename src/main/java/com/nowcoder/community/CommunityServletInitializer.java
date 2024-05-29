package com.nowcoder.community;


import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ClassName: CommunityServletInitializer
 * Package: com.nowcoder.community
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
public class CommunityServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 指定了Spring Boot主类为CommunityApplication
        return builder.sources(CommunityApplication.class);
    }
}
