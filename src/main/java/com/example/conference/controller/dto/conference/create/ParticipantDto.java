package com.example.conference.controller.dto.conference.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto {
    @NotNull
    @Size(min = 3, max = 25)
    private String firstName;

    @NotNull
    @Size(min = 3, max = 25)
    private String lastName;

    @Min(18)
    @NotNull
    private Integer age;

    private String gender;

    private String residence;

    @NotNull
    private String companyName;

    @NotNull
    private String specialization;

    private String motivation;

    @NotNull
    private String invitedBy;
}
