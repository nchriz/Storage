package org.storage.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.storage.service.UserService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void testCreateUserReturnsUuid() {
        UUID fakeUuid = UUID.randomUUID();
        when(userService.create()).thenReturn(fakeUuid);

        ResponseEntity<String> response = userController.createUser();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(fakeUuid.toString(), response.getBody());

        verify(userService, times(1)).create();
    }
}
