package com.nowcoder.community.util;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * ClassName: MailClient
 * Package: com.nowcoder.community.util
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Component
public class MailClient {
    private static final Logger logger =LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;     // 发件人

    public void sendMail(String to, String subject, String content){
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);

        } catch (MessagingException e){
            logger.error("发送邮件失败：" + e.getMessage());
        }

    }
}
