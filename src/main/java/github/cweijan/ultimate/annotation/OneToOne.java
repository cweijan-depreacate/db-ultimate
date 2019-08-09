package github.cweijan.ultimate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cweijan
 * @version 2019/8/7 14:46
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToOne {
    /**
     要关联的表实体Class,默认关联主键
     */
    Class<?> value();

    /**
     外键名称
     */
    String joinColumn() default "";

}
