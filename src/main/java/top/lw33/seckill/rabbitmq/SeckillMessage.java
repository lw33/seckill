package top.lw33.seckill.rabbitmq;

/**
 * @Author lw
 * @Date 2019-01-20 13:46:46
 **/
public class SeckillMessage {

    private long userId;
    private long goodsId;

    public SeckillMessage() {
    }

    public SeckillMessage(long userId, long goodsId) {
        this.userId = userId;
        this.goodsId = goodsId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
