package top.lw33.seckill.exception;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import top.lw33.seckill.dto.CodeMsg;
import top.lw33.seckill.dto.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author lw
 * @Date 2019-01-16 11:53:28
 **/
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e) {
        e.printStackTrace();
        if (e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return Result.error(ex.getCodeMsg());
        } else if (e instanceof BindException) {
            BindException be = (BindException) e;
            ObjectError objectError = be.getAllErrors().get(0);
            return Result.error(CodeMsg.BIND_ERROR.withArgs(objectError.getDefaultMessage()));
        } else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }

}
