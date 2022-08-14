package com.example.conference.configuration.bean;

import com.example.conference.configuration.security.domain.Role;
import com.example.conference.configuration.security.domain.RoleEnum;
import com.example.conference.configuration.security.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class MongoInitConfig {

    @Bean
    CommandLineRunner preLoadMongo(RoleRepository roleRepository) {
        final List<Role> availableRoles = roleRepository.findAll();
        if (availableRoles.isEmpty()) {
            log.info("There is no available roles in the db: ");
        } else {
            log.info("Available roles are: ");
            log.info(availableRoles.toString());
        }

        List<Role> rolesToAdd = new ArrayList<>();
        try {
            for (RoleEnum roleValue : RoleEnum.values()) {
                if (!roleRepository.findByName(roleValue).isPresent()) {
                    rolesToAdd.add(Role.builder().name(roleValue).build());
                }
            }
        } catch (Exception e) {
            log.error("Unable to init database for role values");
        }

        if (rolesToAdd.isEmpty()) {
            log.info("There is no new role be added to the database");
        } else {
            log.info("The following roles will be added to the database: {}", rolesToAdd);

        }
        return arg -> roleRepository.saveAll(rolesToAdd);
    }
}
