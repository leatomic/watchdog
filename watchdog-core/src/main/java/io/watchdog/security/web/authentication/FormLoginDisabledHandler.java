package io.watchdog.security.web.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface FormLoginDisabledHandler {

    void onFormLoginDisabled(HttpServletRequest request, HttpServletResponse response) throws IOException;

}
