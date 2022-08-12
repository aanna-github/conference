package com.example.conference.controller.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceDto {
    @NotNull
    @Min(2)
    private Integer requestedSeatsCount;

    @NotNull
    private LocalDateTime eventDate;

    private String roomId;

    @NotNull
    @Size(min = 7, max = 100)
    private String description;
}
