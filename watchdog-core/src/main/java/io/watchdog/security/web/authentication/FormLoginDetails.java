package io.watchdog.security.web.authentication;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;

@Getter @Setter
public class FormLoginDetails {

    private String remoteIpAddress;
    private String username;

    public FormLoginDetails(String remoteIpAddress, String username) {
        this.remoteIpAddress = remoteIpAddress;
        this.username = username;
    }

    public FormLoginDetails(HttpServletRequest request) {
        remoteIpAddress = request.getRemoteAddr();
        username = request.getParameter("username");
        if (username == null) {
            throw new IllegalStateException("request must be form login processing request");
        }
    }

}