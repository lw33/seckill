package top.lw33.seckill.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.lw33.seckill.dao.UserDao;
import top.lw33.seckill.dto.CodeMsg;
import top.lw33.seckill.entity.User;
import top.lw33.seckill.exception.GlobalException;
import top.lw33.seckill.redis.RedisService;
import top.lw33.seckill.redis.UserKeyPrefix;
import top.lw33.seckill.redis.UserSessionKeyPrefix;
import top.lw33.seckill.utils.MD5Util;
import top.lw33.seckill.utils.UUIDUtil;
import top.lw33.seckill.vo.LoginVo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author lw
 * @Date 2019-01-16 20:53:30
 **/
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;

    public String loginOrRegister(LoginVo loginVo, HttpServletResponse response) {

        long phoneName = Long.parseLong(loginVo.getPhoneNum());
        String formPassword = loginVo.getPassword();
        User user = getUserById(phoneName);
        if (user == null) {
            user = new User();
            user.setId(phoneName);
            String salt = UUIDUtil.uuid().substring(0, 10);
            String password = MD5Util.formPassToDBPass(formPassword, salt);
            user.setPassword(password);
            user.setSalt(salt);
            user.setNickname(String.valueOf(phoneName));
            userDao.insertUser(user);
            redisService.set(UserKeyPrefix.idKey, String.valueOf(phoneName), user);
            logger.info(phoneName + " register...");
        } else {
            String DBPassword = user.getPassword();
            String formPassToDBPass = MD5Util.formPassToDBPass(formPassword, user.getSalt());
            if (!DBPassword.equals(formPassToDBPass)) {
                throw new GlobalException(CodeMsg.PASSWORD_ERROR);
            }
            logger.info(phoneName + " login...");
        }
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return token;
    }


    public User getUserById(long id) {
        User user = redisService.get(UserKeyPrefix.idKey, String.valueOf(id), User.class);
        if (user != null) {
            return user;
        }
        user = userDao.selectUserById(id);
        if (user != null) {
            redisService.set(UserKeyPrefix.idKey, String.valueOf(id), user);
        }
        return user;
    }
    public User getUserByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        User user = redisService.get(UserSessionKeyPrefix.tokenKey, token, User.class);
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;
    }

    public boolean updatePassword(String token, long id, String password) {
        User user = getUserById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.PHONE_NUM_NOT_EXIST);
        }
        password = MD5Util.formPassToDBPass(password, user.getSalt());
        if (user.getPassword().equals(password)) {
            return true;
        }
        userDao.updateUserPassword(id, password);
        user.setPassword(password);
        redisService.set(UserSessionKeyPrefix.tokenKey, token, user);
        redisService.delete(UserKeyPrefix.idKey, String.valueOf(id));
        return true;
    }

    private void addCookie(HttpServletResponse response, String token, User user) {
        redisService.set(UserSessionKeyPrefix.tokenKey, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(UserSessionKeyPrefix.tokenKey.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }


}
