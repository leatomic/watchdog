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

import lombok.*;

import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
public class VerificationRequest {

    private Type type;
    private Map<String, String[]> params;

    public VerificationRequest(String tokenType, String business, Map<String, String[]> params) {
        this.type = new Type(tokenType, business);
        this.params = params;
    }



    @AllArgsConstructor
    @Getter
    @Setter
    public static class Type {
        private String tokenType;
        private String business;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Type)) return false;
            Type type = (Type) o;
            return  (tokenType == type.getTokenType()
                        || tokenType != null && tokenType.equalsIgnoreCase(type.getTokenType())) &&
                    (business == type.getBusiness()
                        || business != null && business.equalsIgnoreCase(type.getBusiness()));
        }

        @Override
        public int hashCode() {
            return Objects.hash(getTokenType(), getBusiness());
        }
    }

}
