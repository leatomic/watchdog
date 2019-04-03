package io.watchdog.security.web.verification.impl.sms;

public interface SmsCodeSender {
    void send(String phone, CharSequence code);
}
