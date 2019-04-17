package io.watchdog.security.config.annotation.web.configurers;

import io.watchdog.security.authentication.MobilePhoneAttributeAuthenticationProvider;
import io.watchdog.security.authentication.MobilePhoneUserDetailsService;
import io.watchdog.security.web.WebAttributes;
import io.watchdog.security.web.authentication.MobilePhoneAttributeAuthenticationFilter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class SmsCodeLoginConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<SmsCodeLoginConfigurer<H>, H> {

    private MobilePhoneAttributeAuthenticationFilter authFilter = new MobilePhoneAttributeAuthenticationFilter();

    public SmsCodeLoginConfigurer() {
        mobilePhoneAttribute(WebAttributes.SMS_CODE_LOGIN_USERNAME_ATTRIBUTE);
    }

    public SmsCodeLoginConfigurer<H> authenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        authFilter.setAuthenticationDetailsSource(authenticationDetailsSource);
        return this;
    }

    public SmsCodeLoginConfigurer<H> mobilePhoneAttribute(String smsCodeLoginUsernameAttribute) {
        authFilter.setMobilePhoneAttribute(smsCodeLoginUsernameAttribute);
        return this;
    }

    public SmsCodeLoginConfigurer<H> loginProcessingUrl(String loginProcessingUrl) {
        authFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(loginProcessingUrl, "POST"));
        return this;
    }

    public SmsCodeLoginConfigurer<H> successHandler(AuthenticationSuccessHandler successHandler) {
        authFilter.setAuthenticationSuccessHandler(successHandler);
        return this;
    }

    public SmsCodeLoginConfigurer<H> failureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        authFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        return this;
    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);
    }



    @Override
    public void configure(H http) throws Exception {

        super.configure(http);

        authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            authFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }

        ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
        MobilePhoneUserDetailsService userDetailsService
                = getDependency(applicationContext, MobilePhoneUserDetailsService.class);

        MobilePhoneAttributeAuthenticationProvider usernameAttributeAuthenticationProvider
                = new MobilePhoneAttributeAuthenticationProvider(userDetailsService);

        http.authenticationProvider(usernameAttributeAuthenticationProvider);

        http.addFilterAfter(postProcess(authFilter), UsernamePasswordAuthenticationFilter.class);
    }

    private <T> T getDependency(ApplicationContext applicationContext, Class<T> dependencyType) {
        try {
            return applicationContext.getBean(dependencyType);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalStateException("SpringSocialConfigurer depends on " + dependencyType.getName() +". No single bean of that type found in application context.", e);
        }
    }

}

