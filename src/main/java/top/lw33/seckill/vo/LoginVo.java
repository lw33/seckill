package top.lw33.seckill.vo;

import org.hibernate.validator.constraints.Length;
import top.lw33.seckill.validator.IsPhoneNum;

import javax.validation.constraints.NotNull;

/**
 * @Author lw
 * @Date 2019-01-16 11:18:02
 **/
public class LoginVo {

    @NotNull
    @IsPhoneNum
    private String phoneNum;

    @NotNull
    @Length(min = 32)
    private String password;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginVo{" +
                "phoneNum='" + phoneNum + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
