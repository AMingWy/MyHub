package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;


/**
 * ClassName: EventConsumer
 * Package: com.nowcoder.community.event
 * Description:消费者类，从Kafka消息队列中接收事件，并根据事件类型执行相应操作
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Value("${wk.image.command}")
    private String wkImageCommand;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.share.name}")
    private String shareBucketName;

    /**
     * 用于执行定时任务的线程池任务调度器
     */
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * Handle comment message.
     * 消费点赞评论关注的事件
     * @KafkaListener用于监听Kafka Topic中的消息
     *
     * @param record the record
     */
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        // 进行空值和格式检查
        if(record == null || record.value() == null){
            logger.error("消息的内容为空！");
            return;
        }
        // 解析消息为Event对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        // 格式判断
        if(event == null) {
            logger.error("消息格式错误!");
            return;
        }

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityId", event.getEntityId());
        content.put("entityType", event.getEntityType());

        if(event.getData().isEmpty()) {
            for(Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    /**
     * Handle publish message.
     * 消费发布的事件
     *
     * @param record the record
     */
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        // 空值判断
        if(record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }
        // 解析为Event对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        // 格式判断
        if(event == null) {
            logger.error("消息格式错误！");
            return;
        }
        // 保存帖子到es
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }

    /**
     * Handle delete message.
     * 消费删除事件
     *
     * @param record the record
     */
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if(record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null) {
            logger.error("消息格式错误！");
            return;
        }
        // es中删除
        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }

    /**
     * 消费分享事件
     *
     * @param record
     */
    @KafkaListener(topics = TOPIC_SHARE)
    public void handleShareMessage(ConsumerRecord record) {
        if(record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null) {
            logger.error("消息格式错误！");
            return;
        }

        // 从事件数据中获取信息
        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        // 构建生成长图的命令cmd
        String cmd = wkImageCommand + "--quality 75 "
                + htmlUrl + " " + wkImageStorage + "/" + fileName + suffix;
        // 尝试执行命令，成功则记录成功日志，否则生成失败日志
        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("生成长图成功：" + cmd);
        } catch (IOException e) {
            logger.error("生成长图失败：" + e.getMessage());
        }

        // 启用定时器，监视该图片，一旦生成了，则上传至七牛云
        UploadTask task = new UploadTask(fileName, suffix);
        Future future = taskScheduler.scheduleAtFixedRate(task, 500);
        // 将定时任务的Future对象设置到task中，以便在适当时候取消定时任务
        task.setFuture(future);
    }

    /**
     * 用于监视图片生成并上传至七牛云
     */
    class UploadTask implements Runnable {

        // 文件名称
        private String fileName;
        // 文件后缀
        private String suffix;
        // 启动任务的返回值
        private Future future;
        // 开始时间
        private long startTime;
        // 上传次数
        private int uploadTimes;

        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        @Override
        public void run() {
            // 生成图片失败
            if (System.currentTimeMillis() - startTime > 30000) {
                logger.error("执行时间过长，未检测到系统生成长图，终止任务: " + fileName);
                future.cancel(true);
                return;
            }
            // 长传失败
            if (uploadTimes >= 3) {
                logger.error("上传次数过多，七牛云服务器上传有问题，终止任务: " + fileName);
                future.cancel(true);
                return;
            }

            String path = wkImageStorage + "/" + fileName + suffix;
            File file = new File(path);
            if (file.exists()) {
                logger.info(String.format("开始第%d次上传[%s].", ++uploadTimes, fileName));
                // 设置响应信息
                StringMap putPolicy = new StringMap();
                putPolicy.put("returnBody", CommunityUtil.getJSONString(0));
                // 生成七牛云的上传凭证
                Auth auth = Auth.create(accessKey, secretKey);
                String uploadToken = auth.uploadToken(shareBucketName, fileName, 3600, putPolicy);
                // 指定上传的机房
                UploadManager manager = new UploadManager(new Configuration(Region.huanan()));
                try {
                    // 开始上传图片
                    Response response = manager.put(
                            path, fileName, uploadToken, null, "image/" + suffix, false
                    );
                    // 处理响应结果
                    JSONObject json = JSONObject.parseObject(response.bodyString());
                    if (json == null || json.get("code") == null || !json.get("code").toString().equals("0")) {
                        logger.info(String.format("第%d次上传失败[%s].", uploadTimes, fileName));
                    } else {
                        logger.info(String.format("第%d次上传成功[%s].", uploadTimes, fileName));
                        future.cancel(true);
                    }
                } catch (QiniuException e) {
                    logger.info(String.format("第%d次上传失败[%s].", uploadTimes, fileName));
                }
            } else {
                logger.info("等待图片生成[" + fileName + "].");
            }
        }
    }

}
