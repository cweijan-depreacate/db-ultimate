package github.cweijan.ultimate.annotation;

/**
 * @author cweijan
 * @version 2019/8/7 14:46
 */
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
