package top.lw33.seckill.validator;

import org.apache.commons.lang3.StringUtils;
import top.lw33.seckill.utils.ValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author lw
 * @Date 2019-01-17 15:37:41
 **/
public class IsPhoneNumValidator implements ConstraintValidator<IsPhoneNum, String> {

    private boolean required = false;

    @Override
    public void initialize(IsPhoneNum constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!required) {
            if (StringUtils.isEmpty(value)) {
                return true;
            }
        }
        return ValidatorUtil.isPhoneNum(value);
    }
}
