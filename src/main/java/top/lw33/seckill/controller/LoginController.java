package top.lw33.seckill.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.lw33.seckill.dto.Result;
import top.lw33.seckill.redis.RedisService;
import top.lw33.seckill.service.UserService;
import top.lw33.seckill.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @Author lw
 * @Date 2019-01-16 20:55:26
 **/
@Controller
@RequestMapping("/login")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login_register")
    @ResponseBody
    public Result<String> doLoginOrRegister(@Valid LoginVo loginVo, HttpServletResponse response) {
        String token = userService.loginOrRegister(loginVo, response);
        return Result.success(token);
    }
}
