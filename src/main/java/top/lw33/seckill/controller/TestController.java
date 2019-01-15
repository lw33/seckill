package top.lw33.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.lw33.seckill.result.CodeMsg;
import top.lw33.seckill.result.Result;

/**
 * @Author lw
 * @Date 2019-01-14 22:11:04
 **/

@Controller
public class TestController {

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
}
