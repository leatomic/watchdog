package io.watchdog.security.web.verification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TokenRequest {
    private int codeLength;
    private Duration codeValidityDuration;
}
