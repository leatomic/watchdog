package io.watchdog.security.web.authentication;

import io.watchdog.security.authentication.UsernameAuthenticationProvider;
import io.watchdog.security.authentication.UsernameAuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证过滤器： 直接在请求中读取指定的属性值，将其作为用户名构建{@link UsernameAuthenticationToken}
 * 并交由{@link UsernameAuthenticationProvider}认证
 */
public class UsernameAttributeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private String usernameAttribute;

    // ~ Constructor
    // =================================================================================================================
    public UsernameAttributeAuthenticationFilter(RequestMatcher requestMatcher, String usernameAttribute) {

        super(requestMatcher);

        if (StringUtils.isBlank(usernameAttribute)) {
            throw new IllegalArgumentException("usernameAttribute can not be blank");
        }
        this.usernameAttribute = usernameAttribute;
    }

    // ~
    // =================================================================================================================
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = obtainUsername(request);

        UsernameAuthenticationToken authRequest = new UsernameAuthenticationToken(username);
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);

    }

    private String obtainUsername(HttpServletRequest request) {

        Object username = request.getAttribute(usernameAttribute);
        if (username == null) {
            throw new IllegalStateException(
                    "Request has been verified, " +
                            "but username attribute: " + usernameAttribute + " not set after verification succeed"
            );
        }

        return username.toString();
    }

    protected void setDetails(HttpServletRequest request, UsernameAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }



    // ~ Getter Setter
    // =================================================================================================================

    public String getUsernameAttribute() {
        return usernameAttribute;
    }

    public void setUsernameAttribute(String usernameAttribute) {
        if (StringUtils.isBlank(usernameAttribute)) {
            throw new IllegalArgumentException("usernameAttribute can not be blank");
        }
        this.usernameAttribute = usernameAttribute;
    }
}
