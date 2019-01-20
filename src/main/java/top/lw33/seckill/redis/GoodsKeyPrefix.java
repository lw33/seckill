package top.lw33.seckill.redis;

/**
 * @Author lw
 * @Date 2019-01-16 10:35:09
 **/
public class GoodsKeyPrefix extends BaseKeyPrefix{

    public static final int EXPIRE_SEC = 60;

    private GoodsKeyPrefix(String prefix, int expireSeconds) {
        super(prefix, expireSeconds);
    }

    public static final GoodsKeyPrefix goodsList = new GoodsKeyPrefix("goodsList", EXPIRE_SEC);
    public static final GoodsKeyPrefix goodsDetail = new GoodsKeyPrefix("goodsDetail", EXPIRE_SEC);
    public static final GoodsKeyPrefix seckillGoodsCount = new GoodsKeyPrefix("seckillGoodsCount", 0);
}
