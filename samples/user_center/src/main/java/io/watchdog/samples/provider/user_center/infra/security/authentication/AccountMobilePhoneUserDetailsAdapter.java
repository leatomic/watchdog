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

package io.watchdog.samples.provider.user_center.infra.security.authentication;

import io.watchdog.samples.provider.user_center.domain.member.Account;
import io.watchdog.security.authentication.MobilePhoneUserDetails;

public class AccountMobilePhoneUserDetailsAdapter extends AccountUserDetailsAdapter implements MobilePhoneUserDetails {

    public AccountMobilePhoneUserDetailsAdapter(Account target) {
        super(target);
    }

    @Override
    public String getMobilePhone() {
        return getTarget().getAssociations().getMobilePhone();
    }

}
