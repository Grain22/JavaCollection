package org.grain.web.spring.framework.annotation;


import java.lang.annotation.*;

/**
 * 容器管理类
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GController {
    String value() default "";
}
