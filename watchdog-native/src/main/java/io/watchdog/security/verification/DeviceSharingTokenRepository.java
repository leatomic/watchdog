package io.watchdog.security.verification;

public abstract class DeviceSharingTokenRepository<T extends VerificationToken> implements TokenRepository<T> {

    private String keyPrefix;
    private static final String deviceIdPrefix = "device-";

    public DeviceSharingTokenRepository(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    protected abstract String obtainDeviceId();

    private String getKey() {
        return keyPrefix + ":" + deviceIdPrefix + obtainDeviceId();
    }
}
