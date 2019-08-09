package github.cweijan.ultimate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 给Field配置该注解,插入操作时Field为空会插入当前时间,默认格式为yyyy-MM-dd HH:mm:ss
 * @author cweijan
 * @version 2019/8/9 10:33
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreateDate {
    /**
     * 日期格式
     */
    String value() default "yyyy-MM-dd HH:mm:ss";
}
