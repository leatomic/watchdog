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

package io.watchdog.autoconfigure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.watchdog.http.SimpleResponseBody;
import io.watchdog.security.config.BeanIds;
import io.watchdog.security.config.annotation.web.WatchdogWebSecurityConfigurerAdapter;
import io.watchdog.security.verification.HttpSessionTokenRepository;
import io.watchdog.security.verification.InternalTokenServiceException;
import io.watchdog.security.verification.TokenServiceException;
import io.watchdog.security.web.authentication.FormLoginAttemptsLimiter;
import io.watchdog.security.web.authentication.InMemoryFormLoginAttemptsLimiter;
import io.watchdog.security.web.verification.*;
import io.watchdog.security.web.verification.image.DefaultImageCodeWriter;
import io.watchdog.security.web.verification.image.ImageCode;
import io.watchdog.security.web.verification.image.ImageCodeService;
import io.watchdog.security.web.verification.image.ImageCodeVerificationService;
import io.watchdog.security.web.verification.sms.*;
import io.watchdog.validation.MobilePhoneValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.time.Duration;

@Slf4j
@Configuration
@EnableConfigurationProperties({AuthenticationProperties.class})
@PropertySource("classpath:/config/authentication.properties")
@Import({
    WebSecurityAutoConfiguration.VerificationConfiguration.class,
    WebSecurityAutoConfiguration.FormLoginConfiguration.class,
    WebSecurityAutoConfiguration.SmsCodeLoginConfiguration.class,
    ValidationConfiguration.class
})
@ComponentScan(basePackages = {"io.watchdog.security.authentication.provider.endpoint"})
public class WebSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WebSecurityConfigurer webSecurityConfigurer() {
        return new WatchdogWebSecurityConfigurerAdapter();
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        log.info("Using default Password Encoder: BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }




    @Slf4j
    @Configuration
    static class SmsCodeLoginConfiguration {

        private AuthenticationProperties.SmsCodeLogin smsCodeLoginProperties;

        public SmsCodeLoginConfiguration(AuthenticationProperties authenticationProperties) {
            this.smsCodeLoginProperties = authenticationProperties.getSmsCodeLogin();
        }

        @Bean(name = BeanIds.SMS_CODE_LOGIN_SUCCESS_HANDLER)
        @ConditionalOnMissingBean(name = BeanIds.SMS_CODE_LOGIN_SUCCESS_HANDLER)
        protected AuthenticationSuccessHandler smsCodeLoginSuccessHandler() {
            SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
            successHandler.setDefaultTargetUrl(smsCodeLoginProperties.getDefaultTargetUrl());
            return successHandler;
        }

        @Bean(name = BeanIds.SMS_CODE_LOGIN_FAILURE_HANDLER)
        @ConditionalOnMissingBean(name = BeanIds.SMS_CODE_LOGIN_FAILURE_HANDLER)
        protected AuthenticationFailureHandler smsCodeLoginFailureHandler() {
            return new SimpleUrlAuthenticationFailureHandler(smsCodeLoginProperties.getFailureUrl());
        }

        @Bean
        public VerificationRequestHandler smsCodeLoginVerificationRequestHandler(VerificationRequestHandler universalSmsCodeVerificationRequestHandler) {
            return universalSmsCodeVerificationRequestHandler;
        }
    }


    @Slf4j
    @Configuration
//    @ConditionalOnProperty(name = "watchdog.authentication.form-login.enabled", havingValue = "true")
    static class FormLoginConfiguration {

        private AuthenticationProperties.FormLogin formLoginProperties;

        public FormLoginConfiguration(AuthenticationProperties authenticationProperties) {
            this.formLoginProperties = authenticationProperties.getFormLogin();
        }

        @Bean
        @ConditionalOnProperty(name = "watchdog.authentication.form-login.attempts-limit.enabled", havingValue = "true")
        @ConditionalOnMissingBean
        public FormLoginAttemptsLimiter formLoginAttemptsLimiter() {

            log.warn("no FormLoginAttemptsLimiter configured, using InMemoryFormLoginAttemptsLimiter...");

            AuthenticationProperties.FormLogin.AttemptsLimit attemptsLimitProperties = formLoginProperties.getAttemptsLimit();
            long warningFailureAttempts = attemptsLimitProperties.getWarningThreshold();
            long maximumFailureAttempts = attemptsLimitProperties.getMaximum();
            Duration howLongLoginDisabled = attemptsLimitProperties.getHowLongWillLoginBeDisabled();

            return new InMemoryFormLoginAttemptsLimiter(warningFailureAttempts, maximumFailureAttempts, howLongLoginDisabled);
        }


        @Bean(name = BeanIds.FORM_LOGIN_SUCCESS_HANDLER)
        @ConditionalOnMissingBean(name = BeanIds.FORM_LOGIN_SUCCESS_HANDLER)
        protected AuthenticationSuccessHandler formLoginSuccessHandler() {
            SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
            successHandler.setDefaultTargetUrl(formLoginProperties.getDefaultTargetUrl());
            return successHandler;
        }


        @Bean(name = BeanIds.FORM_LOGIN_FAILURE_HANDLER)
        @ConditionalOnMissingBean(name = BeanIds.FORM_LOGIN_FAILURE_HANDLER)
        protected AuthenticationFailureHandler formLoginFailureHandler() {
            String failureUrl = formLoginProperties.getFailureUrl();
            return new SimpleUrlAuthenticationFailureHandler(failureUrl);
        }

        @Bean
        public VerificationRequestHandler formLoginVerificationRequestHandler(VerificationRequestHandler universalImageCodeVerificationRequestHandler) {
            return universalImageCodeVerificationRequestHandler;
        }

    }


    @Slf4j
    @Configuration
    @EnableConfigurationProperties({VerificationProperties.class})
    @PropertySource("classpath:/config/verification.properties")
    static class VerificationConfiguration {

        private VerificationProperties verificationProperties;
        private ObjectMapper objectMapper;
        public VerificationConfiguration(VerificationProperties verificationProperties, ObjectMapper objectMapper) {
            this.verificationProperties = verificationProperties;
            this.objectMapper = objectMapper;
        }


        @Bean
        public VerificationServiceFailureHandler commonVerificationServiceFailureHandler() {
            return (request, response, exception) -> {
                HttpStatus status = exception instanceof InternalTokenServiceException
                        ? HttpStatus.INTERNAL_SERVER_ERROR
                        : HttpStatus.BAD_REQUEST;
                response.setStatus(status.value());
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.getWriter().write(
                        objectMapper.writeValueAsString(
                                new SimpleResponseBody(exception.getClass().getName(), exception.getLocalizedMessage(), null)
                        )
                );
            };
        }





        // 通用的图片验证码服务
        // =======================================================================================
        @Bean
        public ImageCodeService imageCodeService() {
            return new ImageCodeService();
        }

        @Bean
        public TokenRepository<ImageCode> imageCodeRepository() {
            return new HttpSessionTokenRepository<>();
        }

        @Bean
        public ImageCodeVerificationService universalImageCodeVerificationService() {
            return new ImageCodeVerificationService(imageCodeService(), imageCodeRepository());
        }

        @Bean
        public VerificationRequestHandler<ImageCode> universalImageCodeVerificationRequestHandler() {

            DefaultImageCodeWriter imageCodeWriter = new DefaultImageCodeWriter();

            return new VerificationRequestHandler<>(
                    universalImageCodeVerificationService(),
                    imageCodeWriter,
                    commonVerificationServiceFailureHandler()
            );
        }





        // 通用的手机短信验证码服务
        // =================================================================================================================
        @Bean
        public SmsCodeService smsCodeService() {
            return new SmsCodeService();
        }

        @Bean
        public TokenRepository<SmsCode> smsCodeRepository() {
            return new HttpSessionTokenRepository<>();
        }

        @Bean
        public TokenRequestChecker<SmsCodeTokenRequest> commonPreSmsCodeTokenRequestChecker(MobilePhoneValidator mobilePhoneValidator) {
            return request -> {
                String mobilePhone = request.getMobilePhone();
                if (!mobilePhoneValidator.isValid(request.getMobilePhone(), null)) {
                    throw new TokenServiceException("invalid mobile phone : " + mobilePhone);
                }
            };
        }

        @Bean
        public SmsCodeVerificationService universalSmsCodeVerificationService(
                TokenRequestChecker<SmsCodeTokenRequest> commonPreSmsCodeTokenRequestChecker
        ) {
            SmsCodeVerificationService smsCodeVerificationService
                    = new SmsCodeVerificationService(smsCodeService(), smsCodeRepository());
            smsCodeVerificationService.setPreTokenRequestChecker(commonPreSmsCodeTokenRequestChecker);
            return smsCodeVerificationService;
        }

        @Bean
        public VerificationRequestHandler<SmsCode> universalSmsCodeVerificationRequestHandler(
                SmsCodeVerificationService universalSmsCodeVerificationService
        ) {
            TokenWriter<SmsCode> smsCodeSender = new SmsCodeConsoleWriter();
            return new VerificationRequestHandler<>(
                    universalSmsCodeVerificationService,
                    smsCodeSender,
                    commonVerificationServiceFailureHandler()
            );
        }

    }

}
