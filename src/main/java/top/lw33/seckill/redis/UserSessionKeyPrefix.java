package top.lw33.seckill.redis;

/**
 * @Author lw
 * @Date 2019-01-16 12:13:13
 **/
public class UserSessionKeyPrefix extends BaseKeyPrefix{

    private static final int EX_SECOND = 3600 * 24 * 2;

    public UserSessionKeyPrefix(String prefix, int expireSeconds) {
        super(prefix, expireSeconds);
    }

    public static final UserSessionKeyPrefix tokenKey = new UserSessionKeyPrefix("TOKEN", EX_SECOND);

}
