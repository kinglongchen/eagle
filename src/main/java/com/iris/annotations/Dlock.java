package com.iris.annotations;

import java.lang.annotation.*;

/**
 * Created by chenjinlong on 17/4/26.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Dlock {
    String value() default "";
}
