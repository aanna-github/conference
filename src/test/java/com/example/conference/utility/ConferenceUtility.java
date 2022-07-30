package com.example.conference.utility;

import com.example.conference.constants.TestMockValueConstants;
import com.example.conference.controller.dto.conference.create.ConferenceDto;
import com.example.conference.controller.dto.conference.create.ParticipantDto;
import com.example.conference.controller.dto.conference.update.ConferenceUpdateDto;
import com.example.conference.dao.document.Conference;
import com.example.conference.dao.document.Participant;
import com.example.conference.utility.enumeration.ConferenceStatus;
import org.bson.BsonTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.UUID;

public class ConferenceUtility {

    public static ConferenceDto buildConferenceDtoWithRequiredProp() {
        return ConferenceDto.builder()
                .description(TestMockValueConstants.DESCRIPTION_MOCK_VALUE)
                .requestedSeatsCount(TestMockValueConstants.REQUESTED_SEATS_COUNT_MOCK_VALUE)
                .eventDate(LocalDateTime.now())
                .build();
    }

    public static Conference buildConferenceWithRequiredProp() {
        return Conference.builder()
                .participants(new LinkedHashSet<>())
                .status(ConferenceStatus.SCHEDULED.name())
                .description(TestMockValueConstants.DESCRIPTION_MOCK_VALUE)
                .requestedSeatsCount(TestMockValueConstants.REQUESTED_SEATS_COUNT_MOCK_VALUE)
                .eventDate(new BsonTimestamp((int) Instant.now().getEpochSecond(), 1))
                .build();
    }

    public static ConferenceUpdateDto buildConferenceUpdateDto(Integer requestedSeatsCount, String status, LocalDateTime eventDate, String description) {
        ConferenceUpdateDto conferenceUpdateDto = ConferenceUpdateDto.builder().build();
        if (requestedSeatsCount != null) {
            conferenceUpdateDto.setRequestedSeatsCount(requestedSeatsCount);
        }
        if (status != null) {
            conferenceUpdateDto.setStatus(status);
        }
        if (eventDate != null) {
            conferenceUpdateDto.setEventDate(eventDate);
        }
        if (description != null) {
            conferenceUpdateDto.setDescription(description);
        }
        return conferenceUpdateDto;
    }

    public static ParticipantDto buildParticipantDto() {
        return ParticipantDto.builder()
                .firstName(TestMockValueConstants.FIRSTNAME_MOCK_VALUE)
                .lastName(TestMockValueConstants.LASTNAME_MOCK_VALUE)
                .age(TestMockValueConstants.AGE_MOCK_VALUE)
                .invitedBy(TestMockValueConstants.INVITED_BY_MOCK_VALUE)
                .companyName(TestMockValueConstants.COMPANY_NAME_MOCK_VALUE)
                .specialization(TestMockValueConstants.SPECIALIZATION_MOCK_VALUE)
                .build();
    }

    public static Participant buildParticipant() {
        return Participant.builder()
                .id(UUID.randomUUID().toString())
                .firstName(TestMockValueConstants.FIRSTNAME_MOCK_VALUE)
                .lastName(TestMockValueConstants.LASTNAME_MOCK_VALUE)
                .age(TestMockValueConstants.AGE_MOCK_VALUE)
                .invitedBy(TestMockValueConstants.INVITED_BY_MOCK_VALUE)
                .companyName(TestMockValueConstants.COMPANY_NAME_MOCK_VALUE)
                .specialization(TestMockValueConstants.SPECIALIZATION_MOCK_VALUE)
                .build();
    }
}
