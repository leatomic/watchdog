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

package io.watchdog.security.config.annotation.web.configurers;

import io.watchdog.security.web.authentication.FormLoginAttemptsLimitFilter;
import io.watchdog.security.web.authentication.FormLoginDisabledHandler;
import io.watchdog.security.web.authentication.FormLoginAttemptsLimiter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class FormLoginAttemptsLimitConfigurer <H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<FormLoginAttemptsLimitConfigurer<H>, H> {

    private RequestMatcher requestMatcher;
    private FormLoginAttemptsLimiter attemptsLimiter;
    private FormLoginDisabledHandler loginDisabledHandler;

    public FormLoginAttemptsLimitConfigurer<H> loginProcessingRequestMatcher(RequestMatcher formLoginProcessingRequestMatcher) {
        this.requestMatcher = formLoginProcessingRequestMatcher;
        return this;
    }

    public FormLoginAttemptsLimitConfigurer<H> attemptsLimiter(FormLoginAttemptsLimiter attemptsLimiter) {
        this.attemptsLimiter = attemptsLimiter;
        return this;
    }

    public FormLoginAttemptsLimitConfigurer<H> disableLogin(FormLoginDisabledHandler handler) {
        this.loginDisabledHandler = handler;
        return this;
    }

    @Override
    public void configure(H http) throws Exception {
        FormLoginAttemptsLimitFilter limitFilter = new FormLoginAttemptsLimitFilter(requestMatcher, attemptsLimiter, loginDisabledHandler);
        http.addFilterBefore(postProcess(limitFilter), UsernamePasswordAuthenticationFilter.class);
    }


}
