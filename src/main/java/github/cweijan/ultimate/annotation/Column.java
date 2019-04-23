package github.cweijan.ultimate.annotation;

import java.lang.annotation.*;

/**
 对field添加更多描述
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column{

    String value() default "";

    String comment() default "";

    String defaultValue() default "";

    boolean nullable() default false;

    int length() default 0;

}
