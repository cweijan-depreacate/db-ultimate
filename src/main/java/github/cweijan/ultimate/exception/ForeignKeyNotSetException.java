package github.cweijan.ultimate.exception;

/**
 * @author cweijan
 * @version 2019/9/5 11:32
 */
public class ForeignKeyNotSetException extends RuntimeException {
    public ForeignKeyNotSetException(String message) {
        super(message);
    }
}
