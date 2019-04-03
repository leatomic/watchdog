package io.watchdog.samples.provider.user_center.domain.member;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Profile {
    private String avatar;
    private String bio;
    private Gender gender = Gender.PRIVATE;
    private LocalDate birthday;
}
