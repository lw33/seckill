package top.lw33.seckill.utils;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Author lw
 * @Date 2019-01-17 14:25:17
 **/
public class MD5Util {


    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    public static String inputPassToFormPass(String inputPass) {
        return md5(addSalt(inputPass, salt));
    }

    public static String formPassToDBPass(String formPass, String salt) {
        String pass = addSalt(formPass, salt);
        return md5(pass);
    }

    public static String inputPassToDBPass(String inputPass, String salt) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, salt);
        return dbPass;
    }

    public static String addSalt(String passWord, String salt) {
        String str = ""+salt.charAt(0)+salt.charAt(2) + passWord +salt.charAt(5) + salt.charAt(4);
        return str;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("123456"));//d3b1294a61a07da9b49b6e22b2cbd7f9
    }

}
