/*
 * Copyright (c) 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.watchdog.security.web.verification;

import org.springframework.lang.NonNull;
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
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        for (VerificationProvider provider : providers) {
            boolean requiredAndFailed = !provider.tryVerify(request, response);
            if (requiredAndFailed) return;
        }

        filterChain.doFilter(request, response);
    }



    public void addProvider(VerificationProvider provider) {
        this.providers.add(Objects.requireNonNull(provider));
    }

    public void addProviders(List<VerificationProvider> providers) {
        this.providers.addAll(Objects.requireNonNull(providers));
    }

    public List<VerificationProvider> getProviders() {
        return providers;
    }

}