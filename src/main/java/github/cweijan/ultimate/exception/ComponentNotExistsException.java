package github.cweijan.ultimate.exception;

/**
 * @author cweijan
 * @version 2019/9/5 11:31
 * Throw when get nonexists componentInfo in TableInfo
 */
public class ComponentNotExistsException extends RuntimeException{
    public ComponentNotExistsException(String message) {
        super(message);
    }
}
