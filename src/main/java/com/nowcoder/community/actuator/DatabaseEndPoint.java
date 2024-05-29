package com.nowcoder.community.actuator;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * ClassName: DatabaseEndpoint
 * Package: com.nowcoder.community.actuator
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Component
// 这个类是一个端点，是Spring Boot Actuator一部分，用于暴露应用程序的监控和管理端点
@Endpoint(id = "database")
public class DatabaseEndPoint {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndPoint.class);

    @Autowired
    private DataSource dataSource;

    /**
     * @ReadOperation代表仅读的EndPoint。即只允许Get请求来访问此端点。
     * 还有@WriterOperation注解，代表Post、Put等请求访问端点。
     * 该方法用于检查数据库连接是否正常
     * @return
     */
    @ReadOperation
    public String checkConnection() {
        try (
                Connection conn = dataSource.getConnection()
        ) {
            return CommunityUtil.getJSONString(0, "获取连接成功！");
        } catch(SQLException e) {
            logger.error("获取连接失败：" + e.getMessage());
            return CommunityUtil.getJSONString(1, "获取连接失败！");
        }
    }
}
