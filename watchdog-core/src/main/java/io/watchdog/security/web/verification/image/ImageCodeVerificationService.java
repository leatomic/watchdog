package io.watchdog.security.web.verification.image;

import io.watchdog.security.verification.TokenService;
import io.watchdog.security.web.verification.TokenRepository;
import io.watchdog.security.web.verification.VerificationService;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.Map;

@Setter @Getter
public class ImageCodeVerificationService extends VerificationService<ImageCodeTokenRequest, ImageCode> {

    public static final String SUPPORTED_TOKEN_TYPE = "image_code";

    public static final int DEFAULT_CODE_LENGTH = 4;
    public static final Duration DEFAULT_CODE_VALIDITY_DURATION = Duration.ofMinutes(15);

    private int defaultImageWidth = 90,
                defaultImageHeight = 34;

    public ImageCodeVerificationService(String forBusiness,
                                        TokenService<ImageCodeTokenRequest, ImageCode> service,
                                        TokenRepository<ImageCode> tokenRepository) {
        super(SUPPORTED_TOKEN_TYPE, forBusiness,
                DEFAULT_CODE_LENGTH, DEFAULT_CODE_VALIDITY_DURATION,
                service, tokenRepository);
    }

    public ImageCodeVerificationService(TokenService<ImageCodeTokenRequest, ImageCode> service,
                                        TokenRepository<ImageCode> tokenRepository) {
        this(DEFAULT_BUSINESS, service, tokenRepository);
    }

    @Override
    protected ImageCodeTokenRequest createRequest(int codeLength, Duration codeValidityDuration,
                                                  Map<String, String[]> parameterMap) {

        String[]    strings = parameterMap.get("image-width");
        int imageWidth  = (strings != null && strings.length > 0) ? Integer.parseInt(strings[0]) : defaultImageWidth;

                    strings = parameterMap.get("image-height");
        int imageHeight = (strings != null && strings.length > 0) ? Integer.parseInt(strings[0]) : defaultImageHeight;

        return ImageCodeTokenRequest.builder()
                .codeLength(codeLength)
                .codeValidityDuration(codeValidityDuration)
                .imageWidth(imageWidth)
                .imageHeight(imageHeight)
                .build();
    }
}
