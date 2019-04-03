package io.watchdog.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class MobilePhoneValidator implements ConstraintValidator<MobilePhone, CharSequence> {

    private static final Pattern MOBILE_REGEX = Pattern.compile(
            "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0-2,5-9])|(177))\\d{8}$"
    );

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        if (value.length() != 11 ) {
            return false;
        }
        return MOBILE_REGEX.matcher(value).matches();
    }
}
