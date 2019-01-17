package top.lw33.seckill.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Author lw
 * @Date 2019-01-16 11:47:51
 **/
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {IsPhoneNumValidator.class})
public @interface IsPhoneNum {

    boolean required() default true;

    String message() default "{手机号码格式错误}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
