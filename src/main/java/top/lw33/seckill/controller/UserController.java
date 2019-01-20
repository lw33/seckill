package top.lw33.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.lw33.seckill.dto.Result;
import top.lw33.seckill.entity.User;
import top.lw33.seckill.service.UserService;

/**
 * @Author lw
 * @Date 2019-01-16 20:53:44
 **/
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<User> info(User user) {
        return Result.success(user);
    }
}
