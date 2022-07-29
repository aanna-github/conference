package com.example.conference.controller.dto.room.create;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    @NotNull
    private String addressLine1;

    @NotNull
    private String addressLine2;

    @NotNull
    private String timezone;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;
}
