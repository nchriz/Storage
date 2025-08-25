package org.storage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.storage.repository.UserRepository;
import org.storage.repository.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void testCreateWithoutId() {
        UserEntity savedUser = new UserEntity();
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        UUID result = userService.create();

        assertEquals(savedUser.getId(), result);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testCreateWithId() {
        UUID customId = UUID.randomUUID();
        UserEntity savedUser = new UserEntity(customId);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        UUID result = userService.create(customId);

        assertEquals(customId, result);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testUserExistsTrue() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));

        boolean exists = userService.userExists(userId);

        assertTrue(exists);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testUserExistsFalse() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        boolean exists = userService.userExists(userId);

        assertFalse(exists);
        verify(userRepository, times(1)).findById(userId);
    }
}
