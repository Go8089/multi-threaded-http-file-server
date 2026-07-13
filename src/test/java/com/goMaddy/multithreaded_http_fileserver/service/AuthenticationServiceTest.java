package com.goMaddy.multithreaded_http_fileserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.goMaddy.multithreaded_http_fileserver.dto.AuthenticationResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.LoginRequest;
import com.goMaddy.multithreaded_http_fileserver.entity.User;
import com.goMaddy.multithreaded_http_fileserver.repository.UserRepository;
import com.goMaddy.multithreaded_http_fileserver.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

 @Test
 void login_ShouldReturnJwt_WhenCredentialsAreValid() {

    User user = new User();
    user.setEmail("vi@gmail.com");
    user.setPassword("encoded");

    LoginRequest request =
            new LoginRequest("vi@gmail.com","password");

    when(userService.getUserByEmail("vi@gmail.com"))
            .thenReturn(user);

    when(passwordEncoder.matches("password","encoded"))
            .thenReturn(true);

    when(jwtService.generateToken(user))
            .thenReturn("jwt-token");

    AuthenticationResponse response =
            authenticationService.login(request);

    assertEquals("jwt-token", response.token());

    verify(userService).getUserByEmail("vi@gmail.com");
    verify(jwtService).generateToken(user);
 } 
 
@Test
void login_ShouldThrowException_WhenPasswordIsWrong() {

    User user = new User();
    user.setEmail("vi@gmail.com");
    user.setPassword("encoded");

    LoginRequest request =
            new LoginRequest("vi@gmail.com","wrong");

    when(userService.getUserByEmail("vi@gmail.com"))
            .thenReturn(user);

    when(passwordEncoder.matches("wrong","encoded"))
            .thenReturn(false);

    assertThrows(
            BadCredentialsException.class,
            () -> authenticationService.login(request)
    );

    verify(jwtService, never()).generateToken(any());
 }
}