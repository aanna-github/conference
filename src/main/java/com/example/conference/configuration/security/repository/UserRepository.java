package com.example.conference.configuration.security.repository;

import com.example.conference.configuration.security.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
        Optional<User> findByUsername(String username);

        boolean existsByUsername(String username);

        boolean existsByEmail(String email);
}
