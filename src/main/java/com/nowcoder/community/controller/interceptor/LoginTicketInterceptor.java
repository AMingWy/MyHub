package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;

import com.nowcoder.community.util.HostHolder;
import org.springframework.security.core.Authentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;



import java.util.Date;

/**
 * ClassName: LoginTicketInterceptor
 * Package: com.nowcoder.community.controller.interceptor
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie中获取凭证ticket
        String ticket = CookieUtil.getValue(request, "ticket");

        if(ticket!=null){
            // 查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 检查凭证是否有效
            if(loginTicket!=null && loginTicket.getStatus()==0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求中持有用户
                hostHolder.setUser(user);
                // 构建用户认证的结果,并存入SecurityContext,以便于Security进行授权.
                Authentication authenticator = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user.getId()));
                // 实现持久化的第一个步骤：在运行前，SecurityContextHolder从SecurityContextRepository中读取SecurityContext
                SecurityContextHolder.setContext(new SecurityContextImpl(authenticator));
                // 实现持久化的第二个步骤：运行结束后，SecurityContextHolder将修改后的SecurityContext再存入SecurityContextRepository中，以便下次访问
                securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
            }
        }

        return true;
    }

    @Override
    public void postHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        // 在请求处理完成后执行，将当前用户信息添加到mvc中，以便视图中显示当前登陆用户的信息
        if(user!=null && modelAndView !=null){
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
        // 清理权限
        SecurityContextHolder.clearContext();
    }
}
