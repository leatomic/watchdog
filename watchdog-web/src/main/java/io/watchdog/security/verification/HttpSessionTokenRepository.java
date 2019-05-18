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

package io.watchdog.security.verification;

import io.watchdog.security.web.verification.TokenRepository;
import io.watchdog.security.web.verification.VerificationRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

public class HttpSessionTokenRepository<T extends VerificationToken> implements TokenRepository<T> {

    private static final String PREFIX = "verification-token:";

    @Override @SuppressWarnings("unchecked")
    public T load(VerificationRequest.Type forTokenRequestType) {
        return (T) currentSession().getAttribute(assembleAttributeName(forTokenRequestType));
    }

    @Override
    public void save(VerificationRequest.Type forTokenRequestType, T token) {
        currentSession().setAttribute(assembleAttributeName(forTokenRequestType), token);
    }

    @Override
    public void remove(VerificationRequest.Type forTokenRequestType, T token) {
        currentSession().removeAttribute(assembleAttributeName(forTokenRequestType));
    }

    private HttpSession currentSession() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return servletRequestAttributes.getRequest().getSession(true);
    }

    private String assembleAttributeName(VerificationRequest.Type forTokenRequestType) {
        return PREFIX + forTokenRequestType.getTokenType() + ":" + forTokenRequestType.getBusiness();
    }

}
