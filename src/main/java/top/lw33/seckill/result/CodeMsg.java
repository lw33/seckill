package top.lw33.seckill.result;

/**
 * @Author lw
 * @Date 2019-01-14 23:04:13
 **/
public class CodeMsg {

    private Integer code;
    private String msg;

    // 通用消息
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");

    // 登入模块 5002xx

    // 商品模块 5003xx

    // 订单模块 5004xx

    // 秒杀模块 5005xx

    public CodeMsg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
