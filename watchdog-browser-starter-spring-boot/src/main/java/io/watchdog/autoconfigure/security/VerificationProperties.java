package io.watchdog.autoconfigure.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "watchdog.verification")
@Getter @Setter
public class VerificationProperties implements InitializingBean {

    private Service service = new Service();

    @Data
    public static class Service {
        private String acquiresTokenUrl         = "/verification.token";
        private String tokenTypeParameter       = "type";
        private String businessParameter        = "for";
        private ImageCodeService imageCode = new ImageCodeService();
        private SmsCodeService smsCode     = new SmsCodeService();

        @Getter @Setter
        public static class ImageCodeService {
            private String supportsTokenType = "image_code";
            private int seqLength = 4;          // 验证码序列的长度
            private int expireIn  = 60 * 5;     // 图片验证码有效期,单位秒,默认5分钟

            private int defaultImageWidth   = 67;      // 输出的图片的宽（像素）
            private int defaultImageHeight  = 23;      // 输出的图片的长（像素）

            private Repository repository = new Repository("VERIFICATION_IMAGE_CODE");

            @Getter @Setter @AllArgsConstructor
            public static class Repository {
                private String tokenAttribute;
            }
        }

        @Getter @Setter
        public static class SmsCodeService {
            private String supportsTokenType    = "sms_code";
            private int seqLength   = 6;        // 验证码序列的长度
            private int expireIn    = 60 * 15;  // 短信验证码的有效期,单位秒,默认15分钟

            private String toMobileParameter = "to-mobile";  // 请求参数的名称，该参数指定要发送到哪个手机号码

            private Repository repository = new Repository("VERIFICATION_SMS_CODE");
            @Getter @Setter @AllArgsConstructor
            public static class Repository {
                private String tokenAttribute;
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
