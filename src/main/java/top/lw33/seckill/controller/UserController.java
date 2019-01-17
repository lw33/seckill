package top.lw33.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import top.lw33.seckill.service.UserService;

/**
 * @Author lw
 * @Date 2019-01-16 20:53:44
 **/
@Controller
public class UserController {

    @Autowired
    private UserService userService;


}
