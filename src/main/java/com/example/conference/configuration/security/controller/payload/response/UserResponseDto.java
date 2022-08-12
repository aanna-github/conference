package com.example.conference.configuration.security.controller.payload.response;

import com.example.conference.configuration.security.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private Set<Role> roles = new HashSet<>();
}
