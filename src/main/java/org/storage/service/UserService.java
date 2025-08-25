package org.storage.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.storage.repository.UserRepository;
import org.storage.repository.entity.UserEntity;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UUID create() {
        UserEntity userEntity = new UserEntity();
        return userRepository.save(userEntity).getId();
    }

    @Transactional
    public UUID create(UUID id) {
        UserEntity userEntity = new UserEntity(id);
        return userRepository.save(userEntity).getId();
    }

    public boolean userExists(UUID userId) {
        System.out.println("Checking if user exists " + userId);
        return userRepository.findById(userId).isPresent();
    }
}
