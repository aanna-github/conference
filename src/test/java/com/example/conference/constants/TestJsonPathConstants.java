package com.example.conference.constants;

public interface TestJsonPathConstants {
    String jsonPathStart = "$";

    String jsonPathId = "$.id";

    String jsonPathEventDate =" $.eventDate";

    String jsonPathDescription ="$.description";

    String jsonPathRequestedSeatsCount = "$.requestedSeatsCount";

    String jsonPathRoomId = "$.room.id";

    String jsonPathNumber =" $.number";

    String jsonPathSeatsCount ="$.seatsCount";

    String jsonPathRoomAvailability ="$.roomAvailability";

    String jsonPathFloor ="$.floor";

    String jsonPath1stId = "$[0].id";

    String jsonPath1stDescription ="$[0].description";

    String jsonPath1stRequestedSeatsCount = "$[0].requestedSeatsCount";
}
