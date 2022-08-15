package com.example.conference.configuration.security.controller;

import com.example.conference.configuration.security.controller.payload.request.LoginRequestDto;
import com.example.conference.configuration.security.controller.payload.request.SignupRequestDto;
import com.example.conference.configuration.security.controller.payload.response.JwtResponseDto;
import com.example.conference.configuration.security.controller.payload.response.MessageResponseDto;
import com.example.conference.configuration.security.controller.payload.response.UserResponseDto;
import com.example.conference.configuration.security.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    @ApiOperation(value = "To login, put a token from a response to be authenticated for other requests")
    public ResponseEntity<JwtResponseDto> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        final JwtResponseDto jwtResponseDto = userService.authenticateUser(loginRequestDto);
        if (jwtResponseDto == null) {
            log.error("Error occurred during authentication");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(jwtResponseDto);
    }

    @PostMapping("/signup")
    @ApiOperation(value = "To register a new user", notes = "Available roles are: USER, MODERATOR, ADMIN")
    public ResponseEntity<MessageResponseDto> registerUser(@Valid @RequestBody SignupRequestDto signUpRequestDto) {
        if (userService.isExistsByUsername(signUpRequestDto.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Username is already taken"));
        }

        if (userService.isExistsByEmail(signUpRequestDto.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Email iis already taken"));
        }

        userService.save(signUpRequestDto);
        final MessageResponseDto messageResponseDto = new MessageResponseDto("User registered successfully");
        log.info(messageResponseDto.getMessage());

        return ResponseEntity.ok(messageResponseDto);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "To see registered users", authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        final List<UserResponseDto> allUsers = userService.getAllUsers();
        if (allUsers == null || allUsers.isEmpty()) {
            log.warn("There is no registered user");
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(allUsers);
    }
}
