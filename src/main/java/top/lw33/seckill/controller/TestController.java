package top.lw33.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.lw33.seckill.dto.CodeMsg;
import top.lw33.seckill.dto.Result;
import top.lw33.seckill.rabbitmq.MQSender;
import top.lw33.seckill.redis.RedisService;

/**
 * @Author lw
 * @Date 2019-01-14 22:11:04
 **/

@Controller
public class TestController {

    @Autowired
    private RedisService redisService;

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "success";
    }

    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello() {
        return Result.success("hello world");
    }



    @RequestMapping("/testerror")
    @ResponseBody
    public Result<String> error() {
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/thy")
    public String thy(Model model) {
        model.addAttribute("name", "java");
        return "hello";
    }

    @Autowired
    private MQSender mqSender;

    @RequestMapping("/rmq/{msg}")
    @ResponseBody
    public Result<String> rmq(@PathVariable("msg")String msg) {
        mqSender.send(msg);
        return Result.success(msg);
    }

   /* @RequestMapping("/get/{key}")
    @ResponseBody
    public Result<String> get(@PathVariable("key") String key) {
        //String s = redisService.get(key, String.class);
        return Result.success(s);
    }


    @RequestMapping("/set/{key}/{value}")
    @ResponseBody
    public Result<String> set(@PathVariable("key") String key, @PathVariable("value") String value) {
        boolean set = redisService.set(key, value);
        return Result.success(String.valueOf(set));
    }*/
}
