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

package io.watchdog.security.web.verification;

import io.watchdog.security.verification.InternalTokenServiceException;
import io.watchdog.security.verification.TokenServiceException;
import io.watchdog.security.verification.VerificationToken;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Getter @Setter
public class VerificationRequestHandler<T extends VerificationToken> {

    private final VerificationService<?, T> service;
    private TokenWriter<T> writer;
    private VerificationServiceFailureHandler failureHandler;

    public VerificationRequestHandler(VerificationService<?, T> service,
                                       TokenWriter<T> writer,
                                       VerificationServiceFailureHandler failureHandler) {
        this.service = service;
        this.writer = writer;
        this.failureHandler = failureHandler;
    }

    public void handle(VerificationRequest request, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        try {
            T token = service.allocate(request.getParams());
            writer.write(token);
        }
        catch (IOException e) {
            log.error(e.getLocalizedMessage());
            InternalTokenServiceException we = new InternalTokenServiceException(e.getLocalizedMessage(), e);
            failureHandler.onVerificationServiceFailure(req, resp, we);
        }
        catch (InternalTokenServiceException ie) {
            log.error(ie.getLocalizedMessage());
            failureHandler.onVerificationServiceFailure(req, resp, ie);
        }
        catch (TokenServiceException e) {
            failureHandler.onVerificationServiceFailure(req, resp, e);
        }
    }

}


