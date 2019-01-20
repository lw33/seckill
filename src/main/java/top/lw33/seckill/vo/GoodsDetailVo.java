package top.lw33.seckill.vo;

import top.lw33.seckill.entity.User;

/**
 * @Author lw
 * @Date 2019-01-19 21:51:42
 **/
public class GoodsDetailVo {

    private int remainSeconds = 0;
    private GoodsVo goods;
    private User user;

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
