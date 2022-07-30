package com.example.conference.service;

import com.example.conference.controller.dto.room.create.RoomDto;
import com.example.conference.controller.dto.room.response.RoomResponseDto;
import com.example.conference.controller.dto.room.update.RoomUpdateDto;
import com.example.conference.dao.document.Conference;
import com.example.conference.dao.document.Room;
import com.example.conference.dao.document.RoomAvailability;
import com.example.conference.dao.repository.ConferenceRepository;
import com.example.conference.dao.repository.RoomRepository;
import com.example.conference.exception.DocumentNotFoundException;
import com.example.conference.exception.RoomBusyException;
import com.example.conference.utility.enumeration.RoomStatus;
import com.example.conference.utility.mapper.CommonMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonTimestamp;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;

    private final ConferenceRepository conferenceRepository;

    private final CommonMapper commonMapper;

    public RoomResponseDto saveRoom(RoomDto roomDto) {
        log.debug("RoomService.saveRoom method has been called");

        Room room = roomRepository.save(commonMapper.dtoToDao(roomDto));
        return commonMapper.daoToRoomResponseDto(room);
    }

    public RoomResponseDto updateRoom(RoomUpdateDto roomUpdateDto, @NonNull String roomId) {
        log.debug("RoomService.updateRoom method has been called. roomId: {}", roomId);

        Optional<Room> roomById = roomRepository.findById(roomId);
        if (roomById.isPresent()) {
            Room room = roomById.get();
            commonMapper.updateRoom(roomUpdateDto, room);
            roomRepository.save(room);
            return commonMapper.daoToRoomResponseDto(room);
        } else {
            throw new DocumentNotFoundException("No Conference found for id: " + roomId);
        }
    }

    public RoomResponseDto bookRoomByEventDate(@NonNull String roomId, @NonNull Integer requestedSeatsCount, LocalDateTime eventDate) {
        log.debug("RoomService.bookRoomByEventDate method has been called. roomId: {}, requestedSeatsCount: {}, eventDate: {}",
                roomId, requestedSeatsCount, eventDate);

        final Room room = findByIdAndSeatsCountIsGreaterThanEqual(roomId, requestedSeatsCount);

        if (RoomUtility.isRoomAvailabilityForTheDate(room, commonMapper.toBsonTimestamp(eventDate))) {
            return bookRoom(room, eventDate);
        }
        log.error("The room {} doesn't available for the date : {}", roomId, eventDate);
        throw new RoomBusyException("The room doesn't available for the date", roomId, eventDate);
    }

    public RoomResponseDto bookRoomByConferenceId(@NonNull String roomId, @NonNull String conferenceId) {
        log.debug("RoomService.bookRoomByConferenceId method has been called. roomId: {}, conferenceId: {}", roomId, conferenceId);

        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new DocumentNotFoundException("There is no conference with id:" + conferenceId));

        Room room = findByIdAndSeatsCountIsGreaterThanEqual(roomId, conference.getRequestedSeatsCount());

        LocalDateTime eventualDateTime = commonMapper.fromBsonTimestamp(conference.getEventDate());

        if (RoomUtility.isRoomAvailabilityForTheDate(room, conference.getEventDate())) {
            if (conference.getRoom() != null) {
                log.info("Canceling the previous booked room for the conference. roomId: {}", roomId);
                cancelBooking(conference.getRoom(), conference.getEventDate());
            }
            final RoomResponseDto roomResponseDto = bookRoom(room, eventualDateTime);
            conference.setRoom(room);
            conferenceRepository.save(conference);
            return roomResponseDto;
        } else {
            log.error("The room {} doesn't available for the date : {}", roomId, conference.getEventDate());
            throw new RoomBusyException("The room doesn't available for the date", roomId, eventualDateTime);
        }
    }

    public List<RoomResponseDto> checkRoomsAvailabilityByRequestedProp(@NonNull Integer requestedSeatsCount, LocalDateTime eventDate) {
        log.debug("RoomService.checkRoomsAvailabilityByRequestedProp method has been called");
        List<Room> roomList = roomRepository.findBySeatsCountIsGreaterThanEqual(requestedSeatsCount);

        if (roomList.isEmpty()) {
            throw new DocumentNotFoundException(
                    String.format("Could not find any room with requested properties. seats count:%d, status:%s",
                            requestedSeatsCount, RoomStatus.FREE));
        }

        List<RoomResponseDto> availableRooms = new ArrayList<>();
        final BsonTimestamp eventDateBsonTimestamp = commonMapper.toBsonTimestamp(eventDate);
        for (Room room : roomList) {
            if (RoomUtility.isRoomAvailabilityForTheDate(room, eventDateBsonTimestamp)) {
                availableRooms.add(commonMapper.daoToRoomResponseDto(room));
            } else {
                log.info("The room with id: {} is no available for the date: {}", room.getId(), eventDate);
            }
        }

        if (availableRooms.isEmpty()) {
            log.error("There are no available room for the date : {}", eventDate);
            throw new RoomBusyException("There are no room available for the date", eventDate);
        }
        return availableRooms;
    }

    public List<RoomResponseDto> getAllRooms() {
        log.debug("RoomService.getAllRooms method has been called");

        List<Room> rooms = roomRepository.findAll();
        return commonMapper.daoListToRoomResponseDtoList(rooms);
    }

    public void cancelBooking(Room room, BsonTimestamp bookDate) {
        log.debug("RoomService.cancelBooking method has been called. roomId: {}, bookDate: {}", room.getId(),
                commonMapper.fromBsonTimestamp(bookDate));

        RoomAvailability roomAvailability = room.getRoomAvailability()
                .stream()
                .filter(availability -> availability.getEventDate().equals(bookDate))
                .findFirst()
                .orElseThrow(() -> new DocumentNotFoundException("There is no booking for the date"));

        room.getRoomAvailability().remove(roomAvailability);
        roomRepository.save(room);

        log.info("The booking for the room {} has been successfully canceled for the date {}", room.getId(), bookDate);
    }


    public Room findById(String roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new DocumentNotFoundException(
                String.format("Could not find the room with requested id:%s",
                        roomId)));
    }

    protected RoomResponseDto bookRoom(Room room, LocalDateTime bookDate) {
        log.debug("RoomService.Room method has been called for room: {} and date: {}", room.getId(), bookDate);

        room.getRoomAvailability().add(new RoomAvailability(RoomStatus.BOOKED.name(), commonMapper.toBsonTimestamp(bookDate)));
        roomRepository.save(room);
        return commonMapper.daoToRoomResponseDto(room);
    }

    private Room findByIdAndSeatsCountIsGreaterThanEqual(String roomId, Integer requestedSeatsCount) {
        return roomRepository.findByIdAndSeatsCountIsGreaterThanEqual(roomId,
                requestedSeatsCount)
                .orElseThrow(() -> new DocumentNotFoundException(
                        String.format("Could not find the room with requested properties. Id:%s, seats count:%d, status:%s",
                                roomId, requestedSeatsCount, RoomStatus.FREE)));
    }

    static class RoomUtility {
        private RoomUtility() {
        }

        private static boolean isRoomAvailabilityForTheDate(Room room, BsonTimestamp eventDate) {
            log.debug("RoomService.isRoomAvailabilityForTheDate method has been called for the room. roomId: {}, eventDate[BsonTimestamp]: {}",
                    room.getId(), eventDate);
            if (room.getRoomAvailability() == null || room.getRoomAvailability().isEmpty()) {
                log.info("Requested room is available to booked for the date: status of the room {}. roomId: {}, eventDate[BsonTimestamp]: {}", RoomStatus.FREE, room.getId(), eventDate);
                return true;
            }

            for (RoomAvailability roomAvailability : room.getRoomAvailability()) {
                if (roomAvailability.getEventDate().equals(eventDate)) {
                    return false;
                }
            }
            return true;
        }
    }
}
