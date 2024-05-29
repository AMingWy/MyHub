package com.nowcoder.community.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * ClassName: WkConfig
 * Package: com.nowcoder.community.config
 * Description: 应用启用时的初始化工作
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */

@Configuration
public class WkConfig {

    private static final Logger logger = LoggerFactory.getLogger(WkConfig.class);

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    /**
     * 检查并创建一个指定路径的文件目录(服务端，Config都作用于服务端)
     */
    @PostConstruct
    public void init() {
        // 创建WK图片目录
        File file = new File(wkImageStorage);
        if(!file.exists()) {
            file.mkdir();
            logger.info("创建WK图片目录：" + wkImageStorage);
        }
    }

}
