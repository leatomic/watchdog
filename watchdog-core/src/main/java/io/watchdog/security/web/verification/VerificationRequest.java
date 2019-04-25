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
