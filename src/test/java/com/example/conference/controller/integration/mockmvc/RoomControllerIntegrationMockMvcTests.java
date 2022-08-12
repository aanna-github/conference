package com.example.conference.controller.integration.mockmvc;

import com.example.conference.ConferenceRoomApiSpringBootContextLoader;
import com.example.conference.constants.TestRequestConstants;
import com.example.conference.constants.TestJsonPathConstants;
import com.example.conference.constants.TestMockValueConstants;
import com.example.conference.constants.TestRoles;
import com.example.conference.controller.payload.request.RoomDto;
import com.example.conference.controller.payload.request.RoomUpdateDto;
import com.example.conference.dao.document.Conference;
import com.example.conference.dao.document.Room;
import com.example.conference.dao.repository.ConferenceRepository;
import com.example.conference.dao.repository.RoomRepository;
import com.example.conference.utility.ConferenceUtility;
import com.example.conference.utility.RoomUtility;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
public class RoomControllerIntegrationMockMvcTests extends ConferenceRoomApiSpringBootContextLoader {
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
    @WithMockUser(roles = {TestRoles.ROLE_MODERATOR, TestRoles.ROLE_ADMIN})
    public void testPostRoomShouldReturnCreated() throws Exception {
        final RoomDto roomDto = RoomUtility.buildRoomDtoWithRequiredProp();

        mockMvc.perform(post(TestRequestConstants.API_ROOM)
                .contentType(APPLICATION_JSON_VALUE)
                .content(json(roomDto))
                .characterEncoding(ENCODING))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_START, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_ID, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_SEATS_COUNT, is(roomDto.getSeatsCount())))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_FLOOR, is(roomDto.getFloor())));
    }

    @Test
    @WithMockUser
    public void testPostRoomShouldReturnUnauthorized() throws Exception {
        final RoomDto roomDto = RoomUtility.buildRoomDtoWithRequiredProp();

        mockMvc.perform(post(TestRequestConstants.API_ROOM)
                .contentType(APPLICATION_JSON_VALUE)
                .content(json(roomDto))
                .characterEncoding(ENCODING))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {TestRoles.ROLE_MODERATOR, TestRoles.ROLE_ADMIN})
    public void testPatchExistentRoomShouldReturnOk() throws Exception {
        final Room room = RoomUtility.buildRoomWithRequiredProp();
        roomRepository.save(room);
        final Integer updatedSeatsCount = room.getSeatsCount() + 1;
        final RoomUpdateDto roomUpdateDto = RoomUtility.buildRoomUpdateDto(null, updatedSeatsCount);

        mockMvc.perform(patch(TestRequestConstants.API_ROOM + "/" + room.getId())
                .contentType(APPLICATION_JSON_VALUE)
                .content(json(roomUpdateDto))
                .characterEncoding(ENCODING))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_START, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_ID, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_SEATS_COUNT, is(updatedSeatsCount)))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_FLOOR, is(room.getFloor())));
    }

    @Test
    @WithMockUser
    public void testPatchExistentRoomShouldReturnUnauthorized() throws Exception {
        final Room room = RoomUtility.buildRoomWithRequiredProp();
        roomRepository.save(room);
        final Integer updatedSeatsCount = room.getSeatsCount() + 1;
        final RoomUpdateDto roomUpdateDto = RoomUtility.buildRoomUpdateDto(null, updatedSeatsCount);

        mockMvc.perform(patch(TestRequestConstants.API_ROOM + "/" + room.getId())
                .contentType(APPLICATION_JSON_VALUE)
                .content(json(roomUpdateDto))
                .characterEncoding(ENCODING))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {TestRoles.ROLE_MODERATOR, TestRoles.ROLE_ADMIN})
    public void testPatchNonExistentRoomShouldReturnNotFound() throws Exception {
        final Integer updatedSeatsCount = TestMockValueConstants.SEATS_COUNT_MOCK_VALUE;
        final RoomUpdateDto roomUpdateDto = RoomUtility.buildRoomUpdateDto(null, updatedSeatsCount);

        mockMvc.perform(patch(TestRequestConstants.API_ROOM + "/" + ObjectId.get().toString())
                .contentType(APPLICATION_JSON_VALUE)
                .content(json(roomUpdateDto))
                .characterEncoding(ENCODING))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {TestRoles.ROLE_MODERATOR, TestRoles.ROLE_ADMIN})
    public void testPatchBookExistentRoomForExistentConferenceShouldReturnOk() throws Exception {
        final Room room = RoomUtility.buildRoomWithRequiredProp();
        roomRepository.save(room);

        final Conference conference = ConferenceUtility.buildConferenceWithRequiredProp();
        conferenceRepository.save(conference);

        mockMvc.perform(patch(TestRequestConstants.API_ROOM + TestRequestConstants.BOOK_PATH + room.getId())
                .contentType(APPLICATION_JSON_VALUE)
                .characterEncoding(ENCODING)
                .header(TestRequestConstants.CONFERENCE_ID_HEADER, conference.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_START, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_ID, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_SEATS_COUNT, is(room.getSeatsCount())))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_ROOM_AVAILABILITY, notNullValue()))
                .andExpect(jsonPath(TestJsonPathConstants.JSON_PATH_FLOOR, is(room.getFloor())));
    }

    @Test
    @WithMockUser
    public void testPatchBookExistentRoomForExistentConferenceShouldReturnUnauthorized() throws Exception {
        final Room room = RoomUtility.buildRoomWithRequiredProp();
        roomRepository.save(room);

        final Conference conference = ConferenceUtility.buildConferenceWithRequiredProp();
        conferenceRepository.save(conference);

        mockMvc.perform(patch(TestRequestConstants.API_ROOM + TestRequestConstants.BOOK_PATH + room.getId())
                .contentType(APPLICATION_JSON_VALUE)
                .characterEncoding(ENCODING)
                .header(TestRequestConstants.CONFERENCE_ID_HEADER, conference.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {TestRoles.ROLE_MODERATOR, TestRoles.ROLE_ADMIN})
    public void testPatchBookNonExistentRoomForExistentConferenceShouldReturnNotFound() throws Exception {
        final Conference conference = ConferenceUtility.buildConferenceWithRequiredProp();
        conferenceRepository.save(conference);

        mockMvc.perform(patch(TestRequestConstants.API_ROOM + TestRequestConstants.BOOK_PATH + ObjectId.get().toString())
                .contentType(APPLICATION_JSON_VALUE)
                .characterEncoding(ENCODING)
                .header(TestRequestConstants.CONFERENCE_ID_HEADER, conference.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {TestRoles.ROLE_MODERATOR, TestRoles.ROLE_ADMIN})
    public void testPatchBookExistentRoomShouldForNonExistentConferenceReturnNotFound() throws Exception {
        final Room room = RoomUtility.buildRoomWithRequiredProp();
        roomRepository.save(room);
        mockMvc.perform(patch(TestRequestConstants.API_ROOM + TestRequestConstants.BOOK_PATH + room.getId())
                .contentType(APPLICATION_JSON_VALUE)
                .characterEncoding(ENCODING)
                .header(TestRequestConstants.CONFERENCE_ID_HEADER, ObjectId.get().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {TestRoles.ROLE_MODERATOR, TestRoles.ROLE_ADMIN})
    public void testPatchBookExistentRoomForExistentConferenceShouldReturnBadRequest() throws Exception {
        final Room room = RoomUtility.buildRoomWithRequiredProp();
        roomRepository.save(room);

        final Conference conference = ConferenceUtility.buildConferenceWithRequiredProp();
        conference.setRequestedSeatsCount(Integer.MAX_VALUE);
        conferenceRepository.save(conference);

        mockMvc.perform(patch(TestRequestConstants.API_ROOM + TestRequestConstants.BOOK_PATH + room.getId())
                .contentType(APPLICATION_JSON_VALUE)
                .characterEncoding(ENCODING)
                .header(TestRequestConstants.CONFERENCE_ID_HEADER, conference.getId()))
                .andExpect(status().isNotFound());
    }
}
