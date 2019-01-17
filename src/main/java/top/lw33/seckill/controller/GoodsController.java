package top.lw33.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author lw
 * @Date 2019-01-17 17:07:04
 **/
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @RequestMapping("/to_list")
    public String toList() {
        return "goods_list";
    }
}
