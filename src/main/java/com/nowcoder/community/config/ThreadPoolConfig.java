package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ClassName: ThreadPoolConfig
 * Package: com.nowcoder.community.config
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Configuration
// 使用定时任务
@EnableScheduling
// 启用Spring异步方法执行功能，允许在应用程序中使用异步方法执行，可以提高系统并发能力和性能
@EnableAsync
public class ThreadPoolConfig {



}
