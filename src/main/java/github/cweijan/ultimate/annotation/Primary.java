package github.cweijan.ultimate.annotation;

import kotlin.ReplaceWith;

import java.lang.annotation.*;

/**
 主键注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Primary {

    String value() default "";

    boolean autoIncrement() default true;

    String comment() default "";


    int length() default 0;


}
