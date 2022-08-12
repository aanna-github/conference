package com.example.conference.configuration.security.controller.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDto {
    private String token;

    private String type = "Bearer";

    private String id;

    private String username;

    private String email;

    private List<String> roles;
}
