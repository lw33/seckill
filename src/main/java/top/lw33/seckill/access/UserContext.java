package top.lw33.seckill.access;

import top.lw33.seckill.entity.User;

/**
 * @Author lw
 * @Date 2019-01-20 21:59:27
 **/
public class UserContext {
    public static final ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static User get() {
        return userHolder.get();
    }

    public static void set(User user) {
        userHolder.set(user);
    }

    public static void remove() {
        userHolder.remove();
    }
}
