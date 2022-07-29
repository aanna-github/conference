//package com.example.conference;
//
//import com.example.conference.constants.TestConstants;
//import com.example.conference.dao.document.Conference;
//import com.example.conference.dao.document.Participant;
//import com.example.conference.dao.repository.ConferenceRepository;
//import com.example.conference.service.ConferenceService;
//import com.example.conference.service.RoomService;
//import com.example.conference.service.SequenceGeneratorService;
//import com.example.conference.util.enumeration.ConferenceStatus;
//import com.example.conference.util.mapper.CommonMapper;
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
//        URI = String.format("%s%s%s%s", TestConstants.HTTP, BASE_URI, ROOT, TestConstants.API_CONFERENCE);
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
//                .body(TestConstants.FIRSTNAME_PROPERTY, equalTo(participantParams.get(TestConstants.FIRSTNAME_PROPERTY)))
//                .body(TestConstants.LASTNAME_PROPERTY, equalTo(participantParams.get(TestConstants.LASTNAME_PROPERTY)))
//                .body(TestConstants.INVITED_BY_PROPERTY, equalTo(participantParams.get(TestConstants.INVITED_BY_PROPERTY)))
//                .body(TestConstants.COMPANY_NAME_PROPERTY, equalTo(participantParams.get(TestConstants.COMPANY_NAME_PROPERTY)))
//                .body(TestConstants.SPECIALIZATION_PROPERTY, equalTo(participantParams.get(TestConstants.SPECIALIZATION_PROPERTY)))
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
//        participant.setFirstName(TestConstants.FIRSTNAME_MOCK_VALUE);
//        participant.setLastName(TestConstants.LASTNAME_MOCK_VALUE);
//        participant.setInvitedBy(TestConstants.INVITED_BY_MOCK_VALUE);
//        participant.setSpecialization(TestConstants.SPECIALIZATION_MOCK_VALUE);
//        participant.setCompanyName(TestConstants.COMPANY_NAME_MOCK_VALUE);
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
//        participantParams.put(TestConstants.FIRSTNAME_PROPERTY, TestConstants.FIRSTNAME_MOCK_VALUE);
//        participantParams.put(TestConstants.LASTNAME_PROPERTY, TestConstants.LASTNAME_MOCK_VALUE);
//        participantParams.put(TestConstants.INVITED_BY_PROPERTY, TestConstants.INVITED_BY_MOCK_VALUE);
//        participantParams.put(TestConstants.COMPANY_NAME_PROPERTY, TestConstants.COMPANY_NAME_MOCK_VALUE);
//        participantParams.put(TestConstants.SPECIALIZATION_PROPERTY, TestConstants.SPECIALIZATION_MOCK_VALUE);
//
//        return participantParams;
//    }
//}
