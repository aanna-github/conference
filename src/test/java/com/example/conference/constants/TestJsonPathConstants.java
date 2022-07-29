package com.example.conference.constants;

public interface TestJsonPathConstants {
    String JSON_PATH_START = "$";

    String JSON_PATH_ID = "$.id";

    String JSON_PATH_DESCRIPTION ="$.description";

    String JSON_PATH_REQUESTED_SEATS_COUNT = "$.requestedSeatsCount";

    String JSON_PATH_ROOM_ID = "$.room.id";

    String JSON_PATH_SEATS_COUNT ="$.seatsCount";

    String JSON_PATH_ROOM_AVAILABILITY ="$.roomAvailability";

    String JSON_PATH_FLOOR ="$.floor";

    String JSON_PATH_1ST_ID = "$[0].id";

    String JSON_PATH_1ST_DESCRIPTION ="$[0].description";

    String JSON_PATH_1ST_REQUESTED_SEATS_COUNT = "$[0].requestedSeatsCount";
}
