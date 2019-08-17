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
     * 调用搜索方法时默认搜索的Field
     * @see  github.cweijan.ultimate.core.lucene.LuceneQuery#searchFull(Object)
     */
    String[] value() default {};

    /**
     * 所有field默认是否分词
     */
    boolean tokenize() default false;

}
