package top.lw33.seckill.access;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.lw33.seckill.dto.CodeMsg;
import top.lw33.seckill.dto.Result;
import top.lw33.seckill.entity.User;
import top.lw33.seckill.redis.AccessKeyPrefix;
import top.lw33.seckill.redis.RedisService;
import top.lw33.seckill.service.UserService;
import top.lw33.seckill.utils.JsonUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * @Author lw
 * @Date 2019-01-20 21:53:39
 **/
@Component
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        User user = setUser(request, response);
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxcount = accessLimit.maxcount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin) {
                if (user == null) {
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key = user.getId() + ":" + key;
            }
            AccessKeyPrefix accessKeyPrefix = AccessKeyPrefix.withExpire(seconds);
            // 访问次数控制
            Integer accessTimes = redisService.get(accessKeyPrefix, key, Integer.class);
            if (accessTimes == null) {
                redisService.set(accessKeyPrefix, key, 0);
            } else if (accessTimes < maxcount) {
                ++accessTimes;
                redisService.incr(accessKeyPrefix, key);
            } else {
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg codeMsg) throws IOException {
        // 解决乱码问题
        response.setContentType("application/json;charset=utf-8");
        BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        String s = JsonUtil.bean2string(Result.error(codeMsg));
        outputStream.write(s.getBytes("utf-8"));
        outputStream.flush();
        outputStream.close();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        UserContext.remove();
    }


    private User setUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(UserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, UserService.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        User user = userService.getUserByToken(response, token);
        UserContext.set(user);
        return user;
    }

    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        //return Arrays.stream(request.getCookies())
        //        .filter(cookie -> cookie.getName().equals(cookieNameToken)).findAny().get().getValue();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieNameToken)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
