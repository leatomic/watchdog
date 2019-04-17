package io.watchdog.security.web.verification;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.watchdog.http.SimpleResponseBody;
import io.watchdog.security.verification.InternalTokenServiceException;
import io.watchdog.security.verification.TokenServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestVerificationServiceFailureHandler implements VerificationServiceFailureHandler {

    private ObjectMapper objectMapper;

    public RestVerificationServiceFailureHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onVerificationServiceFailure(HttpServletRequest request, HttpServletResponse response, TokenServiceException exception) throws IOException, ServletException {
        HttpStatus status = exception instanceof InternalTokenServiceException
                ? HttpStatus.INTERNAL_SERVER_ERROR
                : HttpStatus.BAD_REQUEST;
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        new SimpleResponseBody(exception.getClass().getName(), exception.getLocalizedMessage(), null)
                )
        );
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
