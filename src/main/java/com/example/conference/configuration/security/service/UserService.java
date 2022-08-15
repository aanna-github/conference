package com.example.conference.configuration.security.service;

import com.example.conference.configuration.security.config.JwtUtils;
import com.example.conference.configuration.security.controller.payload.request.LoginRequestDto;
import com.example.conference.configuration.security.controller.payload.request.SignupRequestDto;
import com.example.conference.configuration.security.controller.payload.response.JwtResponseDto;
import com.example.conference.configuration.security.controller.payload.response.UserResponseDto;
import com.example.conference.configuration.security.domain.Role;
import com.example.conference.configuration.security.domain.RoleEnum;
import com.example.conference.configuration.security.domain.User;
import com.example.conference.configuration.security.repository.RoleRepository;
import com.example.conference.configuration.security.repository.UserRepository;
import com.example.conference.exception.DocumentNotFoundException;
import com.example.conference.exception.InvalidInputException;
import com.example.conference.utility.mapper.CommonMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final CommonMapper commonMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private static final String ROLE_NOT_FOUND = "Role is not found: ";

    public boolean isExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void save(SignupRequestDto signUpRequestDto) {
        User user = User.builder()
                .username(signUpRequestDto.getUsername())
                .email(signUpRequestDto.getEmail())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .build();

        Set<String> signUpRequestRoles = signUpRequestDto.getRole();
        Set<Role> roles = new HashSet<>();

        if (signUpRequestRoles == null) {
            Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                    .orElseThrow(() -> new DocumentNotFoundException(ROLE_NOT_FOUND + RoleEnum.ROLE_USER));
            roles.add(userRole);
        } else {
            signUpRequestRoles.forEach(role -> {
                switch (role) {
                    case "USER":
                        Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                                .orElseThrow(() -> new DocumentNotFoundException(ROLE_NOT_FOUND + RoleEnum.ROLE_USER));
                        roles.add(userRole);
                        break;
                    case "ADMIN":
                        Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
                                .orElseThrow(() -> new DocumentNotFoundException(ROLE_NOT_FOUND + RoleEnum.ROLE_ADMIN));
                        roles.add(adminRole);
                        break;
                    case "MODERATOR":
                        Role modRole = roleRepository.findByName(RoleEnum.ROLE_MODERATOR)
                                .orElseThrow(() -> new DocumentNotFoundException(ROLE_NOT_FOUND + RoleEnum.ROLE_MODERATOR));
                        roles.add(modRole);
                        break;
                    default:
                        final RoleEnum byName = RoleEnum.findByName(role);
                        if (byName == null) {
                            throw new InvalidInputException("There is no role named: " + role);
                        } else {
                            Role roleByName = roleRepository.findByName(byName)
                                    .orElseThrow(() -> new DocumentNotFoundException(ROLE_NOT_FOUND + RoleEnum.ROLE_USER));
                            roles.add(roleByName);
                        }
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream().map(commonMapper::daoToUserResponseDto)
                .collect(Collectors.toList());
    }

    public JwtResponseDto authenticateUser(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JwtResponseDto.builder()
                .token(jwtUtils.generateJwtToken(authentication))
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles).build();
    }
}
