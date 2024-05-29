package com.nowcoder.community.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.erhlc.RestClients;

/**
 * ClassName: EsConfig
 * Package: com.nowcoder.community.config
 * Description:
 * 实现模糊类 AbstractElasticsearchConfiguration
 * 来得到 RestHighLevelClient 用于查询
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Configuration
public class EsConfig {
    @Value("${spring.elasticsearch.uris}")
    private String esUrl;

    // localhost:9200 写在配置文件中,直接用 <- spring.elasticsearch.uris
    @Bean
    // 创建并返回一个RestHighLevelClient对象
    RestHighLevelClient client() {
        // 创建一个ClientConfiguration对象，用于配置Es客户端连接的属性
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(esUrl)//elasticsearch地址
                .build();// 配置构建完成，返回一个配置对象
        // 使用RestClients工厂类基于前面clientConfiguration创建了一个REST客户端，并将其返回作为RestHighLevelClient的bean
        return RestClients.create(clientConfiguration).rest();
    }
}