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

package io.watchdog.security.web.authentication;

import io.watchdog.security.web.WebAttributes;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

public class RequiresVerificationFormLoginRequestMatcher implements RequestMatcher {

    private final RequestMatcher formLoginProcessingRequestMatcher;

    public RequiresVerificationFormLoginRequestMatcher(String formLoginProcessingUrl) {
        this.formLoginProcessingRequestMatcher = new AntPathRequestMatcher(formLoginProcessingUrl, "POST");
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return formLoginProcessingRequestMatcher.matches(request) && requiresVerification(request);
    }

    public static boolean requiresVerification(HttpServletRequest request) {
        return WebUtils.getSessionAttribute(request, WebAttributes.FORM_LOGIN_REQUIRES_VERIFICATION_TOKEN) != null;
    }

}
