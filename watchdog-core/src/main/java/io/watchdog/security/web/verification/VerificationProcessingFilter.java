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

    public VerificationProcessingFilter() {

    }

    public VerificationProcessingFilter(List<VerificationProvider> providers) {
        this.providers = Objects.requireNonNull(providers);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        for (VerificationProvider provider : providers) {
            boolean requiredAndFailed = !provider.tryVerify(request, response);
            if (requiredAndFailed) return;
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

}