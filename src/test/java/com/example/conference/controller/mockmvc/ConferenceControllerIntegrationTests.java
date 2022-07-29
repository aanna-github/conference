package com.example.conference.controller.mockmvc;

import com.example.conference.ConferenceRoomApiSpringBootContextLoader;
import com.example.conference.constants.TestRequestConstants;
import com.example.conference.constants.TestJsonPathConstants;
import com.example.conference.constants.TestMockValueConstants;
import com.example.conference.controller.dto.conference.create.ConferenceDto;
import com.example.conference.controller.dto.conference.create.ParticipantDto;
import com.example.conference.controller.dto.conference.update.ConferenceUpdateDto;
import com.example.conference.dao.document.Conference;
import com.example.conference.dao.document.Participant;
import com.example.conference.dao.document.Room;
import com.example.conference.dao.repository.ConferenceRepository;
import com.example.conference.dao.repository.RoomRepository;
import com.example.conference.utility.ConferenceUtility;
import com.example.conference.utility.RoomUtility;
import com.example.conference.utility.enumeration.ConferenceStatus;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class ConferenceControllerIntegrationTests extends ConferenceRoomApiSpringBootContextLoader {
    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        conferenceRepository.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    public void testPostConferenceWithoutRoomShouldReturnCreated() throws Exception {
        final ConferenceDto conferenceDto = ConferenceUtility.buildConferenceDtoWithRequiredProp();

        mockMvc.perform(post(TestRequestConstants.API_CONFERENCE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(json(conferenceDto))
                .characterEncoding(ENCODING))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_START, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_ID, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_DESCRIPTION, is(conferenceDto.getDescription())))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_REQUESTED_SEATS_COUNT, is(conferenceDto.getRequestedSeatsCount())));
    }

    @Test
    public void testPostConferenceWithRoomShouldReturnCreated() throws Exception {
        final Room room = roomRepository.save(RoomUtility.buildRoomWithRequiredProp());
        final ConferenceDto conferenceDto = ConferenceUtility.buildConferenceDtoWithRequiredProp();
        conferenceDto.setRoomId(room.getId());

        mockMvc.perform(post(TestRequestConstants.API_CONFERENCE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(json(conferenceDto))
                .characterEncoding(ENCODING))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_START, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_ID, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_DESCRIPTION, is(conferenceDto.getDescription())))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_REQUESTED_SEATS_COUNT, is(conferenceDto.getRequestedSeatsCount())))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_ROOM_ID, is(room.getId())));
    }

    @Test
    public void testPostConferenceWithRoomShouldReturnNotFound() throws Exception {
        final ConferenceDto conferenceDto = ConferenceUtility.buildConferenceDtoWithRequiredProp();

        conferenceDto.setRoomId(ObjectId.get().toString());
        mockMvc.perform(post(TestRequestConstants.API_CONFERENCE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(json(conferenceDto))
                .characterEncoding(ENCODING))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPatchExistentConferenceShouldReturnOk() throws Exception {
        final Conference conference = ConferenceUtility.buildConferenceWithRequiredProp();
        conferenceRepository.save(conference);

        final ConferenceUpdateDto conferenceUpdateDto =
                ConferenceUtility.buildConferenceUpdateDto(TestMockValueConstants.REQUESTED_SEATS_COUNT_MOCK_VALUE - 1,
                        null, null, null);

        mockMvc.perform(patch(TestRequestConstants.API_CONFERENCE + "/" + conference.getId())
                .contentType(APPLICATION_JSON_VALUE)
                .content(json(conferenceUpdateDto))
                .characterEncoding(ENCODING))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_START, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_ID, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_DESCRIPTION, is(conference.getDescription())))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_REQUESTED_SEATS_COUNT, is(conferenceUpdateDto.getRequestedSeatsCount())));
    }

    @Test
    public void testPatchNonExistentConferenceShouldReturnNonFound() throws Exception {
        final ConferenceUpdateDto conferenceUpdateDto =
                ConferenceUtility.buildConferenceUpdateDto(TestMockValueConstants.REQUESTED_SEATS_COUNT_MOCK_VALUE,
                        null, null, null);

        mockMvc.perform(patch(TestRequestConstants.API_CONFERENCE + "/" + ObjectId.get().toString())
                .contentType(APPLICATION_JSON_VALUE)
                .content(json(conferenceUpdateDto))
                .characterEncoding(ENCODING))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPatchConferenceCancelShouldReturnOk() throws Exception {
        final Conference conference = ConferenceUtility.buildConferenceWithRequiredProp();
        conferenceRepository.save(conference);

        mockMvc.perform(patch(TestRequestConstants.API_CONFERENCE + TestRequestConstants.PATH_CANCEL + conference.getId())
                .contentType(APPLICATION_JSON_VALUE)
                .characterEncoding(ENCODING))
                .andExpect(status().isOk());
    }

    @Test
    public void testPatchConferenceCancelShouldReturnBadRequest() throws Exception {
        final Conference conference = ConferenceUtility.buildConferenceWithRequiredProp();
        conference.setStatus(ConferenceStatus.CANCELED.name());
        conferenceRepository.save(conference);

        mockMvc.perform(patch(TestRequestConstants.API_CONFERENCE + TestRequestConstants.PATH_CANCEL + conference.getId())
                .contentType(APPLICATION_JSON_VALUE)
                .characterEncoding(ENCODING))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostAddParticipantToScheduledConferenceShouldReturnOk() throws Exception {
        final Conference conference = ConferenceUtility.buildConferenceWithRequiredProp();
        conferenceRepository.save(conference);

        final ParticipantDto participantDto = ConferenceUtility.buildParticipantDto();

        mockMvc.perform(post(TestRequestConstants.API_CONFERENCE + "/" + conference.getId() + TestRequestConstants.PATH_PARTICIPANTS)
                .contentType(APPLICATION_JSON_VALUE)
                .content(json(participantDto))
                .characterEncoding(ENCODING))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_START, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_ID, notNullValue()));
    }

    @Test
    public void testPostAddParticipantToCanceledConferenceShouldReturnBadRequest() throws Exception {
        final Conference conference = ConferenceUtility.buildConferenceWithRequiredProp();
        conference.setStatus(ConferenceStatus.CANCELED.name());
        conferenceRepository.save(conference);

        final ParticipantDto participantDto = ConferenceUtility.buildParticipantDto();

        mockMvc.perform(post(TestRequestConstants.API_CONFERENCE + "/" + conference.getId() + TestRequestConstants.PATH_PARTICIPANTS)
                .contentType(APPLICATION_JSON_VALUE)
                .content(json(participantDto))
                .characterEncoding(ENCODING))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostAddParticipantToScheduledConferenceShouldReturnBadRequest() throws Exception {
        final Conference conference = ConferenceUtility.buildConferenceWithRequiredProp();
        conference.setRequestedSeatsCount(0);
        conferenceRepository.save(conference);

        final ParticipantDto participantDto = ConferenceUtility.buildParticipantDto();

        mockMvc.perform(post(TestRequestConstants.API_CONFERENCE + "/" + conference.getId() + TestRequestConstants.PATH_PARTICIPANTS)
                .contentType(APPLICATION_JSON_VALUE)
                .content(json(participantDto))
                .characterEncoding(ENCODING))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteParticipantFromScheduledConferenceShouldReturnOk() throws Exception {
        final Participant participant = ConferenceUtility.buildParticipant();
        final Conference conference = ConferenceUtility.buildConferenceWithRequiredProp();
        conference.getParticipants().add(participant);
        conferenceRepository.save(conference);

        String participantId = conference.getParticipants().iterator().next().getId();

        mockMvc.perform(delete(TestRequestConstants.API_CONFERENCE + "/" + conference.getId()
                + TestRequestConstants.PATH_PARTICIPANTS + "/" + participantId)
                .contentType(APPLICATION_JSON_VALUE)
                .characterEncoding(ENCODING))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteParticipantFromScheduledConferenceShouldNotFound() throws Exception {
        final Conference conference = ConferenceUtility.buildConferenceWithRequiredProp();
        conferenceRepository.save(conference);

        mockMvc.perform(delete(TestRequestConstants.API_CONFERENCE + "/" + conference.getId()
                + TestRequestConstants.PATH_PARTICIPANTS + "/" + UUID.randomUUID().toString())
                .contentType(APPLICATION_JSON_VALUE)
                .characterEncoding(ENCODING))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetConferencesShouldReturnOk() throws Exception {
        final Conference conference = ConferenceUtility.buildConferenceWithRequiredProp();
        conferenceRepository.save(conference);

        mockMvc.perform(get(TestRequestConstants.API_CONFERENCE)
                .contentType(APPLICATION_JSON_VALUE)
                .characterEncoding(ENCODING))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_START, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_START, hasSize(1)))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_1ST_ID, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_1ST_ID, not("")))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_1ST_REQUESTED_SEATS_COUNT, is(conference.getRequestedSeatsCount())))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_1ST_DESCRIPTION, is(conference.getDescription())));
    }

    @Test
    public void testGetConferencesShouldReturnNoContent() throws Exception {
        mockMvc.perform(get(TestRequestConstants.API_CONFERENCE)
                .contentType(APPLICATION_JSON_VALUE)
                .characterEncoding(ENCODING))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetConferenceByIdShouldReturnOk() throws Exception {
        final Conference conference = ConferenceUtility.buildConferenceWithRequiredProp();
        conferenceRepository.save(conference);

        mockMvc.perform(get(TestRequestConstants.API_CONFERENCE + "/" + conference.getId())
                .contentType(APPLICATION_JSON_VALUE)
                .characterEncoding(ENCODING))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_START, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_ID, is(conference.getId())))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_REQUESTED_SEATS_COUNT, is(conference.getRequestedSeatsCount())))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_DESCRIPTION, is(conference.getDescription())));
    }

    @Test
    public void testGetConferenceByIdShouldReturnNoFound() throws Exception {
        mockMvc.perform(get(TestRequestConstants.API_CONFERENCE + "/" + ObjectId.get().toString())
                .contentType(APPLICATION_JSON_VALUE)
                .characterEncoding(ENCODING))
                .andExpect(status().isNotFound());
    }
}
