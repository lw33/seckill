package top.lw33.seckill.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.lw33.seckill.entity.OrderInfo;
import top.lw33.seckill.entity.User;
import top.lw33.seckill.redis.RedisService;
import top.lw33.seckill.redis.SeckillKeyPrefix;
import top.lw33.seckill.utils.UUIDUtil;
import top.lw33.seckill.vo.GoodsVo;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
public class SeckillService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisService redisService;


    @Transactional
    public OrderInfo seckill(User user, GoodsVo goods) {
        goodsService.reduceStock(goods);
        return orderService.createOrder(user, goods);
    }

    @Transactional
    public OrderInfo seckill(long userId, GoodsVo goods) {
        boolean success = goodsService.reduceStock(goods);
        if (success) {
            return orderService.createOrder(userId, goods);
        } else {
            setGoodsOvers(goods.getId());
            return null;
        }
    }

    private void setGoodsOvers(Long id) {
        redisService.set(SeckillKeyPrefix.isGoodsOver, String.valueOf(id), true);
    }

    // 0 排队  -1 失败  订单号
    public long getSeckillStatus(Long id, String goodsId) {
        OrderInfo orderInfo = orderService.getSeckillOrderByUserIdAndGoodsId(id, Long.parseLong(goodsId));
        if (orderInfo != null) {
            return orderInfo.getId();
        } else {
            if (getGoodsOver(goodsId)) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private boolean getGoodsOver(String goodsId) {
        return redisService.exists(SeckillKeyPrefix.isGoodsOver, goodsId);
    }

    public void reset(List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }

    public String createSeckillPath(Long id, Long goodsId) {
        String path = UUIDUtil.uuid();
        redisService.set(SeckillKeyPrefix.seckillPath, id + ":" + goodsId, path);
        return path;
    }

    public boolean checkPath(Long id, Long goodsId, String path) {
        if(path == null) {
            return false;
        }
        String pathCache = redisService.get(SeckillKeyPrefix.seckillPath, id + ":" + goodsId, String.class);
        return Objects.equals(pathCache, path);
    }

    public BufferedImage createVerifyCodeImg(Long userId, Long goodsId) {
        if(userId == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(SeckillKeyPrefix.seckillVerifyCode, userId+":"+goodsId, rnd);
        //输出图片
        return image;
    }

    private int calc(String verifyCode) {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine javascript = engineManager.getEngineByName("javascript");
        try {
            Object eval = javascript.eval(verifyCode);
            return (Integer) eval;
        } catch (ScriptException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Test
    public void test() {
        System.out.println(calc("1+8*2"));
    }

    char[] opts = {'+', '-', '*'};

    private String generateVerifyCode(Random rdm) {

        int n1 = rdm.nextInt(10);
        int n2 = rdm.nextInt(10);
        int n3 = rdm.nextInt(10);
        char opt1 = opts[rdm.nextInt(3)];
        char opt2 = opts[rdm.nextInt(3)];
        StringBuilder sb = new StringBuilder();
        sb.append(n1);
        sb.append(opt1);
        sb.append(n2);
        sb.append(opt2);
        sb.append(n3);

        return sb.toString();
    }


    public boolean checkVerifyCode(Long userId, Long goodsId, Integer verifyCode) {
        if (verifyCode == null) {
            return false;
        }
        // 从缓存中取出验证码的结果
        Integer verifyCodeCache = redisService.get(SeckillKeyPrefix.seckillVerifyCode, userId + ":" + goodsId, Integer.class);
        boolean equals = Objects.equals(verifyCode, verifyCodeCache);
        if (equals) {
            // 如果此次验证成功 则将验证码从缓存中删去
            redisService.delete(SeckillKeyPrefix.seckillVerifyCode, userId + ":" + goodsId);
        }
        return equals;
    }
}
