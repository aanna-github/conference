package com.example.conference.utility;

import com.example.conference.constants.TestMockValueConstants;
import com.example.conference.controller.dto.room.create.AddressDto;
import com.example.conference.controller.dto.room.create.RoomDto;
import com.example.conference.controller.dto.room.update.RoomUpdateDto;
import com.example.conference.dao.document.Address;
import com.example.conference.dao.document.Room;


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

}
