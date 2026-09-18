package com.crisprate.crisprate;

import com.crisprate.crisprate.entity.User;
import com.crisprate.crisprate.repository.UserRepository;
import com.crisprate.crisprate.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_throwsException_whenUsernameTaken() {
        when(userRepository.existsByUsername("tyler")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                userService.register("tyler", "tyler@example.com", "password123"));
    }

    @Test
    void register_throwsException_whenEmailTaken() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                userService.register("newuser", "taken@example.com", "password123"));
    }

    @Test
    void register_hashesPasswordBeforeSaving_notPlainText() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$fakeHashedValue");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.register("newuser", "new@example.com", "password123");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("$2a$10$fakeHashedValue", savedUser.getPasswordHash());
        assertNotEquals("password123", savedUser.getPasswordHash());
    }

}
