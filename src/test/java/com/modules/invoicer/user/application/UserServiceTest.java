package com.modules.invoicer.user.application;

import com.modules.invoicer.user.domain.User;
import com.modules.invoicer.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void registerNewUserPersistsUserWithEncodedPassword() {
        User user = User.builder().username("u").email("e@example.com").password("p").build();
        when(userRepository.existsByUsername("u")).thenReturn(false);
        when(userRepository.existsByEmail("e@example.com")).thenReturn(false);
        when(passwordEncoder.encode("p")).thenReturn("enc");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.registerNewUser(user);

        assertThat(result.getPassword()).isEqualTo("enc");
        verify(userRepository).save(user);
    }

    @Test
    void registerNewUserThrowsWhenUsernameExists() {
        User user = User.builder().username("u").email("e@example.com").password("p").build();
        when(userRepository.existsByUsername("u")).thenReturn(true);
        assertThatThrownBy(() -> userService.registerNewUser(user))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateUserDetailsSetsTimestampAndSaves() {
        User user = new User();
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        User result = userService.updateUserDetails(user);
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void updateUserDetailsAsyncDelegates() {
        User user = new User();
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        CompletableFuture<User> future = userService.updateUserDetailsAsync(user);
        assertThat(future.join().getCreatedAt()).isNotNull();
    }
}
