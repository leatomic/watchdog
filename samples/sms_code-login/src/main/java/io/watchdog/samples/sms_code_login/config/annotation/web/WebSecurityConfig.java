package io.watchdog.samples.sms_code_login.config.annotation.web;

import io.watchdog.samples.sms_code_login.infra.security.authentication.InMemoryMobilePhoneUserDetailsManager;
import io.watchdog.samples.sms_code_login.infra.security.authentication.MobilePhoneUserDetailsAdapter;
import io.watchdog.security.authentication.MobilePhoneUserDetailsService;
import io.watchdog.security.config.annotation.web.WatchdogWebSecurityConfigurerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;

@Slf4j
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WatchdogWebSecurityConfigurerAdapter {


    @Bean
    public MobilePhoneUserDetailsService mobilePhoneUserDetailsService() throws Exception {
        return new InMemoryMobilePhoneUserDetailsManager().withUsers(
            new MobilePhoneUserDetailsAdapter(
                    "13570000000", AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN")
            ),
            new MobilePhoneUserDetailsAdapter(
                    "13570000001", AuthorityUtils.createAuthorityList("ROLE_USER")
            )
        );
    }




    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }







    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated();

    }



}

