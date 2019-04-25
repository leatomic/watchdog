package io.watchdog.security.web.verification.image;

import io.watchdog.security.web.verification.TokenRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter @Setter
public class ImageCodeTokenRequest extends TokenRequest {
    int imageWidth;
    int imageHeight;

    @Builder
    public ImageCodeTokenRequest(int codeLength, Duration codeValidityDuration,
                                 int imageWidth, int imageHeight) {
        super(codeLength, codeValidityDuration);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }
}
