package io.watchdog.security.config.annotation.web.configurers;

import io.watchdog.security.web.verification.*;
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
    public void configure(H http) throws Exception {
        RequestMatcher acquiresTokenRequestMatcher              = tokenEndpoint.acquiresTokenRequestMatcher;
        String tokenTypeParameter                               = tokenEndpoint.tokenTypeParameter;
        List< VerificationService > services                    = tokenEndpoint.services;
        VerificationServiceFailureHandler verificationServiceFailureHandler =
                Objects.requireNonNull(tokenEndpoint.serviceFailureHandler, "VerificationServiceFailureHandler has not been configured");

        VerificationTokenEndpointFilter tokenEndpointFilter = new VerificationTokenEndpointFilter(acquiresTokenRequestMatcher, tokenTypeParameter, services, verificationServiceFailureHandler);
        http.addFilterBefore(tokenEndpointFilter, AbstractPreAuthenticatedProcessingFilter.class);

        List<VerificationProvider> providers      = processing.providers;
        VerificationFailureHandler failureHandler =
                Objects.requireNonNull(processing.failureHandler, "VerificationFailureHandler has not been configured");

        VerificationProcessingFilter processingFilter = new VerificationProcessingFilter(failureHandler);
        processingFilter.addProviders(providers);
        http.addFilterBefore(processingFilter, AbstractPreAuthenticatedProcessingFilter.class);
    }

    /**
     * 配置过滤器{@link VerificationProcessingFilter}的配置项
     */
    public class ProcessingRegistry {

        private List<VerificationProvider> providers = new ArrayList<>();
        private VerificationFailureHandler failureHandler;

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

        public final VerificationFiltersConfigurer<H>.ProcessingRegistry failureHandler(VerificationFailureHandler failureHandler) {
            this.failureHandler = Objects.requireNonNull(failureHandler);
            return this;
        }

        public VerificationFiltersConfigurer<H> and() {
            return VerificationFiltersConfigurer.this;
        }

    }


    /**
     * 配置过滤器{@link VerificationTokenEndpointFilter}的配置项
     */
    public class TokenEndpointRegistry {

        private RequestMatcher acquiresTokenRequestMatcher;
        private String tokenTypeParameter;
        private List< VerificationService > services = new ArrayList<>();
        private VerificationServiceFailureHandler serviceFailureHandler;

        public final VerificationFiltersConfigurer<H>.TokenEndpointRegistry acquiresTokenUrl(String acquiresTokenUrl) {
            this.acquiresTokenRequestMatcher = new AntPathRequestMatcher(acquiresTokenUrl, "GET");
            return this;
        }

        public final VerificationFiltersConfigurer<H>.TokenEndpointRegistry tokenTypeParameter(String tokenTypeParameter) {
            this.tokenTypeParameter = tokenTypeParameter;
            return this;
        }

        public final VerificationFiltersConfigurer<H>.TokenEndpointRegistry addServices(Collection<VerificationService> services) {
            boolean hasServices = services != null && !services.isEmpty();
            if (hasServices) {
                for (VerificationService service : services)
                    this.services.add(Objects.requireNonNull(service));
            }
            return this;
        }

        public final VerificationFiltersConfigurer<H>.TokenEndpointRegistry serviceFailureHandler(VerificationServiceFailureHandler serviceFailureHandler) {
            this.serviceFailureHandler = serviceFailureHandler;
            return this;
        }

        public VerificationFiltersConfigurer<H> and() {
            return VerificationFiltersConfigurer.this;
        }

    }

}
