package com.modules.invoicer.user.application;

import com.modules.invoicer.user.domain.User;
import com.modules.invoicer.user.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerNewUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Async
    public CompletableFuture<User> registerNewUserAsync(User user) {
        return CompletableFuture.completedFuture(registerNewUser(user));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Async
    public CompletableFuture<Optional<User>> findByUsernameAsync(String username) {
        return CompletableFuture.completedFuture(findByUsername(username));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Async
    public CompletableFuture<Optional<User>> findByIdAsync(Long id) {
        return CompletableFuture.completedFuture(findById(id));
    }

    @Transactional
    public User updateUserDetails(User user) {
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Async
    public CompletableFuture<User> updateUserDetailsAsync(User user) {
        return CompletableFuture.completedFuture(updateUserDetails(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Async
    public CompletableFuture<Void> deleteUserAsync(Long id) {
        deleteUser(id);
        return CompletableFuture.completedFuture(null);
    }
}
