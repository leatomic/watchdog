package io.watchdog.security.web.verification.image;

import io.watchdog.security.verification.GeneralTokenService;

import java.util.Random;

public class ImageCodeService extends GeneralTokenService<ImageCodeTokenRequest, ImageCode> {


    protected final Random random = new Random();

    @Override
    public ImageCode allocate(ImageCodeTokenRequest request) {

        String seq = generateCodeSeq(request.getCodeLength());

        return new ImageCode(seq, request.getCodeValidityDuration(), request.getImageWidth(), request.getImageHeight());

    }

    private  String generateCodeSeq(int length) {
        char[] seq = new char[length];
        char[] codes = {  'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'g', 'G',
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
