package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * ClassName: LoginRequiredInterceptor
 * Package: com.nowcoder.community.controller.interceptor
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    //@Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断handler是否为HandlerMethod实例
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            // 当前方法需要登陆，并且用户未登录，重定向到login
            if(loginRequired != null && hostHolder.getUser() == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                // 后续拦截器不再执行
                return false;
            }
        }
        // 不需要登陆或用户已经登陆，继续执行后续拦截器
        return true;
    }
}
