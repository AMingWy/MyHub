package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * ClassName: RedisConfig
 * Package: com.nowcoder.community.config
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Configuration
public class RedisConfig {

    /**
     * *redis序列化的工具定置类，下面这个请一定开启配置
     * *127.0.0.1:6379> keys *
     * *1) “ord:102” 序列化过
     * *2)“\xaclxedlxeelx05tixeelaord:102” 野生，没有序列化过
     * *this.redisTemplate.opsForValue(); //提供了操作string类型的所有方法
     * *this.redisTemplate.opsForList();// 提供了操作List类型的所有方法
     * *this.redisTemplate.opsForset(); //提供了操作set类型的所有方法
     * *this.redisTemplate.opsForHash(); //提供了操作hash类型的所有方认
     * *this.redisTemplate.opsForZSet(); //提供了操作zset类型的所有方法
     * param LettuceConnectionFactory
     * return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string() );
        // 设置value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        // 设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        // 使配置生效
        template.afterPropertiesSet();
        return template;
    }

}
