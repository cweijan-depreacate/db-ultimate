package github.cweijan.ultimate.exception;

/**
 * @author cweijan
 * @version 2019/9/5 11:32
 * When invoke update but primary value is null.
 */
public class PrimaryValueNotSetException extends RuntimeException {
    public PrimaryValueNotSetException(String message) {
        super(message);
    }
}
