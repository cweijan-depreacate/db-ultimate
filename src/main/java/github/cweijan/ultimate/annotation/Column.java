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

    boolean unique() default false;

    boolean nullable() default false;

    String excelHeader() default "";

    int length() default 0;

}
