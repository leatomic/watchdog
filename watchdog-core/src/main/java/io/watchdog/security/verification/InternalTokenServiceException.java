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

package io.watchdog.security.verification;

/**
 * <p>token服务运行期间（主要包括生成token、以及校验token），由服务器内部导致的异常。区别于{@link TokenServiceException}。<p/>
 */
public class InternalTokenServiceException extends TokenServiceException {

    public InternalTokenServiceException(String message) {
        super(message);
    }

    public InternalTokenServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
