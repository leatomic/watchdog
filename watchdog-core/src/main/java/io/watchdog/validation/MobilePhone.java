package io.watchdog.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * @author le
 * @since v0.1.0
 */
@Target({FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MobilePhoneValidator.class)
public @interface MobilePhone {

    String message() default "{io.watchdog.validation.constraints.MobilePhone.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String regexp() default ".*";

}
