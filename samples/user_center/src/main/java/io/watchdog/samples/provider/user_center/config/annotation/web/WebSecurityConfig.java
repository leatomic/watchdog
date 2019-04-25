package io.watchdog.samples.provider.user_center.config.annotation.web;

import io.watchdog.samples.provider.user_center.infra.security.authentication.DaoUserDetailsService;
import io.watchdog.samples.provider.user_center.service.AccountService;
import io.watchdog.security.config.annotation.web.WatchdogWebSecurityConfigurerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

@Slf4j
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WatchdogWebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);       // 必须先调用父类的
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);      // 必须先调用父类的

        http.authorizeRequests()
                .antMatchers("/h2-console", "/h2-console/**").permitAll()
                .and().headers().frameOptions().disable();   // 方便h2-console查看数据库数据

        http.authorizeRequests()
                .antMatchers("/", "/index").permitAll()
                .anyRequest().authenticated();

    }

//    @Bean(BeanIds.AUTHENTICATION_MANAGER)
//    @ConditionalOnMissingBean(name = BeanIds.AUTHENTICATION_MANAGER)
//    public AuthenticationManager authenticationManager() throws Exception {
//        return authenticationManagerBean();
//    }

    @Bean
    public DaoUserDetailsService userDetailsService(AccountService accountService) {
        return new DaoUserDetailsService(accountService);
    }

}

