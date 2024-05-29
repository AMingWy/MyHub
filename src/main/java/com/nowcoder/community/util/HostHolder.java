package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * ClassName: HostHolder
 * Package: com.nowcoder.community.util
 * Description:
 *
 * 持有用户的信息，用于代替session对象
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Component
public class HostHolder {

    // 已线程为单位存取值，把user存入当前线程，可供本次请求随时获取。
    private ThreadLocal<User> users = new ThreadLocal<User>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
