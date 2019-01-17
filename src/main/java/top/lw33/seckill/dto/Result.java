package top.lw33.seckill.dto;

/**
 * @Author lw
 * @Date 2019-01-14 22:54:23
 **/
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;



    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    public static <T> Result<T> success(CodeMsg codeMsg) {
        return new Result<>(codeMsg);
    }

    public static <T> Result<T> error(CodeMsg codeMsg) {
        return new Result<>(codeMsg);
    }

    private Result(T data) {
        this(0, "success", data);
    }

    public Result(CodeMsg codeMsg) {
        if (codeMsg == null) {
            return;
        }
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
