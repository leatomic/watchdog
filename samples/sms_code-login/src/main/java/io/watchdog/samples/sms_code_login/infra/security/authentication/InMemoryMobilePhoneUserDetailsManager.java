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

package io.watchdog.samples.sms_code_login.infra.security.authentication;

import io.watchdog.security.authentication.MobilePhoneUserDetails;
import io.watchdog.security.authentication.MobilePhoneUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InMemoryMobilePhoneUserDetailsManager implements MobilePhoneUserDetailsService {

    private Map<String, MobilePhoneUserDetails> users = new HashMap<>(16);

    public InMemoryMobilePhoneUserDetailsManager withUsers(MobilePhoneUserDetails... users) {
        for (MobilePhoneUserDetails user: Objects.requireNonNull(users)) {
            Objects.requireNonNull(user);
            this.users.put(user.getMobilePhone(), user);
        }
        return this;
    }

    @Override
    public MobilePhoneUserDetails loadUserByMobilePhone(String mobilePhone) throws UsernameNotFoundException {
        MobilePhoneUserDetails target = users.get(mobilePhone);
        if (target == null)
            throw new UsernameNotFoundException("MobilePhoneUserDetails for mobile phone: " + mobilePhone);
        return target;
    }

}
