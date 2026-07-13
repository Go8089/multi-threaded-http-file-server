package com.goMaddy.multithreaded_http_fileserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goMaddy.multithreaded_http_fileserver.dto.AuthenticationResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.LoginRequest;
import com.goMaddy.multithreaded_http_fileserver.service.AuthenticationService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

@Test
void login_ShouldReturnToken() throws Exception {

    LoginRequest request =
            new LoginRequest(
                    "vi@gmail.com",
                    "password"
            );

    AuthenticationResponse response =
            new AuthenticationResponse(
                    "jwt-token"
            );

    when(authenticationService.login(any()))
            .thenReturn(response);

    mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))

            .andExpect(status().isOk())

            .andExpect(jsonPath("$.token")
                    .value("jwt-token"));

 }

}
