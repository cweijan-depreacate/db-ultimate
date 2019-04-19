package github.cweijan.ultimate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 配置在field上面,表明不参与实体映射
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Exclude{
    boolean excludeInsert=true;
    boolean excludeDelete=true;
    boolean excludeUpdate=true;
    boolean excludeResult=true;
}
