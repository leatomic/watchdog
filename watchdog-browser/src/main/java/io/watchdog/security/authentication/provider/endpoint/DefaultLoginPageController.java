package io.watchdog.security.authentication.provider.endpoint;

import io.watchdog.security.web.authentication.RequiresVerificationFormLoginRequestMatcher;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 替代{@link org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter}
 */
@Slf4j
@Controller
@RequestMapping("${watchdog.authentication.login-page-url:/login}")
public class DefaultLoginPageController {

    private Function<HttpServletRequest, Map<String, String>> resolveHiddenInputs = request -> {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if(token == null) {
            return Collections.emptyMap();
        }
        return Collections.singletonMap(token.getParameterName(), token.getToken());
    };

    @GetMapping
    @ResponseBody
    public ResponseEntity toLogin(@RequestParam(required = false) String error, @RequestParam(required = false) String approach,
                                  @RequestParam(required = false) String logout,
                                  HttpServletRequest request) {
        Map<String, Object> attributes = getAttributesMap(error, approach, logout, request);

        return ResponseEntity.ok(attributes);

    }

    @GetMapping(produces = "text/html")
    public ModelAndView loginHtml(@RequestParam(required = false) String error, @RequestParam(required = false) String approach,
                                  @RequestParam(required = false) String logout,
                                  HttpServletRequest request) {

        Map<String, Object> attributes = getAttributesMap(error, approach, logout, request);

        return new ModelAndView("login", attributes, HttpStatus.OK);

    }

    private Map<String, Object> getAttributesMap(String error, String approach, String logout, HttpServletRequest request) {

        Map<String, Object> attributes = new HashMap<>();

        setFormLoginIfEnabled(attributes, request);

        setSmsCodeLoginIfEnabled(attributes, request);

        setHiddenInputs(attributes, request);

        if (error != null) {
            setErrorMessage(attributes, request);
            setAuthenticationApproach(attributes, approach);
        }
        else if (logout != null) {
            setLogoutSuccess(attributes);
        }

        return attributes;
    }

    @Value("${watchdog.authentication.form-login.enabled}")
    private boolean formLoginEnabled;
    @Value("${watchdog.authentication.form-login.username-parameter}")
    private String formLoginUsernameParameter;
    @Value("${watchdog.authentication.form-login.password-parameter}")
    private String formLoginPasswordParameter;
    @Value("${watchdog.authentication.form-login.verification.token-type}")
    private String formLoginVerificationTokenType;
    @Value("${watchdog.authentication.form-login.verification.token-parameter}")
    private String formLoginVerificationTokenParameter;
    @Value("${watchdog.authentication.form-login.remember-me-parameter}")
    private String formLoginRememberMeParameter;
    @Value("${watchdog.authentication.form-login.processing-url}")
    private String formLoginProcessingUrl;


    private void setFormLoginIfEnabled(Map<String, Object> attributesMap, HttpServletRequest request) {

        if (formLoginEnabled) {

            FormLogin formLogin = new FormLogin();
            attributesMap.put("formLogin", formLogin);

            formLogin.usernameParameter = formLoginUsernameParameter;
            formLogin.passwordParameter = formLoginPasswordParameter;
            if (requiresFormLoginVerification(request)) {
                formLogin.verification = new FormLogin.Verification();
                formLogin.verification.tokenType = formLoginVerificationTokenType;
                formLogin.verification.tokenParameter = formLoginVerificationTokenParameter;
            }
            formLogin.rememberMeParameter = formLoginRememberMeParameter;
            formLogin.processingUrl = formLoginProcessingUrl;
        }
    }

    private boolean requiresFormLoginVerification(HttpServletRequest request) {
        return RequiresVerificationFormLoginRequestMatcher.requiresVerification(request);
    }

    @Value("${watchdog.authentication.sms-code-login.enabled}")
    private boolean smsCodeLoginEnabled;
    @Value("${watchdog.authentication.sms-code-login.verification.token-type}")
    private String smsCodeLoginVerificationTokenType;
    @Value("${watchdog.authentication.sms-code-login.verification.mobile-phone-parameter}")
    private String smsCodeLoginVerificationMobilePhoneParameter;
    @Value("${watchdog.authentication.sms-code-login.verification.token-parameter}")
    private String smsCodeLoginVerificationTokenParameter;
    @Value("${watchdog.authentication.sms-code-login.processing-url}")
    private String smsCodeLoginProcessingUrl;

    private void setSmsCodeLoginIfEnabled(Map<String, Object> attributesMap, HttpServletRequest request) {

        if (smsCodeLoginEnabled) {
            SmsCodeLogin smsCodeLogin = new SmsCodeLogin();
            attributesMap.put("smsCodeLogin", smsCodeLogin);

            smsCodeLogin.smsCodeTokenTypeParameter = smsCodeLoginVerificationTokenType;
            smsCodeLogin.mobilePhoneParameter = smsCodeLoginVerificationMobilePhoneParameter;
            smsCodeLogin.smsCodeParameter = smsCodeLoginVerificationTokenParameter;
            smsCodeLogin.processingUrl = smsCodeLoginProcessingUrl;
        }
    }

    private void setHiddenInputs(Map<String, Object> attributesMap, HttpServletRequest request) {
        attributesMap.put("hiddenInputs", resolveHiddenInputs.apply(request));
    }

    private void setErrorMessage(Map<String, Object> attributesMap, HttpServletRequest request){

        String errorMsg = "none";

        String exceptionAttrName = WebAttributes.AUTHENTICATION_EXCEPTION;
        AuthenticationException ex = (AuthenticationException) request.getAttribute(exceptionAttrName);

        if (ex == null) {
            // redirection, try to obtain exception from session
            HttpSession session = request.getSession(false);
            if (session != null) {
                ex = (AuthenticationException) session.getAttribute(exceptionAttrName);
            }
        }

        if (ex != null) {
            errorMsg = ex.getLocalizedMessage();
        }

        attributesMap.put("error", errorMsg);

    }

    private void setAuthenticationApproach(Map<String, Object> attributesMap, String approach) {
        attributesMap.put("authenticationApproach", approach);
    }

    private void setLogoutSuccess(Map<String, Object> attributesMap) {
        attributesMap.put("logoutSuccess", true);
    }

    @Getter @Setter
    static class FormLogin {
        String usernameParameter;
        String passwordParameter;
        Verification verification;
        String rememberMeParameter;
        String processingUrl;
        @Getter @Setter
        static class Verification {
            String tokenType;
            String tokenParameter;
        }
    }

    @Getter @Setter
    static class SmsCodeLogin {
        String smsCodeTokenTypeParameter;
        String mobilePhoneParameter;
        String smsCodeParameter;
        String processingUrl;
    }

////    private boolean openIdLoginEnabled;
////    private String  openIdUsernameParameter;
////    private String  openIdRememberMeParameter;
////    private String  openIdAuthenticationUrl;
////
////    private boolean socialLoginEnabled;
////    private Map<String, String> oauth2AuthorizationUrlToClientName;

}
