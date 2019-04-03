package io.watchdog.security.config.annotation.web;

import io.watchdog.autoconfigure.properties.AuthenticationProperties;
import io.watchdog.security.config.annotation.web.configurers.VerificationFiltersConfigurer;
import io.watchdog.security.web.authentication.RequiresVerificationFormLoginRequestMatcher;
import io.watchdog.security.web.verification.TokenService;
import io.watchdog.security.web.verification.VerificationProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Order(99)
@AllArgsConstructor
@Getter @Setter
public class CoreWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private AuthenticationProperties authenticationProperties;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {



        http.authorizeRequests()
                .antMatchers("/verification.token").permitAll()
                .antMatchers("/sign-up", "/sign-up/**").permitAll();

        String formLoginProcessingUrl = authenticationProperties.getFormLogin().getProcessingUrl();
        http.formLogin()
                .loginProcessingUrl(formLoginProcessingUrl)
                .successHandler(formLoginSuccessHandler)
                .failureHandler(formLoginFailureHandler)
                .permitAll();

        enableFormLoginRequestVerification(http);

    }

    private AuthenticationFailureHandler formLoginFailureHandler;
    private AuthenticationSuccessHandler formLoginSuccessHandler;
//    private ImageCodeService imageCodeService;

    private TokenService<?> formLoginRequestVerificationTokenService;

    // 激活对表单登录请求的图片验证码验证功能
    protected void enableFormLoginRequestVerification(HttpSecurity http) throws Exception {
        String formLoginProcessingUrl = authenticationProperties.getFormLogin().getProcessingUrl();
        RequestMatcher requestMatcher = new RequiresVerificationFormLoginRequestMatcher(formLoginProcessingUrl);

        VerificationProvider<?> formLoginImageCodeVerifier = new VerificationProvider<>(requestMatcher, formLoginRequestVerificationTokenService);

        getOrApplyVerificationConfigurer(http)
                .processing().addProvider(formLoginImageCodeVerifier);
    }

    private VerificationFiltersConfigurer<HttpSecurity> verificationFiltersConfigurer;
    @SuppressWarnings("unchecked")
    protected VerificationFiltersConfigurer<HttpSecurity> getOrApplyVerificationConfigurer(HttpSecurity http) throws Exception {
        VerificationFiltersConfigurer<HttpSecurity> configurer = http.getConfigurer(VerificationFiltersConfigurer.class);
        if(configurer == null) {
            configurer = http.apply(verificationFiltersConfigurer);
        }
        return configurer;
    }

}
