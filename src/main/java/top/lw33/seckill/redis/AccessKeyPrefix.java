package top.lw33.seckill.redis;

/**
 * @Author lw
 * @Date 2019-01-16 10:35:09
 **/
public class AccessKeyPrefix extends BaseKeyPrefix{

    public static final int EXPIRE_SEC = 5;

    private AccessKeyPrefix(String prefix, int expireSeconds) {
        super(prefix, expireSeconds);
    }

    public static final AccessKeyPrefix accessTimes = new AccessKeyPrefix("accessTimes", EXPIRE_SEC);

    public static AccessKeyPrefix withExpire(int expireSeconds) {
        return new AccessKeyPrefix("accessTimes", expireSeconds);
    }
}
