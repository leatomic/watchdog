package io.watchdog.http;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleResponseBody {
    private String id;
    private String message;
    private Object content;
}
