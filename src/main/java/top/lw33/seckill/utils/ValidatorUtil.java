package top.lw33.seckill.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @Author lw
 * @Date 2019-01-16 11:22:50
 **/
public class ValidatorUtil {

    private static final String PHONE_NUM_STR_PATTERN = "1\\d{10}";
    private static final String PHONE_NUM_STR_PATTERN_TRUE = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";

    private static final Pattern PHONE_NUM_PATTERN = Pattern.compile(PHONE_NUM_STR_PATTERN);

    public static boolean isPhoneNum(String src) {
        if (StringUtils.isEmpty(src)) {
            return false;
        }
        return PHONE_NUM_PATTERN.matcher(src).matches();
    }

    public static void main(String[] args) {
        System.out.println(isPhoneNum("18279127912"));
    }
}
