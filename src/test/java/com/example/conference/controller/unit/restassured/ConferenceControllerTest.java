//package com.example.conference.controller.unit.restassured;
//
//import com.example.conference.constants.TestJsonObjectPropertyContents;
//import com.example.conference.constants.TestMockValueConstants;
//import com.example.conference.constants.TestRequestConstants;
//import com.example.conference.dao.document.Conference;
//import com.example.conference.dao.document.Participant;
//import com.example.conference.dao.repository.ConferenceRepository;
//import com.example.conference.utility.enumeration.ConferenceStatus;
//import io.restassured.http.ContentType;
//import org.apache.http.HttpStatus;
//import org.bson.BsonTimestamp;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
//import java.time.LocalDateTime;
//import java.util.LinkedHashSet;
//import java.util.UUID;
//
//import static io.restassured.RestAssured.get;
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.equalTo;
//
//@SpringBootTest
//class ConferenceControllerTest {
//
//    @Value("${api.local.url}")
//    private String BASE_URI;
//
//    @Value("${server.servlet.context-path}")
//    private String ROOT;
//
//    private static String URI;
//
//    @Autowired
//    private ConferenceRepository conferenceRepository;
////
////    @Autowired
////    private ConferenceService conferenceService;
////
////    @Autowired
////    private RoomService roomService;
////
////    @Autowired
////    private CommonMapper commonMapper;
//
////    @Autowired
////    private MongoTemplate mongoTemplate;
//
//    @BeforeEach
//    public void setup() {
//        URI = String.format("%s%s%s%s", TestRequestConstants.HTTP, BASE_URI, ROOT, TestRequestConstants.API_CONFERENCE);
//    }
//
//    @Test
//    void testConferenceCheckShouldReturnOk() {
//        get(URI + "/check").then().assertThat().statusCode(HttpStatus.SC_OK);
//    }
//
//    @Test
//    void testPostConferenceShouldReturnCreated() throws JSONException {
//        Integer requestedSeatsCount = 10;
//
//        JSONObject conferenceParams = new JSONObject();
//        conferenceParams.put("description", "test");
//        conferenceParams.put("requestedSeatsCount", requestedSeatsCount);
//        conferenceParams.put("eventDate", LocalDateTime.now());
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(conferenceParams.toString())
//                .log().all()
//                .when()
//                .post(URI)
//                .then()
//                .assertThat().statusCode(HttpStatus.SC_CREATED)
//                .body("description", equalTo("test"))
//                .body("requestedSeatsCount", equalTo(requestedSeatsCount))
//                .body("status", equalTo(ConferenceStatus.SCHEDULED.name()))
//                .log().all();
//    }
//
//    @Test
//    void testPatchConferenceShouldReturnOk() throws JSONException {
//        Integer requestedSeatsCount = 100;
//        Conference conference = Conference.builder()
//                .eventDate(new BsonTimestamp(10, 100))
//                .status(ConferenceStatus.SCHEDULED.name())
//                .requestedSeatsCount(++requestedSeatsCount)
//                .build();
//
//        conferenceRepository.save(conference);
//
//        JSONObject conferenceParams = new JSONObject();
//        conferenceParams.put("requestedSeatsCount", requestedSeatsCount);
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(conferenceParams.toString())
//                .log().all()
//                .when()
//                .patch(URI + "/" + conference.getId())
//                .then()
//                .assertThat().statusCode(HttpStatus.SC_OK)
//                .body("requestedSeatsCount", equalTo(requestedSeatsCount))
//                .body("status", equalTo(ConferenceStatus.SCHEDULED.name()))
//                .log().all();
//    }
//
//    @Test
//    void testPatchConferenceCancelShouldReturnOk() throws JSONException {
//        Integer requestedSeatsCount = 100;
//        Conference conference = Conference.builder()
//                .eventDate(new BsonTimestamp(10, 100))
//                .status(ConferenceStatus.SCHEDULED.name())
//                .requestedSeatsCount(++requestedSeatsCount)
//                .build();
//
//        conferenceRepository.save(conference);
//
//        JSONObject conferenceParams = new JSONObject();
//        conferenceParams.put("requestedSeatsCount", requestedSeatsCount);
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(conferenceParams.toString())
//                .log().all()
//                .when()
//                .patch(URI + "/cancel/" + conference.getId())
//                .then()
//                .assertThat().statusCode(HttpStatus.SC_OK)
//                .body("requestedSeatsCount", equalTo(requestedSeatsCount))
//                .body("status", equalTo(ConferenceStatus.CANCELED.name()))
//                .log().all();
//    }
//
//    @Test
//    void testPostAddParticipantToScheduledConferenceShouldReturnOk() throws JSONException {
//        int requestedSeatsCount = 100;
//        Conference conference = Conference.builder()
//                .eventDate(new BsonTimestamp(10, 100))
//                .status(ConferenceStatus.SCHEDULED.name())
//                .requestedSeatsCount(++requestedSeatsCount)
//                .build();
//
//        conferenceRepository.save(conference);
//
//        JSONObject participantParams = generateDefaultParticipant();
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(participantParams.toString())
//                .log().all()
//                .when()
//                .post(URI + "/" + conference.getId() + "/participants")
//                .then()
//                .assertThat().statusCode(HttpStatus.SC_CREATED)
//                .body(TestJsonObjectPropertyContents.FIRSTNAME_PROPERTY, equalTo(participantParams.get(TestJsonObjectPropertyContents.FIRSTNAME_PROPERTY)))
//                .body(TestJsonObjectPropertyContents.LASTNAME_PROPERTY, equalTo(participantParams.get(TestJsonObjectPropertyContents.LASTNAME_PROPERTY)))
//                .body(TestJsonObjectPropertyContents.INVITED_BY_PROPERTY, equalTo(participantParams.get(TestJsonObjectPropertyContents.INVITED_BY_PROPERTY)))
//                .body(TestJsonObjectPropertyContents.COMPANY_NAME_PROPERTY, equalTo(participantParams.get(TestJsonObjectPropertyContents.COMPANY_NAME_PROPERTY)))
//                .body(TestJsonObjectPropertyContents.SPECIALIZATION_PROPERTY, equalTo(participantParams.get(TestJsonObjectPropertyContents.SPECIALIZATION_PROPERTY)))
//                .log().all();
//    }
//
//    @Test
//    void testPostAddParticipantToCanceledConferenceShouldReturnBadRequest() throws JSONException {
//        int requestedSeatsCount = 100;
//        Conference conference = Conference.builder()
//                .eventDate(new BsonTimestamp(10, 100))
//                .status(ConferenceStatus.CANCELED.name())
//                .requestedSeatsCount(++requestedSeatsCount)
//                .build();
//
//        conferenceRepository.save(conference);
//
//        JSONObject participantParams = generateDefaultParticipant();
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(participantParams.toString())
//                .log().all()
//                .when()
//                .post(URI + "/" + conference.getId() + "/participants")
//                .then()
//                .assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
//                .log().all();
//    }
//
//    @Test
//    void testDeleteAddedParticipantFromConferenceShouldReturnOk() {
//        int requestedSeatsCount = 100;
//        Conference conference = Conference.builder()
//                .eventDate(new BsonTimestamp(10, 100))
//                .status(ConferenceStatus.CANCELED.name())
//                .requestedSeatsCount(++requestedSeatsCount)
//                .build();
//
//        Participant participant = new Participant();
//        participant.setId(UUID.randomUUID().toString());
//        participant.setFirstName(TestMockValueConstants.FIRSTNAME_MOCK_VALUE);
//        participant.setLastName(TestMockValueConstants.LASTNAME_MOCK_VALUE);
//        participant.setInvitedBy(TestMockValueConstants.INVITED_BY_MOCK_VALUE);
//        participant.setSpecialization(TestMockValueConstants.SPECIALIZATION_MOCK_VALUE);
//        participant.setCompanyName(TestMockValueConstants.COMPANY_NAME_MOCK_VALUE);
//        conference.setParticipants(new LinkedHashSet<>());
//        conference.getParticipants().add(participant);
//
//        conferenceRepository.save(conference);
//
//        given()
//                .contentType(ContentType.JSON)
//                .log().all()
//                .when()
//                .delete(URI + "/" + conference.getId() + "/participants/" + participant.getId())
//                .then()
//                .assertThat().statusCode(HttpStatus.SC_OK)
//                .log().all();
//    }
//
//    private JSONObject generateDefaultParticipant() throws JSONException {
//        final JSONObject participantParams = new JSONObject();
//        participantParams.put(TestJsonObjectPropertyContents.FIRSTNAME_PROPERTY, TestMockValueConstants.FIRSTNAME_MOCK_VALUE);
//        participantParams.put(TestJsonObjectPropertyContents.LASTNAME_PROPERTY, TestMockValueConstants.LASTNAME_MOCK_VALUE);
//        participantParams.put(TestJsonObjectPropertyContents.INVITED_BY_PROPERTY, TestMockValueConstants.INVITED_BY_MOCK_VALUE);
//        participantParams.put(TestJsonObjectPropertyContents.COMPANY_NAME_PROPERTY, TestMockValueConstants.COMPANY_NAME_MOCK_VALUE);
//        participantParams.put(TestJsonObjectPropertyContents.SPECIALIZATION_PROPERTY, TestMockValueConstants.SPECIALIZATION_MOCK_VALUE);
//
//        return participantParams;
//    }
//}
