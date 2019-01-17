package top.lw33.seckill.redis;

/**
 * @Author lw
 * @Date 2019-01-16 10:29:33
 **/
public interface KeyPrefix {

    String getPrefix();

    int expireSeconds();
}
