package io.watchdog.samples.provider.user_center.config.annotation.web;

import io.watchdog.autoconfigure.properties.AuthenticationProperties;
import io.watchdog.samples.provider.user_center.security.authentication.DaoUserDetailsService;
import io.watchdog.samples.provider.user_center.security.authentication.RedisFormLoginAttemptsLimiter;
import io.watchdog.samples.provider.user_center.service.AccountService;
import io.watchdog.security.web.authentication.FormLoginAttemptsLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Slf4j
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {

    @Bean
    @Profile("dev")
    public WebSecurityConfigurer<WebSecurity> webSecurityConfigurer() {

        return new WebSecurityConfigurerAdapter() {

            @Override
            protected void configure(AuthenticationManagerBuilder auth) throws Exception {

            }

            @Override
            public void configure(WebSecurity web) throws Exception {
                web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
            }

            @Override
            protected void configure(HttpSecurity http) throws Exception {

                http.authorizeRequests()
                        .antMatchers("/h2-console", "/h2-console/**").permitAll()
                        .and().headers().frameOptions().disable();   // 方便h2-console查看数据库数据

                http.authorizeRequests()
                        .antMatchers("/", "/index").permitAll()
                        .anyRequest().authenticated();

            }

        };
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @ConditionalOnMissingBean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean(WebSecurityConfigurerAdapter webSecurityConfigurer) throws Exception {
        return webSecurityConfigurer.authenticationManagerBean();
    }

    @Bean
    public DaoUserDetailsService userDetailsService(AccountService accountService) {
        return new DaoUserDetailsService(accountService);
    }

    @Bean
    public FormLoginAttemptsLimiter formLoginAttemptsLimiter(
            RedisConnectionFactory connectionFactory,
            AuthenticationProperties authenticationProperties
    ) {
        RedisTemplate<String, String> template =  new StringRedisTemplate(connectionFactory);
        template.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));

        AuthenticationProperties.FormLogin.AttemptsLimit attemptsLimitProperties
                = authenticationProperties.getFormLogin().getAttemptsLimit();

        long warningFailureAttempts = attemptsLimitProperties.getWarningThreshold();
        long maximumFailureAttempts = attemptsLimitProperties.getMaximum();

        return new RedisFormLoginAttemptsLimiter(template, warningFailureAttempts, maximumFailureAttempts);
    }

}
