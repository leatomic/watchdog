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

package io.watchdog.security.web.verification.sms;

import io.watchdog.security.web.verification.TokenWriter;

/**
 * <p>默认的短信验证码输出器（发送器）
 * <p>负责模拟调用第三方短信服务平台的接口，将短信内容打印到控制台中</p>
 */
public class SmsCodeConsoleWriter implements TokenWriter<SmsCode> {

    @Override
    public void write(SmsCode token) {
        String content = "[watchdog] 您的验证码是" + token.getKey() + "。" +
                "\n如非本人操作，请您忽略本短信。" +
                "\n该验证码将在" + token.getExpirationTime() + "后失效";

        System.err.println("模拟调用手机短信接口,向手机号码[" + token.getForPhone() + "]发送短信：\n" + content + "\n");
    }

}

