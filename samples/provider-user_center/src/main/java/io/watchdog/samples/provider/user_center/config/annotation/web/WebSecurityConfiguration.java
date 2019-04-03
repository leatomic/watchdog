package io.watchdog.samples.provider.user_center.config.annotation.web;

import io.watchdog.autoconfigure.properties.AuthenticationProperties;
import io.watchdog.autoconfigure.properties.VerificationProperties;
import io.watchdog.samples.provider.user_center.security.authentication.DaoUserDetailsService;
import io.watchdog.samples.provider.user_center.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@EnableConfigurationProperties({AuthenticationProperties.class, VerificationProperties.class})
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

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        log.info("Using default Password Encoder: BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(AccountService accountService) {
        return new DaoUserDetailsService(accountService);
    }

}

