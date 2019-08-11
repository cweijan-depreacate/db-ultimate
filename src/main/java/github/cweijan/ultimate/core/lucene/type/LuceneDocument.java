package github.cweijan.ultimate.core.lucene.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cweijan
 * @version 2019/7/16/016 18:23
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LuceneDocument {

    /**
     * 默认搜索的field
     */
    String[] value() default {};


    /**
     * 主键fieldName
     */
    String primaryKeyField();

    /**
     * 所有field默认是否分词
     */
    boolean tokenize() default false;

}
