package top.lw33.seckill.redis;

/**
 * @Author lw
 * @Date 2019-01-16 10:35:09
 **/
public class SeckillKeyPrefix extends BaseKeyPrefix{


    private SeckillKeyPrefix(String prefix, int expireSeconds) {
        super(prefix, expireSeconds);
    }

    public static final SeckillKeyPrefix isGoodsOver = new SeckillKeyPrefix("isGoodsOver", 0);
    public static final SeckillKeyPrefix seckillPath = new SeckillKeyPrefix("seckillPath", 60);
    public static final SeckillKeyPrefix seckillVerifyCode = new SeckillKeyPrefix("seckillVerifyCode", 300);
}
