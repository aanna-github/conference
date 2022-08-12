package com.example.conference.configuration.security.controller;

import com.example.conference.configuration.security.config.JwtUtils;
import com.example.conference.configuration.security.controller.payload.request.LoginRequestDto;
import com.example.conference.configuration.security.controller.payload.request.SignupRequestDto;
import com.example.conference.configuration.security.controller.payload.response.JwtResponseDto;
import com.example.conference.configuration.security.controller.payload.response.MessageResponseDto;
import com.example.conference.configuration.security.controller.payload.response.UserResponseDto;
import com.example.conference.configuration.security.service.UserDetailsImpl;
import com.example.conference.configuration.security.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return ResponseEntity.ok(JwtResponseDto.builder()
                .token(jwt)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles).build());
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponseDto> registerUser(@Valid @RequestBody SignupRequestDto signUpRequestDto) {
        if (userService.isExistsByUsername(signUpRequestDto.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Username is already taken."));
        }

        if (userService.isExistsByEmail(signUpRequestDto.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Email is already in user."));
        }

        userService.save(signUpRequestDto);
        final MessageResponseDto messageResponseDto = new MessageResponseDto("User registered successfully.");
        log.info(messageResponseDto.getMessage());

        return ResponseEntity.ok(messageResponseDto);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Bearer ", authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        final List<UserResponseDto> allUsers = userService.getAllUsers();
        if (allUsers == null || allUsers.isEmpty()) {
            log.warn("There is no registered user!");
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(allUsers);
    }
}
