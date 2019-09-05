package github.cweijan.ultimate.exception;

/**
 * @author cweijan
 * @version 2019/8/9 14:17
 * When invoke method need primary key but not found.
 */
public class PrimaryKeyNotExistsException extends RuntimeException{
    public PrimaryKeyNotExistsException(String s) {
        super(s);
    }
}
