package com.nowcoder.community.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactoryFriend;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ClassName: ServiceLogAspect
 * Package: com.nowcoder.community.aspect
 * Description:拦截并记录对Service层方法的调用日志
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Component
// 切面类，用于定义切面，即横切关注点
@Aspect
public class ServiceAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAspect.class);

    // 定义切点，指定切面拦截方法调用为com.nowcoder.community.service.包下所有方法
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {

    }

    /**
     * Before.在切点方法执行前执行相关逻辑
     * 实现针对service记录日志的业务逻辑
     */
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 用户[1.2.3.4]，在[xxx]，访问了[com.nowcoder.community.service.xxx()].
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String ip = "";
        if(attributes == null){
            // 这是一个特殊调用(Kafka消息队列调用service，没有来源ip，无法记日志)
            ip = "系统服务";
        } else {
            HttpServletRequest request = attributes.getRequest();
            ip = request.getRemoteHost();
        }
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s].", ip, now, target));
    }


}
