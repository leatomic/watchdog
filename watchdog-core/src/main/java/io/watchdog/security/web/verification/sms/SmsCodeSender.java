package io.watchdog.security.web.verification.sms;

public interface SmsCodeSender {
    void send(String phone, CharSequence code);
}
