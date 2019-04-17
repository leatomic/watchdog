package io.watchdog.security.verification;

/**
 * <p>token服务运行期间（主要包括生成token、以及校验token），由服务器内部导致的异常。区别于{@link TokenServiceException}。<p/>
 */
public class InternalTokenServiceException extends TokenServiceException {

    public InternalTokenServiceException(String message) {
        super(message);
    }

    public InternalTokenServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
