package github.cweijan.ultimate.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table{

    /**
     表名称
     */
    String value() default "";

    /**
     默认查询列
     */
    String selectColumns() default "*";

    /**
     默认表别名
     */
    String alias() default "";

    /**
     自动将驼峰field映射为下划线Column
     */
    boolean camelcaseToUnderLine() default true;
}
