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

package io.watchdog.security.web.verification;


import io.watchdog.security.verification.VerificationToken;

public interface TokenRepository<T extends VerificationToken> {

    T load(VerificationRequest.Type forTokenRequestType);

    void save(VerificationRequest.Type forTokenRequestType, T token);

    // TODO 替换成如果与期望值相等就删除
    void remove(VerificationRequest.Type forTokenRequestType, T token);

}