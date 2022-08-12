package com.example.conference.controller.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomUpdateDto {
    @Min(1)
    private Integer number;

    @Min(2)
    private Integer seatsCount;
}
