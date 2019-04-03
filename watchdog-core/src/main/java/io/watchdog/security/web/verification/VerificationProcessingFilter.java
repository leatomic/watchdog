package io.watchdog.security.web.verification;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VerificationProcessingFilter extends OncePerRequestFilter {

    private List<VerificationProvider> providers = new ArrayList<>();
    private VerificationFailureHandler verificationFailureHandler;

    public VerificationProcessingFilter(VerificationFailureHandler verificationFailureHandler) {
        this.verificationFailureHandler = Objects.requireNonNull(verificationFailureHandler);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            for (VerificationProvider provider : providers) {
                provider.verifyIfNecessary(request);
            }
        }
        catch (InternalVerificationException ive) {
            logger.error(ive.getMessage());
            verificationFailureHandler.onVerificationFailure(request, response, ive);
            return;
        }
        catch (VerificationException ve) {
            // At least one of the verifications failed
            verificationFailureHandler.onVerificationFailure(request, response, ve);
            return;
        }

        filterChain.doFilter(request, response);
    }



    public List<VerificationProvider> getProviders() {
        return providers;
    }

    public void addProvider(VerificationProvider provider) {
        this.providers.add(Objects.requireNonNull(provider));
    }

    public void addProviders(List<VerificationProvider> providers) {
        this.providers.addAll(Objects.requireNonNull(providers));
    }

    public VerificationFailureHandler getVerificationFailureHandler() {
        return verificationFailureHandler;
    }

    public void setVerificationFailureHandler(VerificationFailureHandler verificationFailureHandler) {
        this.verificationFailureHandler = Objects.requireNonNull(verificationFailureHandler);
    }
}