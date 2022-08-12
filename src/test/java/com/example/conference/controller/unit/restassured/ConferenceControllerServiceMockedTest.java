package com.example.conference.controller.unit.restassured;

import com.example.conference.constants.TestJsonObjectPropertyContents;
import com.example.conference.constants.TestMockValueConstants;
import com.example.conference.constants.TestRequestConstants;
import com.example.conference.controller.ConferenceController;
import com.example.conference.controller.payload.request.ConferenceDto;
import com.example.conference.controller.payload.request.ParticipantDto;
import com.example.conference.controller.payload.response.ConferenceResponseDto;
import com.example.conference.controller.payload.response.ParticipantResponseDto;
import com.example.conference.controller.payload.request.ConferenceUpdateDto;
import com.example.conference.exception.ConferenceRoomExceptionHandler;
import com.example.conference.exception.DocumentNotFoundException;
import com.example.conference.exception.InvalidInputException;
import com.example.conference.service.ConferenceService;
import com.example.conference.utility.ConferenceUtility;
import com.example.conference.utility.enumeration.ConferenceStatus;
import com.example.conference.utility.enumeration.ErrorType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.apache.http.HttpStatus;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureMockMvc(addFilters = false)
public class ConferenceControllerServiceMockedTest {

    final private ObjectMapper mapper = new ObjectMapper();

    @Mock
    private ConferenceService conferenceService;

    @InjectMocks
    private ConferenceController conferenceController;

    @InjectMocks
    private ConferenceRoomExceptionHandler conferenceRoomExceptionHandler;

    @Before
    public void initialiseRestAssuredMockMvcStandalone() {
        RestAssuredMockMvc.standaloneSetup(conferenceController, conferenceRoomExceptionHandler,
                springSecurity((request, response, chain) -> chain.doFilter(request, response)));
    }

    @Test
    public void givenValidConferencesInputWhenPostConferencesThenRespondWithStatusCreated() throws JSONException {
        final JSONObject conferenceJsonWithRequiredProp = ConferenceUtility.buildConferenceJsonWithRequiredProp();

        when(conferenceService.saveConference(Mockito.any(ConferenceDto.class))).thenReturn(ConferenceUtility.jsonToConferenceResponseDto(conferenceJsonWithRequiredProp));

        RestAssuredMockMvc.given().contentType(ContentType.JSON)
                .body(conferenceJsonWithRequiredProp.toString())
                .when()
                .post(TestRequestConstants.API_CONFERENCE)
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .header(TestRequestConstants.LOCATION_HEADER, Matchers.containsString(TestRequestConstants.API_CONFERENCE))
                .contentType(ContentType.JSON)
                .body(notNullValue())
                .body(TestJsonObjectPropertyContents.ID_PROPERTY, notNullValue())
                .body(TestJsonObjectPropertyContents.EVENT_DATE_PROPERTY, notNullValue())
                .body(TestJsonObjectPropertyContents.DESCRIPTION_PROPERTY,
                        equalTo(conferenceJsonWithRequiredProp.get(TestJsonObjectPropertyContents.DESCRIPTION_PROPERTY)))
                .body(TestJsonObjectPropertyContents.REQUESTED_SEATS_COUNT_PROPERTY,
                        equalTo(conferenceJsonWithRequiredProp.get(TestJsonObjectPropertyContents.REQUESTED_SEATS_COUNT_PROPERTY)))
                .log().all();

    }

    @Test
    public void givenNonExistentConferencesWhenGetConferencesThenRespondWithStatusNoContentAndEmptyArray() {
        when(conferenceService.getAllConferences()).thenReturn(Collections.emptyList());

        RestAssuredMockMvc.given()
                .when()
                .get(TestRequestConstants.API_CONFERENCE)
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .contentType(ContentType.JSON)
                .body(is(equalTo("[]")))
                .log().all();
    }

    @Test
    public void givenNonExistentConferenceWhenGetConferenceByIdThenRespondWithStatusNotFound() {
        String id = ObjectId.get().toString();
        String message = String.format("No Conference found for id: %s", id);
        when(conferenceService.getConferenceById(id)).thenThrow(new DocumentNotFoundException(message));

        RestAssuredMockMvc.given()
                .when()
                .get(TestRequestConstants.API_CONFERENCE + "/" + id)
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .contentType(ContentType.JSON)
                .body(notNullValue())
                .body(TestJsonObjectPropertyContents.CODE, is(String.valueOf(HttpStatus.SC_NOT_FOUND)))
                .body(TestJsonObjectPropertyContents.TITLE, notNullValue())
                .body(TestJsonObjectPropertyContents.MESSAGE, equalTo(message))
                .body(TestJsonObjectPropertyContents.TIMESTAMP, notNullValue())
                .body(TestJsonObjectPropertyContents.TYPE, is(ErrorType.INVALID_REQUEST_ERROR.toString()))
                .log().all();
    }

    @Test
    public void givenValidConferenceUpdateInputWhenPatchConferenceThenResponseReturnOk() throws JSONException {
        String id = ObjectId.get().toString();
        JSONObject conferenceParams = new JSONObject();
        conferenceParams.put(TestJsonObjectPropertyContents.REQUESTED_SEATS_COUNT_PROPERTY, TestMockValueConstants.REQUESTED_SEATS_COUNT_MOCK_VALUE);
        when(conferenceService.updateConference(Mockito.any(ConferenceUpdateDto.class), eq(id))).thenReturn(ConferenceUtility.jsonToConferenceResponseDto(conferenceParams));


        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(conferenceParams.toString())
                .log().all()
                .when()
                .patch(TestRequestConstants.API_CONFERENCE + "/" + id)
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .body(notNullValue())
                .body(TestJsonObjectPropertyContents.REQUESTED_SEATS_COUNT_PROPERTY, equalTo(TestMockValueConstants.REQUESTED_SEATS_COUNT_MOCK_VALUE))
                .log().all();
    }

    @Test
    public void givenExistentConferencesWhenGetConferencesThenRespondWithStatusOkAndNonEmptyArray() {
        final List<ConferenceResponseDto> conferenceResponseDtoList = new ArrayList<>();
        conferenceResponseDtoList.add(ConferenceUtility.buildConferenceResponseDtoWithRequiredProp());

        when(conferenceService.getAllConferences()).thenReturn(conferenceResponseDtoList);

        RestAssuredMockMvc.given()
                .when()
                .get(TestRequestConstants.API_CONFERENCE)
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .body(notNullValue())
                .body(TestJsonObjectPropertyContents.ID_PROPERTY, notNullValue())
                .body(TestJsonObjectPropertyContents.EVENT_DATE_PROPERTY, notNullValue())
                .body(TestJsonObjectPropertyContents.DESCRIPTION_PROPERTY, notNullValue())
                .body(TestJsonObjectPropertyContents.REQUESTED_SEATS_COUNT_PROPERTY, notNullValue())
                .log().all();
    }

    @Test
    public void givenExistentConferenceWhenCancelConferenceThenRespondWithStatusOk() throws JSONException {
        String id = ObjectId.get().toString();
        JSONObject conferenceJsonWithRequiredProp = ConferenceUtility.buildConferenceJsonWithRequiredProp();
        when(conferenceService.cancelConference(id)).thenReturn(ConferenceUtility.jsonToConferenceResponseDto(conferenceJsonWithRequiredProp));

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .patch(TestRequestConstants.API_CONFERENCE + TestRequestConstants.CANCEL_PATH + id)
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .log().all();
    }

    @Test
    public void givenExistentConferenceWhenAddParticipantToScheduledConferenceShouldReturnOk() throws JsonProcessingException {
        String id = ObjectId.get().toString();
        final ParticipantDto participantDto = ConferenceUtility.buildParticipantDto();
        final ParticipantResponseDto participantResponseDto = ConferenceUtility.buildParticipantResponseDto(id);

        when(conferenceService.addParticipant(participantDto, id)).thenReturn(participantResponseDto);
        mapper.writeValueAsString(participantDto);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(participantDto))
                .log().all()
                .when()
                .post(TestRequestConstants.API_CONFERENCE + "/" + id + TestRequestConstants.PATH_PARTICIPANTS)
                .then()
                .assertThat().statusCode(HttpStatus.SC_CREATED)
                .header(TestRequestConstants.LOCATION_HEADER, Matchers.containsString(TestRequestConstants.API_CONFERENCE))
                .body(notNullValue())
                .body(TestJsonObjectPropertyContents.ID_PROPERTY, notNullValue())
                .body(TestJsonObjectPropertyContents.ID_PROPERTY, equalTo(participantResponseDto.getId()))
                .body(TestJsonObjectPropertyContents.FIRSTNAME_PROPERTY, equalTo(participantResponseDto.getFirstName()))
                .body(TestJsonObjectPropertyContents.LASTNAME_PROPERTY, equalTo(participantResponseDto.getLastName()))
                .body(TestJsonObjectPropertyContents.INVITED_BY_PROPERTY, equalTo(participantResponseDto.getInvitedBy()))
                .body(TestJsonObjectPropertyContents.COMPANY_NAME_PROPERTY, equalTo(participantResponseDto.getCompanyName()))
                .body(TestJsonObjectPropertyContents.SPECIALIZATION_PROPERTY, equalTo(participantResponseDto.getSpecialization()))
                .log().all();
    }

    @Test
    public void givenExistentConferenceWhenAddParticipantToCanceledConferenceShouldReturnBadRequest() throws JsonProcessingException {
        String id = ObjectId.get().toString();
        final ParticipantDto participantDto = ConferenceUtility.buildParticipantDto();
        String message = "The conference has been canceled. Unable to add a participant: status: " + ConferenceStatus.CANCELED;

        when(conferenceService.addParticipant(participantDto, id)).thenThrow(new InvalidInputException(message));
        mapper.writeValueAsString(participantDto);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(participantDto))
                .log().all()
                .when()
                .post(TestRequestConstants.API_CONFERENCE + "/" + id + TestRequestConstants.PATH_PARTICIPANTS)
                .then()
                .assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(notNullValue())
                .body(TestJsonObjectPropertyContents.CODE, is(String.valueOf(HttpStatus.SC_BAD_REQUEST)))
                .body(TestJsonObjectPropertyContents.TITLE, notNullValue())
                .body(TestJsonObjectPropertyContents.MESSAGE, equalTo(message))
                .body(TestJsonObjectPropertyContents.TIMESTAMP, notNullValue())
                .body(TestJsonObjectPropertyContents.TYPE, is(ErrorType.INVALID_REQUEST_ERROR.toString()))
                .log().all();
    }

    @Test
    public void givenExistentConferenceWithNoFreeSeatWhenAddParticipantToScheduledConferenceShouldReturnBadRequest() throws JsonProcessingException {
        String id = ObjectId.get().toString();
        final ParticipantDto participantDto = ConferenceUtility.buildParticipantDto();
        String message = "There is no free space to add new participant. seats: " + TestMockValueConstants.REQUESTED_SEATS_COUNT_MOCK_VALUE;

        when(conferenceService.addParticipant(participantDto, id)).thenThrow(new InvalidInputException(message));
        mapper.writeValueAsString(participantDto);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(participantDto))
                .log().all()
                .when()
                .post(TestRequestConstants.API_CONFERENCE + "/" + id + TestRequestConstants.PATH_PARTICIPANTS)
                .then()
                .assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(notNullValue())
                .body(TestJsonObjectPropertyContents.CODE, is(String.valueOf(HttpStatus.SC_BAD_REQUEST)))
                .body(TestJsonObjectPropertyContents.TITLE, notNullValue())
                .body(TestJsonObjectPropertyContents.MESSAGE, equalTo(message))
                .body(TestJsonObjectPropertyContents.TIMESTAMP, notNullValue())
                .body(TestJsonObjectPropertyContents.TYPE, is(ErrorType.INVALID_REQUEST_ERROR.toString()))
                .log().all();
    }

    @Test
    public void givenExistentConferenceWhenDeleteParticipantToScheduledConferenceShouldReturnOk() throws JsonProcessingException {
        String id = ObjectId.get().toString();
        String participantId = ObjectId.get().toString();
        final ParticipantDto participantDto = ConferenceUtility.buildParticipantDto();

        ConferenceResponseDto conferenceResponseDto = ConferenceUtility.buildConferenceResponseDtoWithRequiredProp();

        when(conferenceService.removeParticipant(participantId, id)).thenReturn(conferenceResponseDto);
        mapper.writeValueAsString(participantDto);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(participantDto))
                .log().all()
                .when()
                .delete(TestRequestConstants.API_CONFERENCE + "/" + id + TestRequestConstants.PATH_PARTICIPANTS + "/" + participantId)
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .body(notNullValue())
                .log().all();
    }

    @Test
    public void givenExistentConferenceWhenDeleteNonParticipantToScheduledConferenceShouldReturnNotFound() throws JsonProcessingException {
        String id = ObjectId.get().toString();
        String participantId = ObjectId.get().toString();
        final ParticipantDto participantDto = ConferenceUtility.buildParticipantDto();
        String message = "There is no registered participants for the conference. conferenceId: " + id;

        when(conferenceService.removeParticipant(participantId, id)).thenThrow(new DocumentNotFoundException(message));
        mapper.writeValueAsString(participantDto);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(participantDto))
                .log().all()
                .when()
                .delete(TestRequestConstants.API_CONFERENCE + "/" + id + TestRequestConstants.PATH_PARTICIPANTS + "/" + participantId)
                .then()
                .assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(notNullValue())
                .body(TestJsonObjectPropertyContents.CODE, is(String.valueOf(HttpStatus.SC_NOT_FOUND)))
                .body(TestJsonObjectPropertyContents.TITLE, notNullValue())
                .body(TestJsonObjectPropertyContents.MESSAGE, equalTo(message))
                .body(TestJsonObjectPropertyContents.TIMESTAMP, notNullValue())
                .body(TestJsonObjectPropertyContents.TYPE, is(ErrorType.INVALID_REQUEST_ERROR.toString()))
                .log().all();
    }
}
