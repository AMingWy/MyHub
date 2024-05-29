package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * ClassName: EventProducer
 * Package: com.nowcoder.community.event
 * Description:发布事件到Kafka消息队列中，通过注入KafkaTemplate，发送消息到Kafka Topic中
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * Fire event.
     * 处理事件
     *
     * @param event the event
     */
    public void fireEvent(Event event) {
        // 通过send方法，将事件对象event序列化为JSON字符串，并发送到指定Kafka主题中
        // 将事件发布导指定的主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }

}
