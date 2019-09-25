package github.cweijan.ultimate.core;

import java.io.Serializable;

/**
 * @author cweijan
 * @version 2019/9/25 14:37
 */
@FunctionalInterface
public interface FieldQuery<T> extends Serializable {

    Object get(T entity);

}
