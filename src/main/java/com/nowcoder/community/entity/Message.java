package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * ClassName: Message
 * Package: com.nowcoder.community.entity
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Data
public class Message {

    private int id;

    private int fromId;

    private int toId;

    private String conversationId;

    private String content;

    /**
     * 0-未读;1-已读;2-删除;
     */
    private int status;

    private Date createTime;

}
