package com.example.conference.configuration.security.repository;

import java.util.Optional;

import com.example.conference.configuration.security.domain.Role;
import com.example.conference.configuration.security.domain.RoleEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(RoleEnum name);
}