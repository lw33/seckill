package top.lw33.seckill.utils;

/**
 * @Author lw
 * @Date 2019-01-15 22:51:39
 **/
public class CommonUtils {
    public static boolean isWrapOrBasicClass(Class clz) {
        return isBasicClass(clz) || isWrapClass(clz);
    }

    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBasicClass(Class clz) {
        return clz.isPrimitive();
    }
}
