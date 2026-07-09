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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService implements UserDetailsService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger =
        LoggerFactory.getLogger(UserService.class);
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
    
        if (userRepository.existsByEmail(request.email())) {
            logger.warn( "Registration failed. Email '{}' already exists.",request.email());
            throw new UserAlreadyExistsException("Email already registered.");
        }
        logger.warn("Registration failed. Display name '{}' already exists.",request.displayName());
        if (userRepository.existsByDisplayName(request.displayName())) {
            logger.warn("Registration failed. Display name '{}' already exists.",request.displayName());
            throw new UserAlreadyExistsException("Username already taken.");
        }
        User user = new User();
        user.setDisplayName(request.displayName());
        user.setEmail(request.email());
        // Password hashing will come in Sprint 14.5
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCreatedAt(Instant.now());
        User savedUser = userRepository.save(user);
        logger.info("New user registered: {}", savedUser.getEmail());
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
