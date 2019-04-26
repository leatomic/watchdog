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

package io.watchdog.samples.provider.user_center.service;

/**
 * @author le
 * @since v0.1.0
 */
public class UserNotFoundOrCanceledException extends BusinessException {

    private static final int CODE = 0x002;

    private final Long invalidId;

    public UserNotFoundOrCanceledException(Long invalidId) {
        super(CODE, "user not found or canceled for id " + invalidId + "!");
        this.invalidId = invalidId;
    }

    public Long getInvalidId() {
        return invalidId;
    }

}
