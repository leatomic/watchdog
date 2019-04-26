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

