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

import io.watchdog.security.authentication.MobilePhoneAttributeAuthenticationToken;
import io.watchdog.security.authentication.UsernameAttributeAuthenticationProvider;
import io.watchdog.security.authentication.UsernameAttributeAuthenticationToken;
import io.watchdog.security.web.WebAttributes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证过滤器： 直接在请求中读取指定的属性值，将其作为用户名构建{@link UsernameAttributeAuthenticationToken}
 * 并交由{@link UsernameAttributeAuthenticationProvider}认证
 */
public class MobilePhoneAttributeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private String mobilePhoneAttribute = WebAttributes.SMS_CODE_LOGIN_USERNAME_ATTRIBUTE;

    // ~ Constructor
    // =================================================================================================================
    public MobilePhoneAttributeAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    // ~
    // =================================================================================================================
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = obtainUsername(request);

        MobilePhoneAttributeAuthenticationToken authRequest = new MobilePhoneAttributeAuthenticationToken(username);
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);

    }

    private String obtainUsername(HttpServletRequest request) {

        Object username = request.getAttribute(mobilePhoneAttribute);
        if (username == null) {
            throw new IllegalStateException(
                    "Request has been verified, " +
                            "but username attribute: " + mobilePhoneAttribute + " not set after verification succeed"
            );
        }

        return username.toString();
    }

    protected void setDetails(HttpServletRequest request, UsernameAttributeAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }



    // ~ Getter Setter
    // =================================================================================================================

    public String getMobilePhoneAttribute() {
        return mobilePhoneAttribute;
    }

    public void setMobilePhoneAttribute(String mobilePhoneAttribute) {
        if (StringUtils.isBlank(mobilePhoneAttribute)) {
            throw new IllegalArgumentException("mobilePhoneAttribute can not be blank");
        }
        this.mobilePhoneAttribute = mobilePhoneAttribute;
    }
}
