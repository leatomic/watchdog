package io.watchdog.security.web.verification;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Getter @Setter
public class VerificationService <T extends VerificationToken> {

    private String supportsTokenType;
    private TokenService<T> service;
    private TokenWriter<T> writer;

    public VerificationService(String supportsTokenType, TokenService<T> service, TokenWriter<T> writer) {
        this.supportsTokenType = supportsTokenType;
        this.service = service;
        this.writer = writer;
    }

    public boolean supports(String tokenType) {
        return supportsTokenType.equalsIgnoreCase(tokenType);
    }

    public void allocateAndWriteTokenFor(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        T token = service.allocate(parameterMap);
        try {
            writer.write(token);
        } catch (IOException e) {
            throw new InternalTokenServiceException("writes token failed, " + e.getMessage(), e);
        }
    }

}
