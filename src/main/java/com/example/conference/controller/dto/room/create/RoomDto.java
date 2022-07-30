package com.example.conference.controller.dto.room.create;

import com.example.conference.controller.dto.room.RoomAvailabilityDto;
import com.example.conference.controller.dto.room.create.AddressDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    @NotNull
    @Min(1)
    private Integer number;

    @NotNull
    @Min(2)
    private Integer seatsCount;

    @NotNull
    @Min(1)
    private Integer floor;

    @NotNull
    private AddressDto address;

    @JsonIgnore
    private List<RoomAvailabilityDto> roomAvailability;
}
