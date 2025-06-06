package com.modules.invoicer.user.application;

import com.modules.invoicer.user.domain.User;
import com.modules.invoicer.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerNewUser(User user) {
        logger.info("Registering new user with username: {}", user.getUsername());
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.info("User {} registered", user.getUsername());
        return userRepository.save(user);
    }

    @Async
    public CompletableFuture<User> registerNewUserAsync(User user) {
        logger.info("Asynchronously registering user");
        return CompletableFuture.completedFuture(registerNewUser(user));
    }

    public Optional<User> findByUsername(String username) {
        logger.info("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Async
    public CompletableFuture<Optional<User>> findByUsernameAsync(String username) {
        logger.info("Asynchronously finding user by username: {}", username);
        return CompletableFuture.completedFuture(findByUsername(username));
    }

    public Optional<User> findById(Long id) {
        logger.info("Finding user by id: {}", id);
        return userRepository.findById(id);
    }

    @Async
    public CompletableFuture<Optional<User>> findByIdAsync(Long id) {
        logger.info("Asynchronously finding user by id: {}", id);
        return CompletableFuture.completedFuture(findById(id));
    }

    @Transactional
    public User updateUserDetails(User user) {
        logger.info("Updating user details for id: {}", user.getId());
        user.setCreatedAt(LocalDateTime.now());
        logger.info("User {} details updated", user.getId());
        return userRepository.save(user);
    }

    @Async
    public CompletableFuture<User> updateUserDetailsAsync(User user) {
        logger.info("Asynchronously updating user details for id: {}", user.getId());
        return CompletableFuture.completedFuture(updateUserDetails(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }

    @Async
    public CompletableFuture<Void> deleteUserAsync(Long id) {
        logger.info("Asynchronously deleting user with id: {}", id);
        deleteUser(id);
        return CompletableFuture.completedFuture(null);
    }
}
