package github.cweijan.ultimate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 外键注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey{

    /**
     要关联的表实体Class,默认关联主键
     */
    Class<?> value();

    /**
     外键名称
     */
    String joinColumn() default "";

    /**
     是否自动关联查询
     */
    boolean autoJoin() default false;

}
