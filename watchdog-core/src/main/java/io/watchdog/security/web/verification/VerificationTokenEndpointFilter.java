package io.watchdog.security.web.verification;

import io.watchdog.security.verification.TokenServiceException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>客户端获取验证码的请求的端点。那么，是如何为客户端提供服务的呢？
 * <p>  统一的验证码获取url：/verification.token,
 *      不同类型的验证码服务将根据参数type来区分，
 *      然后同种类型不同业务的验证码服务将根据参数for来区分
 * </p>
 */
@Slf4j
@Getter @Setter
public class VerificationTokenEndpointFilter extends OncePerRequestFilter {
    private RequestMatcher acquiresTokenRequestMatcher;
    private String tokenTypeParameter = "type";
    private String businessParameter = "for";
    private ConcurrentMap<VerificationRequest.Type, VerificationRequestHandler> tokenRequestHandlerMap;

    public VerificationTokenEndpointFilter(RequestMatcher acquiresTokenRequestMatcher,
                                           List<VerificationRequestHandler> handlers) {

        this.acquiresTokenRequestMatcher = acquiresTokenRequestMatcher;

        tokenRequestHandlerMap = new ConcurrentHashMap<>((int) Math.ceil(handlers.size()/0.75));
        for (VerificationRequestHandler handler : handlers) {
            VerificationRequest.Type supportsRequestType = handler.getService().getSupportsRequestType();
            tokenRequestHandlerMap.put(supportsRequestType, handler);
        }
    }

    /**
     * <p>  基于以下原因：</p>
     * <ul>
     *     <li>后续将输出token时‘可能’需要用到{@link HttpServletResponse}</li>
     *     <li>保存token时‘可能’需要将token保存到{@link HttpSession}</li>
     *     <li>校验以及后续‘可能’还需要从{@link HttpSession}中获取token相关信息</li>
     * </ul>
     * <p>
     *     为了避免引入非必须的参数造成对接口方法的污染，
     *     后续需要{@link HttpServletRequest}、{@link HttpSession}或{@link HttpServletResponse}的场合
     *     我们将通过{@link RequestContextHolder#currentRequestAttributes()}来获得
     * </p>
     * <br/>
     *
     * <p>
     *     {@link RequestContextHolder}通常由{@link RequestContextListener}、{@link RequestContextFilter}
     *     或者{@link org.springframework.web.servlet.DispatcherServlet}来初始化
     *
     * <p>
     *     然而,
     * <ol>
     *     <li>注册{@link RequestContextListener}将只能获取得到{@link HttpServletRequest}，而无法获取到
     *              {@link HttpServletResponse}
     *     </li>
     *     <li>注册{@link RequestContextFilter}则虽然能获取到{@link HttpServletResponse}，但其被注册在
     *              springSecurityFilterChain之后，因此我们无法及时获取到
     *     </li>
     *     <li>{@link DispatcherServlet}中注入的作用域则更靠后</li>
     * </ol>
     * <p>
     *     因此，在此将预先额外初始化和后置地重置{@link RequestContextHolder#setRequestAttributes(RequestAttributes, boolean)}
     * </p>
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        initRequestResponseContextHolder(request, response);
        try {

            if (!acquiresTokenRequestMatcher.matches(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Request acquires verification token");
            }

            String tokenType    = obtainTypeOfTokenAcquired(request);
            String business     = obtainBusinessFor(request);

            VerificationRequest verificationRequest = new VerificationRequest(tokenType, business, request.getParameterMap());
            VerificationRequestHandler handler = determineRequestHandler(verificationRequest);
            handler.handle(verificationRequest, request, response);

        }
        finally {
            resetRequestResponseContextHolder();
        }

    }

    private VerificationRequestHandler determineRequestHandler(VerificationRequest verificationRequest) {

        VerificationRequestHandler handler = tokenRequestHandlerMap.get(verificationRequest.getType());

        if (handler == null) {
            throw new TokenServiceException("no handler matched for token request: " + verificationRequest);
        }

        return handler;
    }

    /**
     * 获取request中提交上来的（要申请的）token类型，存储在参数中
     * @param request 申请要分配token的Http请求
     * @return token类型
     */
    private String obtainTypeOfTokenAcquired(HttpServletRequest request) {
        String tokenType = request.getParameter(tokenTypeParameter);

        if (tokenType == null) {
            throw new TokenServiceException("token type parameter not found");
        } else if (tokenType.isEmpty()) {
            throw new TokenServiceException("invalid token type: " + tokenType);
        }

        return tokenType;
    }

    private String obtainBusinessFor(HttpServletRequest request) {
        String business = request.getParameter(businessParameter);
        return StringUtils.isNotBlank(business) ? business : VerificationService.DEFAULT_BUSINESS;
    }

    private static void initRequestResponseContextHolder(HttpServletRequest request, HttpServletResponse response) {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response), false);
    }

    private static void resetRequestResponseContextHolder() {
        RequestContextHolder.resetRequestAttributes();
    }

}
