package com.example.conference.controller.dto.conference.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
    private String description;
}
