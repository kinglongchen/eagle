package com.iris.annotations;

import java.lang.annotation.*;

/**
 * Created by chenjinlong on 17/4/26.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LockKey {
}