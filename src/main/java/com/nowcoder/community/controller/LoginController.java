package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.RedisKeyUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: LoginController
 * Package: com.nowcoder.community.controller
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * Gets register page.
     *
     * @return the register page
     */
    @GetMapping(path = "/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    /**
     * Gets login page.
     *
     * @return the login page
     */
    @GetMapping(path = "/login")
    public String getLoginPage() {
        return "/site/login";
    }

    /**
     * 注册业务功能.
     *
     * @param model the model
     * @param user  the user
     * @return the string
     */
    @PostMapping(path = "/register")
    public String register(Model model, User user){
        Map<String ,Object>map = userService.register(user);
        if(map==null || map.isEmpty()){
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg "));
            model.addAttribute("passwordMsg", map.get("passwordMsg "));
            model.addAttribute("emailMsg", map.get("emailMsg "));
            return "/site/register";
        }
    }

    /**
     * 邮件激活账号业务
     * RESTFUL风格：http://localhost:8080/community/activation/101/code
     *
     * @param model
     * @param userId
     * @param code
     * @return
     */
    // http://localhost:8080/community/activation/101/code
    @GetMapping(path = "/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if(result==ACTIVATION_SUCCESS){
            model.addAttribute("msg", "激活成功，您的账号已经可以正常使用了");
            model.addAttribute("target", "/login");
        } else if (result==ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已经激活过了");
            model.addAttribute("target", "/index");
        }else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    /**
     * 生成验证码用于显示，并把结果存入Redis中。
     *
     * @param response the response
     * @param session  the session （弃用session）
     */
    @GetMapping(path = "/kaptcha")
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session
        // session.setAttribute("kaptcha", text);

        // 验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);


        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败" + e.getMessage());
        }

    }

    /**
     * Login string.
     * 登陆的业务功能
     *
     * @param username   the username
     * @param password   the password
     * @param code       the code
     * @param rememberMe the remember me
     * @param model      the model
     * @param session    the session (弃用session)
     * @param response   the response
     * @return the string
     */
    @PostMapping(path = "/login")
    public String login(String username, String password, String code, boolean remeberme,
                        Model model/*, HttpSession session*/, HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner) {
        // 检查验证码
        // String kaptcha = (String)session.getAttribute("kaptcha");
        String kaptcha = null;
        if(StringUtils.isNoneBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
           model.addAttribute("codeMsg", "验证码不正确");
           return "/site/login";
        }

        // 检查账号，密码
        int expiredSeconds = remeberme ? REMEBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }

    }

    /**
     * 退出账户
     *
     * @param ticket
     * @return
     */
    @GetMapping(path = "/logout")
    @LoginRequired
    public String logout(@CookieValue("ticket") String ticket, HttpServletResponse response) {
        userService.logout(ticket);
        // 清理SecurityContextHollder中的权限
        SecurityContextHolder.clearContext();

        // 清理权限后，认证信息还存在 session 中，没有被清理(securityContextRepository是基于session的)
        // 我们采取覆盖的方式去清理掉cookie，从而清除认证信息。
        // 不然会发生：在退出后再次访问需要登录的功能会显示没有权限而不是没登录
        Cookie cookie = new Cookie("JSESSIONID", CommunityUtil.generateUUID());
        response.addCookie(cookie);

        return "ridirect:/login";
    }


}
