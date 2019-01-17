package top.lw33.seckill.redis;

/**
 * @Author lw
 * @Date 2019-01-16 10:33:28
 **/
public abstract class BaseKeyPrefix implements KeyPrefix{

    private String prefix;
    private int expireSeconds;

    public BaseKeyPrefix(String prefix) {
        this(prefix, 0);
    }

    public BaseKeyPrefix(String prefix, int expireSeconds) {
        this.prefix = prefix;
        this.expireSeconds = expireSeconds;
    }

    @Override
    public String getPrefix() {
        return (getClass().getSimpleName() + ":" + prefix+":").toUpperCase();
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }
}
