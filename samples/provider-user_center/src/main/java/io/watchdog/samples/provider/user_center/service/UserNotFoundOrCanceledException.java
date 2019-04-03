package io.watchdog.samples.provider.user_center.service;

/**
 * @author le
 * @since v0.1.0
 */
public class UserNotFoundOrCanceledException extends BusinessException {

    private static final int CODE = 0x002;

    private final Long invalidId;

    public UserNotFoundOrCanceledException(Long invalidId) {
        super(CODE, "user not found or canceled for id " + invalidId + "!");
        this.invalidId = invalidId;
    }

    public Long getInvalidId() {
        return invalidId;
    }

}
