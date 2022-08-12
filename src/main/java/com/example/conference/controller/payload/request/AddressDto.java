package com.example.conference.controller.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
