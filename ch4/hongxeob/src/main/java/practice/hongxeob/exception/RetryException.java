package practice.hongxeob.exception;

public class RetryException extends RuntimeException {
    public RetryException(String message) {
        super(message);
    }
}
