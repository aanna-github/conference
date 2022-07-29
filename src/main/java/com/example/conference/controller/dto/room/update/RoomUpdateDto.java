package com.example.conference.controller.dto.room.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class RoomUpdateDto {
    @Min(1)
    private Integer number;

    @Min(1)
    private Integer seatsCount;
}
