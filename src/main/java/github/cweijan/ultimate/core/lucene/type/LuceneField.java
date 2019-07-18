package github.cweijan.ultimate.core.lucene.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cweijan
 * @version 2019/7/16/016 16:55
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LuceneField {

    boolean store() default true;

    boolean index() default true;

    boolean tokenize() default false;
}
