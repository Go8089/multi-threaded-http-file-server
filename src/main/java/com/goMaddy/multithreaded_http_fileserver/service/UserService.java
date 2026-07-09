package com.goMaddy.multithreaded_http_fileserver.service;


import com.goMaddy.multithreaded_http_fileserver.dto.UserRegistrationRequest;
import com.goMaddy.multithreaded_http_fileserver.dto.UserResponse;
import com.goMaddy.multithreaded_http_fileserver.entity.User;
import com.goMaddy.multithreaded_http_fileserver.exception.ResourceNotFoundException;
import com.goMaddy.multithreaded_http_fileserver.exception.UserAlreadyExistsException;
import com.goMaddy.multithreaded_http_fileserver.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UserService implements UserDetailsService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already registered.");
        }
        if (userRepository.existsByDisplayName(request.displayName())) {
            throw new UserAlreadyExistsException("Username already taken.");
        }
        User user = new User();
        user.setDisplayName(request.displayName());
        user.setEmail(request.email());
        // Password hashing will come in Sprint 14.5
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCreatedAt(Instant.now());
        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId(),
                savedUser.getDisplayName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }
    @Override
public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException {

    return userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new UsernameNotFoundException("User not found"));
}
    @Transactional(readOnly = true)
    public User findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));
    }
    @Transactional(readOnly = true)
public User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found"));
}
}
