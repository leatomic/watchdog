package io.watchdog.security.config.annotation.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Getter @Setter
public class BrowserWebSecurityConfigurer extends CoreWebSecurityConfigurer {

    private String loginPageUrl;
    private String formLoginAttemptsLimitedUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http.formLogin().loginPage(loginPageUrl);

        http.authorizeRequests()
                .antMatchers(formLoginAttemptsLimitedUrl).permitAll();

    }
}
