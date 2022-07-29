package com.example.conference.controller.dto.conference.response;

import com.example.conference.controller.dto.room.response.RoomResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceResponseDto {
    @NotNull
    private String id;

    @NotNull
    private Integer requestedSeatsCount;

    @NotNull
    private String status;

    @NotNull
    private LocalDateTime eventDate;

    private Set<ParticipantResponseDto> participants;

    private RoomResponseDto room;

    @NotNull
    private String description;
}
