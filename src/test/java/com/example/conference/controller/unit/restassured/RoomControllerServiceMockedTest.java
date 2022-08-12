package com.example.conference.controller.unit.restassured;

import com.example.conference.constants.TestJsonObjectPropertyContents;
import com.example.conference.constants.TestMockValueConstants;
import com.example.conference.constants.TestRequestConstants;
import com.example.conference.controller.RoomController;
import com.example.conference.controller.payload.request.RoomDto;
import com.example.conference.controller.payload.response.RoomResponseDto;
import com.example.conference.controller.payload.request.RoomUpdateDto;
import com.example.conference.exception.ConferenceRoomExceptionHandler;
import com.example.conference.exception.DocumentNotFoundException;
import com.example.conference.exception.RoomBusyException;
import com.example.conference.service.RoomService;
import com.example.conference.utility.RoomUtility;
import com.example.conference.utility.enumeration.ErrorType;
import com.example.conference.utility.enumeration.RoomStatus;
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

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(MockitoJUnitRunner.class)
public class RoomControllerServiceMockedTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    @InjectMocks
    private ConferenceRoomExceptionHandler conferenceRoomExceptionHandler;

    @Before
    public void initialiseRestAssuredMockMvcStandalone() {
        RestAssuredMockMvc.standaloneSetup(roomController, conferenceRoomExceptionHandler,
                springSecurity((request, response, chain) -> chain.doFilter(request, response)));
    }

    @Test
    public void givenValidRoomInputWhenPostRoomThenRespondWithStatusCreated() throws JSONException {
        final JSONObject roomJsonWithRequiredProp = RoomUtility.buildRoomJsonWithRequiredProp();
        RoomResponseDto roomResponseDto = RoomUtility.jsonToRoomResponseDto(roomJsonWithRequiredProp);

        when(roomService.saveRoom(Mockito.any(RoomDto.class))).thenReturn(roomResponseDto);

        RestAssuredMockMvc.given().contentType(ContentType.JSON)
                .body(roomJsonWithRequiredProp.toString())
                .when()
                .post(TestRequestConstants.API_ROOM)
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .header(TestRequestConstants.LOCATION_HEADER, Matchers.containsString(TestRequestConstants.API_ROOM))
                .contentType(ContentType.JSON)
                .body(notNullValue())
                .body(TestJsonObjectPropertyContents.SEATS_COUNT, notNullValue())
                .body(TestJsonObjectPropertyContents.NUMBER, notNullValue())
                .body(TestJsonObjectPropertyContents.FLOOR, notNullValue())
                .body(TestJsonObjectPropertyContents.ADDRESS, notNullValue())
                .body(TestJsonObjectPropertyContents.SEATS_COUNT, equalTo(roomResponseDto.getSeatsCount()))
                .body(TestJsonObjectPropertyContents.NUMBER, equalTo(roomResponseDto.getNumber()))
                .body(TestJsonObjectPropertyContents.FLOOR, equalTo(roomResponseDto.getFloor()))
                .log().all();
    }

    @Test
    public void givenExistentRoomWhenPatchRoomThenRespondWithStatusOk() throws JSONException {
        String id = ObjectId.get().toString();
        JSONObject roomParams = new JSONObject();
        int updatedSeatsCount = TestMockValueConstants.SEATS_COUNT_MOCK_VALUE + 1;
        roomParams.put(TestJsonObjectPropertyContents.SEATS_COUNT, updatedSeatsCount);

        when(roomService.updateRoom(Mockito.any(RoomUpdateDto.class), eq(id))).thenReturn(RoomUtility.jsonToRoomResponseDto(roomParams));

        RestAssuredMockMvc.given().contentType(ContentType.JSON)
                .body(roomParams.toString())
                .when()
                .patch(TestRequestConstants.API_ROOM + "/" + id)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .body(notNullValue())
                .body(TestJsonObjectPropertyContents.SEATS_COUNT, notNullValue())
                .body(TestJsonObjectPropertyContents.SEATS_COUNT, equalTo(updatedSeatsCount))
                .log().all();
    }

    @Test
    public void givenNonExistentRoomWhenPatchRoomThenRespondWithStatusNotFound() throws JSONException {
        String id = ObjectId.get().toString();
        JSONObject roomParams = new JSONObject();
        int updatedSeatsCount = TestMockValueConstants.SEATS_COUNT_MOCK_VALUE + 1;
        roomParams.put(TestJsonObjectPropertyContents.SEATS_COUNT, updatedSeatsCount);
        String message = "No Conference found for id: " + id;

        when(roomService.updateRoom(Mockito.any(RoomUpdateDto.class), eq(id))).thenThrow(new DocumentNotFoundException(message));

        RestAssuredMockMvc.given().contentType(ContentType.JSON)
                .body(roomParams.toString())
                .when()
                .patch(TestRequestConstants.API_ROOM + "/" + id)
                .then()
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
    public void givenExistentRoomWhenBookThenRespondWithStatusOk() throws JSONException {
        String id = ObjectId.get().toString();
        String conferenceId = ObjectId.get().toString();
        JSONObject roomParams = new JSONObject();
        int updatedSeatsCount = TestMockValueConstants.SEATS_COUNT_MOCK_VALUE + 1;
        roomParams.put(TestJsonObjectPropertyContents.ID_PROPERTY, id);
        roomParams.put(TestJsonObjectPropertyContents.SEATS_COUNT, updatedSeatsCount);

        when(roomService.bookRoomByConferenceId(id, conferenceId)).thenReturn(RoomUtility.jsonToRoomResponseDto(roomParams));

        RestAssuredMockMvc.given().contentType(ContentType.JSON)
                .header(TestRequestConstants.CONFERENCE_ID_HEADER, conferenceId)
                .when()
                .patch(TestRequestConstants.API_ROOM + TestRequestConstants.BOOK_PATH + id)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .body(notNullValue())
                .body(TestJsonObjectPropertyContents.SEATS_COUNT, notNullValue())
                .body(TestJsonObjectPropertyContents.SEATS_COUNT, equalTo(updatedSeatsCount))
                .log().all();
    }

    @Test
    public void givenNonExistentRoomWhenBookThenRespondWithStatusNotFound() {
        String id = ObjectId.get().toString();
        String conferenceId = ObjectId.get().toString();

        String message = String.format("Could not find the room with requested properties. Id:%s, seats count:%d, status:%s",
                id, TestMockValueConstants.SEATS_COUNT_MOCK_VALUE + 1, RoomStatus.FREE);

        when(roomService.bookRoomByConferenceId(id, conferenceId)).thenThrow(new DocumentNotFoundException(message));

        RestAssuredMockMvc.given().contentType(ContentType.JSON)
                .header(TestRequestConstants.CONFERENCE_ID_HEADER, conferenceId)
                .when()
                .patch(TestRequestConstants.API_ROOM + TestRequestConstants.BOOK_PATH + id)
                .then()
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
    public void givenExistentRoomWithNotEnoughSeatsWhenBookThenRespondWithBadRequest() {
        String id = ObjectId.get().toString();
        String conferenceId = ObjectId.get().toString();
        final LocalDateTime eventDate = LocalDateTime.now();
        final String message = "The room doesn't available for the date";

        when(roomService.bookRoomByConferenceId(id, conferenceId)).thenThrow(new RoomBusyException(message, eventDate));

        RestAssuredMockMvc.given().contentType(ContentType.JSON)
                .header(TestRequestConstants.CONFERENCE_ID_HEADER, conferenceId)
                .when()
                .patch(TestRequestConstants.API_ROOM + TestRequestConstants.BOOK_PATH + id)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body(notNullValue())
                .body(TestJsonObjectPropertyContents.CODE, is(String.valueOf(HttpStatus.SC_BAD_REQUEST)))
                .body(TestJsonObjectPropertyContents.TITLE, notNullValue())
                .body(TestJsonObjectPropertyContents.MESSAGE, equalTo(message + ": " + eventDate))
                .body(TestJsonObjectPropertyContents.TIMESTAMP, notNullValue())
                .body(TestJsonObjectPropertyContents.TYPE, is(ErrorType.INVALID_REQUEST_ERROR.toString()))
                .log().all();
    }
}
