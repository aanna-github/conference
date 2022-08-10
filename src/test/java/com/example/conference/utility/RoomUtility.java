package com.example.conference.utility;

import com.example.conference.constants.TestJsonObjectPropertyContents;
import com.example.conference.constants.TestMockValueConstants;
import com.example.conference.controller.dto.room.create.AddressDto;
import com.example.conference.controller.dto.room.create.RoomDto;
import com.example.conference.controller.dto.room.response.AddressResponseDto;
import com.example.conference.controller.dto.room.response.RoomResponseDto;
import com.example.conference.controller.dto.room.update.RoomUpdateDto;
import com.example.conference.dao.document.Address;
import com.example.conference.dao.document.Room;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;


public class RoomUtility {

    public static RoomDto buildRoomDtoWithRequiredProp() {
        return RoomDto.builder()
                .seatsCount(TestMockValueConstants.SEATS_COUNT_MOCK_VALUE)
                .number(TestMockValueConstants.REQUESTED_SEATS_COUNT_MOCK_VALUE)
                .floor(TestMockValueConstants.FLOOR_MOCK_VALUE)
                .address(buildAddressDtoWithRequiredProp())
                .build();
    }

    public static Room buildRoomWithRequiredProp() {
        return Room.builder()
                .seatsCount(TestMockValueConstants.SEATS_COUNT_MOCK_VALUE)
                .number(TestMockValueConstants.REQUESTED_SEATS_COUNT_MOCK_VALUE)
                .floor(TestMockValueConstants.FLOOR_MOCK_VALUE)
                .address(buildAddressWithRequiredProp())
                .build();
    }

    private static AddressDto buildAddressDtoWithRequiredProp() {
        return AddressDto.builder()
                .addressLine1(TestMockValueConstants.ADDRESS_LINE1_MOCK_VALUE)
                .addressLine2(TestMockValueConstants.ADDRESS_LINE2_MOCK_VALUE)
                .timezone(TestMockValueConstants.TIMEZONE_MOCK_VALUE)
                .latitude(TestMockValueConstants.LATITUDE_MOCK_VALUE)
                .longitude(TestMockValueConstants.LONGITUDE_MOCK_VALUE)
                .build();
    }

    private static Address buildAddressWithRequiredProp() {
        return Address.builder()
                .addressLine1(TestMockValueConstants.ADDRESS_LINE1_MOCK_VALUE)
                .addressLine2(TestMockValueConstants.ADDRESS_LINE2_MOCK_VALUE)
                .timezone(TestMockValueConstants.TIMEZONE_MOCK_VALUE)
                .latitude(TestMockValueConstants.LATITUDE_MOCK_VALUE)
                .longitude(TestMockValueConstants.LONGITUDE_MOCK_VALUE)
                .build();
    }

    private static AddressResponseDto buildAddressResponse() {
        return AddressResponseDto.builder()
                .addressLine1(TestMockValueConstants.ADDRESS_LINE1_MOCK_VALUE)
                .addressLine2(TestMockValueConstants.ADDRESS_LINE2_MOCK_VALUE)
                .timezone(TestMockValueConstants.TIMEZONE_MOCK_VALUE)
                .latitude(TestMockValueConstants.LATITUDE_MOCK_VALUE)
                .longitude(TestMockValueConstants.LONGITUDE_MOCK_VALUE)
                .build();
    }

    public static RoomUpdateDto buildRoomUpdateDto(Integer number, Integer seatsCount) {
        RoomUpdateDto roomUpdateDto = RoomUpdateDto.builder().build();
        if (number != null) {
            roomUpdateDto.setNumber(number);
        }

        if (seatsCount != null) {
            roomUpdateDto.setSeatsCount(seatsCount);
        }
        return roomUpdateDto;
    }

    public static RoomResponseDto buildRoomResponseDto(JSONObject roomParams) {
            return RoomResponseDto.builder()
                    .seatsCount(TestMockValueConstants.SEATS_COUNT_MOCK_VALUE)
                    .number(TestMockValueConstants.REQUESTED_SEATS_COUNT_MOCK_VALUE)
                    .floor(TestMockValueConstants.FLOOR_MOCK_VALUE)
                    .address(buildAddressResponse())
                    .build();
    }

    public static JSONObject buildRoomJsonWithRequiredProp() throws JSONException {
        JSONObject addressParams = generateDefaultAddressParams();
        JSONObject roomParams = new JSONObject();
        roomParams.put(TestJsonObjectPropertyContents.SEATS_COUNT, TestMockValueConstants.SEATS_COUNT_MOCK_VALUE);
        roomParams.put(TestJsonObjectPropertyContents.NUMBER, TestMockValueConstants.NUMBER);
        roomParams.put(TestJsonObjectPropertyContents.FLOOR, TestMockValueConstants.FLOOR_MOCK_VALUE);
        roomParams.put(TestJsonObjectPropertyContents.ADDRESS, addressParams);

        return roomParams;
    }

    public static RoomResponseDto jsonToRoomResponseDto(JSONObject roomParams) throws JSONException {
        RoomResponseDto roomResponseDto = new RoomResponseDto();
        if (roomParams.has(TestJsonObjectPropertyContents.ID_PROPERTY)) {
            roomResponseDto.setId(roomParams.get(TestJsonObjectPropertyContents.ID_PROPERTY).toString());
        }
        if (roomParams.has(TestJsonObjectPropertyContents.SEATS_COUNT)) {
            roomResponseDto.setSeatsCount((Integer) roomParams.get(TestJsonObjectPropertyContents.SEATS_COUNT));

        }
        if (roomParams.has(TestJsonObjectPropertyContents.NUMBER)) {
            roomResponseDto.setNumber((Integer) roomParams.get(TestJsonObjectPropertyContents.NUMBER));
        }

        if (roomParams.has(TestJsonObjectPropertyContents.FLOOR)) {
            roomResponseDto.setFloor((Integer) roomParams.get(TestJsonObjectPropertyContents.FLOOR));
        }

        if (roomParams.has(TestJsonObjectPropertyContents.ADDRESS)) {
            roomResponseDto.setAddress(jsonToAddressResponseDto((JSONObject) roomParams.get(TestJsonObjectPropertyContents.ADDRESS)));
        }

        return roomResponseDto;
    }

    private static AddressResponseDto jsonToAddressResponseDto(JSONObject addressParams) throws JSONException {
        AddressResponseDto addressResponseDto = new AddressResponseDto();
        if (addressParams.has(TestJsonObjectPropertyContents.ADDRESS_LINE1_PROPERTY)) {
            addressResponseDto.setAddressLine1(addressParams.get(TestJsonObjectPropertyContents.ADDRESS_LINE1_PROPERTY).toString());
        }
        if (addressParams.has(TestJsonObjectPropertyContents.ADDRESS_LINE2_PROPERTY)) {
            addressResponseDto.setAddressLine2(addressParams.get(TestJsonObjectPropertyContents.ADDRESS_LINE2_PROPERTY).toString());

        }
        if (addressParams.has(TestJsonObjectPropertyContents.TIMEZONE_PROPERTY)) {
            addressResponseDto.setTimezone(addressParams.get(TestJsonObjectPropertyContents.TIMEZONE_PROPERTY).toString());
        }

        if (addressParams.has(TestJsonObjectPropertyContents.LONGITUDE_PROPERTY)) {
            addressResponseDto.setLongitude((Double) addressParams.get(TestJsonObjectPropertyContents.LONGITUDE_PROPERTY));
        }

        if (addressParams.has(TestJsonObjectPropertyContents.LATITUDE_PROPERTY)) {
            addressResponseDto.setLatitude((Double) addressParams.get(TestJsonObjectPropertyContents.LATITUDE_PROPERTY));
        }

        return addressResponseDto;
    }

    private static JSONObject generateDefaultAddressParams() throws JSONException {

        JSONObject addressParams = new JSONObject();
        addressParams.put(TestJsonObjectPropertyContents.ADDRESS_LINE1_PROPERTY, TestMockValueConstants.ADDRESS_LINE1_MOCK_VALUE);
        addressParams.put(TestJsonObjectPropertyContents.ADDRESS_LINE2_PROPERTY, TestMockValueConstants.ADDRESS_LINE2_MOCK_VALUE);
        addressParams.put(TestJsonObjectPropertyContents.TIMEZONE_PROPERTY, TestMockValueConstants.TIMEZONE_MOCK_VALUE);
        addressParams.put(TestJsonObjectPropertyContents.LATITUDE_PROPERTY, TestMockValueConstants.LATITUDE_MOCK_VALUE);
        addressParams.put(TestJsonObjectPropertyContents.LONGITUDE_PROPERTY, TestMockValueConstants.LONGITUDE_MOCK_VALUE);

        return addressParams;
    }
}
