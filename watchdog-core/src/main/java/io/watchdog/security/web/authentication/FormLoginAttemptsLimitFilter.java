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

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class FormLoginAttemptsLimitFilter extends GenericFilterBean {

    private RequestMatcher formLoginProcessingRequestMatcher;
    private FormLoginAttemptsLimiter attemptsLimiter;
    private FormLoginDisabledHandler attemptsLimitedHandler;

    public FormLoginAttemptsLimitFilter(RequestMatcher formLoginProcessingRequestMatcher,
                                        FormLoginAttemptsLimiter attemptsLimiter,
                                        FormLoginDisabledHandler attemptsLimitedHandler) {
        this.formLoginProcessingRequestMatcher = formLoginProcessingRequestMatcher;
        this.attemptsLimiter = attemptsLimiter;
        this.attemptsLimitedHandler = attemptsLimitedHandler;
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (formLoginProcessingRequestMatcher.matches(request)) {
            boolean allowed = attemptsLimiter.checkAttempt(new FormLoginDetails(request));
            if (!allowed) {
                attemptsLimitedHandler.onFormLoginDisabled(request, response);
                return;
            }
        }

        chain.doFilter(req, res);

    }





}
