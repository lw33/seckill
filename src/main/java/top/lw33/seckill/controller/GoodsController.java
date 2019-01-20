package top.lw33.seckill.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import top.lw33.seckill.dto.Result;
import top.lw33.seckill.entity.User;
import top.lw33.seckill.redis.GoodsKeyPrefix;
import top.lw33.seckill.redis.RedisService;
import top.lw33.seckill.service.GoodsService;
import top.lw33.seckill.service.UserService;
import top.lw33.seckill.vo.GoodsDetailVo;
import top.lw33.seckill.vo.GoodsVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author lw
 * @Date 2019-01-17 17:07:04
 **/
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisService redisService;


    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 10000 337.6
     * 10000 413.6 页面缓存
     *
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, User user) {

        model.addAttribute("user", user);
        String html = redisService.get(GoodsKeyPrefix.goodsList, "", String.class);

        if (StringUtils.isEmpty(html)) {

            List<GoodsVo> goodsVoList = goodsService.getGoodsVoList();
            model.addAttribute("goodsList", goodsVoList);
            html = process(request, response, model, "goods_list");
            if (StringUtils.isNotEmpty(html)) {
                redisService.set(GoodsKeyPrefix.goodsList, "", html);
            }
        }

        return html;
    }

    /**
     * 静态化页面
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> toDetail(@PathVariable("goodsId") Long goodsId, User user) {
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        long startTime = goodsVo.getStartDate().getTime();
        long endTime = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();

        // 剩下多少秒
        int remainSeconds = 0;

        // 未开始
        if (now < startTime) {
            remainSeconds = (int) ((startTime - now) / 1000);
            // 已结束
        } else if (now > endTime) {
            remainSeconds = -1;
            // 进行中
        }
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoods(goodsVo);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setUser(user);
        return Result.success(goodsDetailVo);
    }

    /**
     * 页面缓存
     *
     * @param request
     * @param response
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
    @ResponseBody
    public String toDetail2(HttpServletRequest request, HttpServletResponse response, Model model, User user, @PathVariable("goodsId") Long goodsId) {

        model.addAttribute("user", user);

        String html = redisService.get(GoodsKeyPrefix.goodsDetail, String.valueOf(goodsId), String.class);
        if (StringUtils.isEmpty(html)) {

            GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
            model.addAttribute("goods", goodsVo);

            long startTime = goodsVo.getStartDate().getTime();
            long endTime = goodsVo.getEndDate().getTime();
            long now = System.currentTimeMillis();

            // 秒杀的状态 0 未开始 1 进行中 2 结束
            int seckillStatus = 0;
            // 剩下多少秒
            int remainSeconds = 0;

            // 未开始
            if (now < startTime) {
                remainSeconds = (int) ((startTime - now) / 1000);
                // 已结束
            } else if (now > endTime) {
                seckillStatus = 2;
                remainSeconds = -1;
                // 进行中
            } else {
                seckillStatus = 1;
            }
            model.addAttribute("seckillStatus", seckillStatus);
            model.addAttribute("remainSeconds", remainSeconds);
            html = process(request, response, model, "goods_detail");
            if (StringUtils.isNotEmpty(html)) {
                redisService.set(GoodsKeyPrefix.goodsDetail, "", html);
            }
        }
        return html;
    }

    // 使用模板引擎解析页面
    private String process(HttpServletRequest request, HttpServletResponse response, Model model, String templates) {
        String html;
        WebContext ctx = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process(templates, ctx);
        return html;
    }
}
