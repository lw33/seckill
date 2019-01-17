package top.lw33.seckill.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @Author lw
 * @Date 2019-01-15 22:25:57
 **/
public class JsonUtil {

    public static <T> T string2bean(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        if (clazz == String.class) {
            return (T) str;
        }
        if (CommonUtils.isWrapClass(clazz)){
            try {
                Method valueOf = clazz.getMethod("valueOf", String.class);
                return (T) valueOf.invoke(null, str);
            } catch (Exception e) {
                return null;
            }
        }
        return JSON.parseObject(str, clazz);
    }

    public static <T> String bean2string(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (CommonUtils.isWrapOrBasicClass(clazz) || clazz == String.class) {
            return String.valueOf(value);
        }
        return JSON.toJSONString(value);
    }


    @Test
    public void test() throws IllegalAccessException, InstantiationException {
        System.out.println(string2bean("1.1", Double.class));
        System.out.println(string2bean("javac", String.class));
        Integer integer = string2bean("1.1", int.class);
        System.out.println(integer);
    }



}
