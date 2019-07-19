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
     * 需要搜索的field,也可以通过单独通过{@link LuceneField}进行配置
     */
    String[] value();


    /**
     * 主键fieldName
     */
    String primaryKeyField() default "";

    /**
     * field默认是否分词
     */
    boolean tokenize() default false;

}
