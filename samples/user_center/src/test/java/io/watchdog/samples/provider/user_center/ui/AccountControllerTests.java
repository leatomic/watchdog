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
