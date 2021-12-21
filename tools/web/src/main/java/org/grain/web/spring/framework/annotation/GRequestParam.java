package org.grain.web.spring.framework.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GRequestParam {
    String value() default "";
}
