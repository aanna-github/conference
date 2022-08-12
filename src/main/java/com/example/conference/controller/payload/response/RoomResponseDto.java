package com.example.conference.controller.payload.response;

import com.example.conference.controller.payload.RoomAvailabilityDto;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDto {
    @NonNull
    private String id;

    @NonNull
    private Integer number;

    @NonNull
    private Integer seatsCount;

    @NonNull
    private Integer floor;

    @NonNull
    private AddressResponseDto address;

    private List<RoomAvailabilityDto> roomAvailability;
}
