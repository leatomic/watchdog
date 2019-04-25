package io.watchdog.samples.provider.user_center.ui;

import io.watchdog.samples.provider.user_center.UserCenterApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("测试UserController中的方法")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserCenterApplication.class)
public class AccountControllerTests {

    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

//    @Test
//    void whenDetectPhoneWithUnavailable_thenReturnOkAnd() throws Exception {
//        mockMvc.perform(
//                get("/sign-up/mobilePhone/mobilePhone.available")
//                        .param("mobilePhone", "13570666666")
//                        .contentType(MediaType.APPLICATION_JSON_UTF8)
//        )
//        .andExpect(status().isOk())
//        .andExpect(content().json("{id:duplicated-mobilePhone, message:该手机号码已被用于注册，无法用于注册新帐号, content:null}"));
//    }
//
//    @Test
//    void whenDetectPhoneWithAvailable_thenReturnOkAnd() throws Exception {
//        mockMvc.perform(
//                get("/sign-up/mobilePhone/mobilePhone.available")
//                        .param("mobilePhone", "13570666667")
//                        .contentType(MediaType.APPLICATION_JSON_UTF8)
//        )
//                .andExpect(status().isOk())
//                .andExpect(content().json("{id:mobilePhone.available, message:null, content:null}"));
//    }


}
