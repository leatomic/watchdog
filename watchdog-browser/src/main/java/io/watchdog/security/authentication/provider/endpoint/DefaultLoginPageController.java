package io.watchdog.security.authentication.provider.endpoint;

import io.watchdog.autoconfigure.properties.AuthenticationProperties;
import io.watchdog.security.web.authentication.RequiresVerificationFormLoginRequestMatcher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * 替代{@link org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter}
 */
@Slf4j
@Controller
@RequestMapping("{watchdog.properties.authentication.login-page-url}")
public class DefaultLoginPageController {

    private AuthenticationProperties properties;

    private Function<HttpServletRequest, Map<String, String>> resolveHiddenInputs = request -> {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if(token == null) {
            return Collections.emptyMap();
        }
        return Collections.singletonMap(token.getParameterName(), token.getToken());
    };

    public DefaultLoginPageController(AuthenticationProperties properties) {
        this.properties = properties;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity toLogin(@RequestParam(required = false) String error, @RequestParam(required = false) String approach,
                                  @RequestParam(required = false) String logout,
                                  HttpServletRequest request) {

        Result result = initResult(request);

        // 登录失败后的请求重定向，或者直接跳转
        if (error != null) {
            Result.LoginError loginError = getLoginError(request);
            loginError.authMode = approach;
            result.error = loginError;
        }
        // 退出成功
        else if (logout != null) {
            result.logoutSuccess = true;
        }
        // else ...默认是请求认证

        return ResponseEntity.ok(result);
    }

    @GetMapping(produces = "text/html")
    public ModelAndView loginHtml(@RequestParam(required = false) String error, @RequestParam(required = false) String approach,
                              @RequestParam(required = false) String logout,
                              HttpServletRequest request) {

        Result result = initResult(request);

        // 登录失败后的请求重定向，或者直接跳转
        if (error != null) {
            Result.LoginError loginError = getLoginError(request);
            loginError.authMode = approach;
            result.error = loginError;
        }
        // 退出成功
        else if (logout != null) {
            result.logoutSuccess = true;
        }

        ModelAndView mnv = new ModelAndView("login", HttpStatus.OK);
        mnv.addObject(result);
        return mnv;
    }

    private Result initResult(HttpServletRequest request) {

        Result result = new Result();

        // TODO use apache BeanUtils
        if (properties.getFormLogin().isEnabled()) {

            Result.FormLogin formLogin = new Result.FormLogin();
            AuthenticationProperties.FormLogin formLoginProperties = properties.getFormLogin();

            formLogin.usernameParameter     = formLoginProperties.getUsernameParameter();
            formLogin.passwordParameter     = formLoginProperties.getPasswordParameter();

            if (requiresFormLoginVerification(request)) {
                formLogin.verification          = new Result.FormLogin.Verification();
                formLogin.verification.tokenType        = formLoginProperties.getVerification().getTokenType();
                formLogin.verification.tokenParameter   = formLoginProperties.getVerification().getTokenParameter();
            }

            formLogin.rememberMeParameter   = formLoginProperties.getRememberMeParameter();
            formLogin.processingUrl         = formLoginProperties.getProcessingUrl();

            result.formLogin = formLogin;
        }

        if (properties.getSmsCodeLogin().isEnabled()) {
            Result.SmsCodeLogin smsCodeLogin = new Result.SmsCodeLogin();
            AuthenticationProperties.SmsCodeLogin smsCodeLoginProperties = properties.getSmsCodeLogin();

            smsCodeLogin.smsCodeTokenTypeParameter  = smsCodeLoginProperties.getVerification().getTokenType();
            smsCodeLogin.mobileParameter            = smsCodeLoginProperties.getVerification().getMobileParameter();
            smsCodeLogin.smsCodeParameter   = smsCodeLoginProperties.getVerification().getTokenParameter();

            smsCodeLogin.processingUrl = smsCodeLoginProperties.getProcessingUrl();

            result.smsCodeLogin = smsCodeLogin;
        }

        result.hiddenInputs = resolveHiddenInputs.apply(request);

        return result;
    }

    private boolean requiresFormLoginVerification(HttpServletRequest request) {
        return RequiresVerificationFormLoginRequestMatcher.requiresVerification(request);
    }

    private Result.LoginError getLoginError(HttpServletRequest request) {
        Result.LoginError loginError = new Result.LoginError();
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

        loginError.message = errorMsg;

        return loginError;
    }

    @Data static class Result implements Serializable {

        private FormLogin formLogin = new FormLogin();
        private SmsCodeLogin smsCodeLogin = new SmsCodeLogin();
        private Map<String, String> hiddenInputs = Collections.emptyMap();
        private LoginError error;
        private boolean logoutSuccess = false;

        @Data static class FormLogin implements Serializable {
            private String usernameParameter;               // 用户名参数，例如："username"
            private String passwordParameter;               // 密码参数，例如："password"

            // 获取短信验证码的内容，若为null则表示不需要提交验证码信息（通常是在第一次访问登录界面，或者未出现用户名密码错误导致登录失败的情况下）
            private Verification verification;

            private String rememberMeParameter;             // 记住我的checkbox参数名，例如："remember-me"

            // 最终按下登录按钮后将表单的数据提交到哪，例如："/authenticate/form"

            private String processingUrl;

            @Data @NoArgsConstructor @AllArgsConstructor
            static class Verification implements Serializable {
                // 需要获取的验证码类型，该值将用于发送获取验证码请求是作为参数指定要获取的验证码类型，
                // 例如图片验证码："image_code"，这之后页面需要自动获取发送获取图片验证码的请求:/verification.token?type=image_code&...
                // 并想办法提示用户
                private String tokenType = "image_code";
                // 用户看到显示的验证码之后需要输入验证码，输入的验证码将作为一个参数该在请求认证时一并提交，例如："verification-token"
                private String tokenParameter = "verification-token";
            }
        }

        @Data static class SmsCodeLogin implements Serializable {
            // 获取时需要提交的验证码类型，例如："sms_code"
            private String smsCodeTokenTypeParameter;
            // 获取时需要额外提交的“发送到哪个手机号码”，例如："mobile"
            private String mobileParameter;
            // 最终提交时将一个参数提交，例如："verification-token"
            private String smsCodeParameter;

            // 最终按下登录按钮后将表单的数据提交到哪，例如："/authenticate/sms_code"
            private String processingUrl;

        }

        //    private boolean openIdLoginEnabled;
//    private String  openIdUsernameParameter;
//    private String  openIdRememberMeParameter;
//    private String  openIdAuthenticationUrl;
//
//    private boolean socialLoginEnabled;
//    private Map<String, String> oauth2AuthorizationUrlToClientName;

        @Data
        static class LoginError {
            private String message;
            private String authMode;
        }

    }

}
