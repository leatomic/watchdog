package io.watchdog.security.web.verification.impl.image;

import io.watchdog.security.web.verification.VerificationToken;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter @Setter
public class ImageCode extends VerificationToken {

    private int imageWidth, imageHeight;

    public ImageCode(String key, Duration expireIn, int imageWidth, int imageHeight) {
        super(key, expireIn);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public ImageCode(String key, int seconds, int imageWidth, int imageHeight) {
        super(key, seconds);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

}
