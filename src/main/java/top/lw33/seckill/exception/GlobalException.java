package top.lw33.seckill.exception;

import top.lw33.seckill.dto.CodeMsg;

/**
 * @Author lw
 * @Date 2019-01-16 12:01:27
 **/
public class GlobalException extends RuntimeException {

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
