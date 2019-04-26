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

import io.watchdog.security.web.verification.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 过滤器{@link VerificationProcessingFilter} 以及{@link VerificationTokenEndpointFilter}的配置器
 */
public class VerificationFiltersConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<VerificationFiltersConfigurer<H>, H> {

    private ProcessingRegistry processing = new ProcessingRegistry();
    private TokenEndpointRegistry tokenEndpoint = new TokenEndpointRegistry();

    public final VerificationFiltersConfigurer<H>.TokenEndpointRegistry tokenEndpoint() {
        return tokenEndpoint;
    }

    public final VerificationFiltersConfigurer<H>.ProcessingRegistry processing() {
        return processing;
    }

    @Override
    public void configure(H http) {

        VerificationTokenEndpointFilter tokenEndpointFilter = tokenEndpoint.createFilter();
        http.addFilterBefore(tokenEndpointFilter, AbstractPreAuthenticatedProcessingFilter.class);

        VerificationProcessingFilter processingFilter = processing.createFilter();
        http.addFilterBefore(postProcess(processingFilter), AbstractPreAuthenticatedProcessingFilter.class);

    }









    // Registry classes
    // =================================================================================================================


    /**
     * 配置过滤器{@link VerificationProcessingFilter}的配置项
     */
    public class ProcessingRegistry {

        private List<VerificationProvider> providers = new ArrayList<>();

        public final VerificationFiltersConfigurer<H>.ProcessingRegistry addProvider(VerificationProvider provider) {
            providers.add(Objects.requireNonNull(provider));
            return this;
        }

        public final VerificationFiltersConfigurer<H>.ProcessingRegistry addProviders(Collection<VerificationProvider> providers) {
            boolean hasProviders = providers != null && !providers.isEmpty();
            if (hasProviders) {
                for (VerificationProvider provider : providers)
                    this.providers.add(Objects.requireNonNull(provider));
            }
            return this;
        }

        public VerificationFiltersConfigurer<H> and() {
            return VerificationFiltersConfigurer.this;
        }

        private VerificationProcessingFilter createFilter() {
            VerificationProcessingFilter processingFilter = new VerificationProcessingFilter();
            processingFilter.addProviders(providers);
            return processingFilter;
        }

    }







    /**
     * 配置过滤器{@link VerificationTokenEndpointFilter}的配置项
     */
    public class TokenEndpointRegistry {
        private RequestMatcher acquiresTokenRequestMatcher;
        private String tokenTypeParameter;
        private String businessParameter;
        private List<VerificationRequestHandler> handlers = new ArrayList<>();

        public final VerificationFiltersConfigurer<H>.TokenEndpointRegistry acquiresTokenUrl(String acquiresTokenUrl) {
            this.acquiresTokenRequestMatcher = new AntPathRequestMatcher(acquiresTokenUrl, "GET");
            return this;
        }

        public final VerificationFiltersConfigurer<H>.TokenEndpointRegistry tokenTypeParameter(String tokenTypeParameter) {
            this.tokenTypeParameter = tokenTypeParameter;
            return this;
        }

        public final VerificationFiltersConfigurer<H>.TokenEndpointRegistry businessParameter(String businessParameter) {
            this.businessParameter = businessParameter;
            return this;
        }

        public final VerificationFiltersConfigurer<H>.TokenEndpointRegistry applyRequestHandler(VerificationRequestHandler handler) {
            this.handlers.add(Objects.requireNonNull(handler));
            return this;
        }

        public final VerificationFiltersConfigurer<H>.TokenEndpointRegistry applyRequestHandlers(Collection<VerificationRequestHandler> handlers) {
            boolean hasHandlers = handlers != null && !handlers.isEmpty();
            if (hasHandlers) {
                for (VerificationRequestHandler handler : handlers)
                    this.handlers.add(Objects.requireNonNull(handler));
            }
            return this;
        }

        public VerificationFiltersConfigurer<H> and() {
            return VerificationFiltersConfigurer.this;
        }

        private VerificationTokenEndpointFilter createFilter() {

            VerificationTokenEndpointFilter tokenEndpointFilter = new VerificationTokenEndpointFilter(acquiresTokenRequestMatcher, handlers);

            if (StringUtils.isNotBlank(tokenTypeParameter))
                tokenEndpointFilter.setTokenTypeParameter(tokenTypeParameter);

            if (StringUtils.isNotBlank(businessParameter))
                tokenEndpointFilter.setBusinessParameter(businessParameter);

            return tokenEndpointFilter;
        }

    }

}
