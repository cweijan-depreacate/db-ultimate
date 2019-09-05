package github.cweijan.ultimate.exception;

/**
 * @author cweijan
 * @version 2019/9/5 11:30
 * Throw when get nonexists column in componentInfo
 */
public class ColumnNotExistsException extends RuntimeException{
    public ColumnNotExistsException(String message) {
        super(message);
    }
}
