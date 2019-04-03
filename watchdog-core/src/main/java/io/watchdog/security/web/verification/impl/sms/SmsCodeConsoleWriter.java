package io.watchdog.security.web.verification.impl.sms;

import io.watchdog.security.web.verification.TokenWriter;

import java.io.IOException;

public class SmsCodeConsoleWriter implements TokenWriter<SmsCode> {

    @Override
    public void write(SmsCode token) throws IOException {
        String content = "[watchdog] 您的验证码是" + token.getKey() + "。如非本人操作，请您忽略本短信。该验证码将在"
        + token.getExpirationTime() + "后失效";
        System.err.println("模拟手机短信验证码发送： 向手机号码：" + token.getForPhone() + "发送短信：" + content);
    }

}
