package io.watchdog.samples.provider.user_center.domain.member;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @AllArgsConstructor
public class Password {

    private String seq;
    private LocalDateTime expirationTime;
    private LocalDate lastModified;

    public boolean isExpired() {
        return expirationTime != null && expirationTime.isBefore(LocalDateTime.now());
    }

}
