/*
 * Copyright (c) 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
