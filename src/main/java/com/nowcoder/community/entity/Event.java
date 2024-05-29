package com.nowcoder.community.entity;

import com.nowcoder.community.util.MailClient;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: Event
 * Package: com.nowcoder.community.entity
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Data
public class Event {

    private String topic;
    private int userId;
    private int entityType;
    private int entityId;
    private int entityUserId;
    private Map<String, Object> data = new HashMap<>();

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

}
