package io.watchdog.security.web.verification.impl.image;

import io.watchdog.security.web.verification.GeneralTokenService;
import io.watchdog.security.web.verification.TokenRepository;

import java.time.Duration;
import java.util.Map;
import java.util.Random;

public class ImageCodeService extends GeneralTokenService<ImageCode> {

    private int defaultImageWidth, defaultImageHeight;

    protected final Random random = new Random();

    public ImageCodeService(int codeLength,
                            Duration codeValidityDuration,
                            TokenRepository<ImageCode> tokenRepository,
                            int defaultImageWidth, int defaultImageHeight) {
        super(codeLength, codeValidityDuration, tokenRepository);
        this.defaultImageWidth = defaultImageWidth;
        this.defaultImageHeight = defaultImageHeight;
    }

    @Override
    public ImageCode createToken(Map<String, String[]> params) {

        String seq = generateCodeSeq(getCodeLength());

        String[]    strings = params.get("image-width");
        int imageWidth  = (strings != null && strings.length > 0) ? Integer.parseInt(strings[0]) : defaultImageWidth;

                    strings = params.get("image-height");
        int imageHeight = (strings != null && strings.length > 0) ? Integer.parseInt(strings[0]) : defaultImageHeight;

        return new ImageCode(seq, getCodeValidityDuration(), imageWidth, imageHeight);

    }

    private  String generateCodeSeq(int length) {
        char[] seq = new char[length];
        char[] codes = { 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'g', 'G',
                'h', 'H', 'i', 'j', 'J', 'k', 'K', 'L', 'm', 'M', 'n', 'N', 'p', 'P',
                'q', 'Q', 'r', 'R', 's', 'S', 't', 'T', 'u', 'U', 'v', 'V', 'w', 'W',
                'x', 'X', 'y', 'Y', 'z', 'Z',
                '2', '3', '4', '5', '6', '7', '8', '9' };
        for (int i = 0; i < length; i++) {
            seq[i] = codes[random.nextInt(codes.length)];
        }

        return new String(seq);
    }

}
