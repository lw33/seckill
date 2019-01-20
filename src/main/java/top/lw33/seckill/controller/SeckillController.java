package top.lw33.seckill.controller;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.lw33.seckill.access.AccessLimit;
import top.lw33.seckill.dto.CodeMsg;
import top.lw33.seckill.dto.Result;
import top.lw33.seckill.entity.OrderInfo;
import top.lw33.seckill.entity.User;
import top.lw33.seckill.rabbitmq.MQSender;
import top.lw33.seckill.rabbitmq.SeckillMessage;
import top.lw33.seckill.redis.GoodsKeyPrefix;
import top.lw33.seckill.redis.OrderKeyPrefix;
import top.lw33.seckill.redis.RedisService;
import top.lw33.seckill.redis.SeckillKeyPrefix;
import top.lw33.seckill.service.GoodsService;
import top.lw33.seckill.service.OrderService;
import top.lw33.seckill.service.SeckillService;
import top.lw33.seckill.vo.GoodsVo;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * @Author lw
 * @Date 2019-01-17 20:39:16
 **/
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MQSender mqSender;

    // 内存标记 如果set中存在 商品 id 则证明商品已卖完
    private HashSet<Long> localOverSet = new HashSet<>();


    /**
     * Redis 预减库存
     * 将商品数量加载进入 redis
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.getGoodsVoList();
        if (goodsVoList != null) {
            for (GoodsVo goodsVo : goodsVoList) {
                redisService.set(GoodsKeyPrefix.seckillGoodsCount, String.valueOf(goodsVo.getId()), goodsVo.getStockCount());
            }
        }
    }

    /**
     * 返回一个随机字符串作为路径
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/path")
    @ResponseBody
    @AccessLimit
    public Result<String> getSeckillPath(HttpServletRequest request, User user, @RequestParam("goodsId") Long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

     /*   // 访问次数控制
        String requestURI = request.getRequestURI();
        Integer accessTimes = redisService.get(AccessKeyPrefix.accessTimes, user.getId() + ":" + requestURI, Integer.class);
        if (accessTimes == null) {
            accessTimes = 0;
        }
        if (accessTimes < 5) {
            redisService.set(AccessKeyPrefix.accessTimes, user.getId() + ":" + requestURI, accessTimes + 1);
        } else {
            return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
        }*/

        String path = seckillService.createSeckillPath(user.getId(), goodsId);
        return Result.success(path);
    }

    /**
     * 生成验证码返回
     *
     * @param response
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/verifyCode")
    @ResponseBody
    public Result<String> verifyCode(HttpServletResponse response, User user, @RequestParam("goodsId") Long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        BufferedImage bufferedImage = seckillService.createVerifyCodeImg(user.getId(), goodsId);
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            ImageIO.write(bufferedImage, "JPEG", outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }
        return null;
    }

    /**
     * final
     *
     * @param user
     * @param path
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/do_seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doSeckill2(User user, @PathVariable("path") String path,
                                      @RequestParam("goodsId") Long goodsId,
                                      @RequestParam("verifyCode") Integer verifyCode) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        boolean pathValid = seckillService.checkPath(user.getId(), goodsId, path);
        if (!pathValid) {
            return Result.error(CodeMsg.PATH_ERROR);
        }

        boolean verifyCodeValid = seckillService.checkVerifyCode(user.getId(), goodsId, verifyCode);
        if (!verifyCodeValid) {
            return Result.error(CodeMsg.VERIFY_CODE_ERROR);
        }

        // 内存标记 如果set中存在 商品 id 则证明商品已卖完
        if (localOverSet.contains(goodsId)) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        // 预减库存
        long stockCount = redisService.decr(GoodsKeyPrefix.seckillGoodsCount, String.valueOf(goodsId));
        // 判断库存
        if (stockCount <= 0) {
            // 添加内存标记 卖完
            localOverSet.add(goodsId);
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        // 判断是否已秒过
        OrderInfo order = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);

        if (order != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }

        // 使用消息队列发送添加订单信息
        SeckillMessage seckillMessage = new SeckillMessage(user.getId(), goodsId);
        mqSender.sendSeckillMessage(seckillMessage);
        return Result.success(0);
    }

    /**
     * 10000   256.5
     * 2000*5  QPS
     * 页面静态化
     *
     * @param user
     * @param goodsId
     * @return
     */
    @Deprecated
    @RequestMapping(value = "/do_seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doSeckill2(User user, @RequestParam("goodsId") Long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 内存标记 如果set中存在 商品 id 则证明商品已卖完
        if (localOverSet.contains(goodsId)) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        // 预减库存
        long stockCount = redisService.decr(GoodsKeyPrefix.seckillGoodsCount, String.valueOf(goodsId));
        // 判断库存
        if (stockCount <= 0) {
            // 添加内存标记 卖完
            localOverSet.add(goodsId);
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        // 判断是否已秒过
        OrderInfo order = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);

        if (order != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }

        // 使用消息队列发送添加订单信息
        SeckillMessage seckillMessage = new SeckillMessage(user.getId(), goodsId);
        mqSender.sendSeckillMessage(seckillMessage);
        return Result.success(0);
    }

    /**
     * 10000 145.2
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @Deprecated
    @RequestMapping("/do_seckill1")
    public String doSeckill1(Model model, User user, @RequestParam("goodsId") Long goodsId) {
        if (user == null) {
            return "login";
        }

        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        int stockCount = goods.getStockCount();
        // 判断库存
        if (stockCount <= 0) {
            model.addAttribute("errmsg", CodeMsg.SECKILL_OVER.getMsg());
            return "seckill_fail";
        }

        // 判断是否已秒过
        OrderInfo order = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);

        if (order != null) {
            model.addAttribute("errmsg", CodeMsg.REPEATE_SECKILL.getMsg());
            return "seckill_fail";
        }

        // 开始秒杀
        order = seckillService.seckill(user, goods);
        model.addAttribute("goods", goods);
        model.addAttribute("orderInfo", order);
        return "order_detail";
    }

    @RequestMapping("/result/{goodsId}")
    @ResponseBody
    public Result<Long> result(@PathVariable("goodsId") String goodsId, User user) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // 0 排队 -1 失败 >0 订单号
        long status = seckillService.getSeckillStatus(user.getId(), goodsId);
        return Result.success(status);
    }


    /**
     * 重置环境
     *
     * @return
     */
    @RequestMapping("/reset")
    @ResponseBody
    public Result<Boolean> reset() {
        List<GoodsVo> goodsList = goodsService.getGoodsVoList();
        // 清除内存标记 重置库存
        for (GoodsVo goods : goodsList) {
            goods.setStockCount(9999);
            redisService.set(GoodsKeyPrefix.seckillGoodsCount, String.valueOf(goods.getId()), goods.getStockCount());
            localOverSet.remove(goods.getId());
        }

        // 删除订单信息
        redisService.delete(OrderKeyPrefix.orderGoodIdUserId);

        // 删除标记秒杀结束的所有 key
        redisService.delete(SeckillKeyPrefix.isGoodsOver);

        seckillService.reset(goodsList);
        return Result.success(true);
    }
}
