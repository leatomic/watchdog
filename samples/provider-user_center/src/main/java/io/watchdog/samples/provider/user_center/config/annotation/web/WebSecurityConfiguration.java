package io.watchdog.samples.provider.user_center.config.annotation.web;

import io.watchdog.samples.provider.user_center.security.authentication.DaoUserDetailsService;
import io.watchdog.samples.provider.user_center.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Slf4j
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {

//    @Bean
//    @Profile("dev")
//    public WebSecurityConfigurer<WebSecurity> webSecurityConfigurer() {
//
//        return new WebSecurityConfigurerAdapter() {
//
//            @Override
//            protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//
//            }
//
//            @Override
//            public void configure(WebSecurity web) throws Exception {
//                web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
//            }
//
//            @Override
//            protected void configure(HttpSecurity http) throws Exception {
//
//                http.authorizeRequests()
//                        .antMatchers("/h2-console", "/h2-console/**").permitAll()
//                        .and().headers().frameOptions().disable();   // 方便h2-console查看数据库数据
//
//                http.authorizeRequests()
//                        .antMatchers("/", "/index").permitAll()
//                        .anyRequest().authenticated();
//
//            }
//
//        };
//    }

//    @Bean(BeanIds.AUTHENTICATION_MANAGER)
//    public AuthenticationManager authenticationManagerBean(WebSecurityConfigurerAdapter webSecurityConfigurer) throws Exception {
//        return webSecurityConfigurer.authenticationManagerBean();
//    }

    @Bean
    public DaoUserDetailsService userDetailsService(AccountService accountService) {
        return new DaoUserDetailsService(accountService);
    }

}

