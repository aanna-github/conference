package com.example.conference.controller;

import com.example.conference.controller.dto.room.create.RoomDto;
import com.example.conference.controller.dto.room.response.RoomResponseDto;
import com.example.conference.controller.dto.room.update.RoomUpdateDto;
import com.example.conference.exception.InvalidInputException;
import com.example.conference.service.RoomService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@AllArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @GetMapping(value = "/check")
    public ResponseEntity<String> checkStatus() {
        return ResponseEntity.ok("OK room");
    }

    @PostMapping
    public ResponseEntity<RoomResponseDto> addRoom(@Valid @RequestBody RoomDto roomDto) {
        log.debug("RoomController.addRoom method has been called");

        RoomResponseDto roomResponseDto = roomService.saveRoom(roomDto);
        if (roomResponseDto != null) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(roomResponseDto.getId())
                    .toUri();

            log.info("A new room has been created: {}", location);
            return ResponseEntity.created(location).body(roomResponseDto);
        }
        throw new InvalidInputException("Can not create the document relevant to that data.");
    }

    @PatchMapping(value = "/{roomId}")
    public ResponseEntity<RoomResponseDto> updateRoom(@Valid @RequestBody RoomUpdateDto roomUpdateDto,
                                                      @PathVariable("roomId") String roomId) {

        log.debug("RoomController.updateRoom method has been called");

        RoomResponseDto roomResponseDto = roomService.updateRoom(roomUpdateDto, roomId);
        if (roomResponseDto != null) {
            return ResponseEntity.ok(roomResponseDto);
        }

        throw new InvalidInputException("Can not update the document relevant to that data.");
    }

    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getAllRooms() {
        log.debug("RoomController.getAllRooms method has been called");

        List<RoomResponseDto> roomResponses = roomService.getAllRooms();
        if (roomResponses.isEmpty()) {
            log.warn("There is no any room to return");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roomResponses);
    }

    @GetMapping("/availability")
    public ResponseEntity<List<RoomResponseDto>> checkRoomsAvailability(@RequestParam(value = "seats") Integer seats,
                                                                        @RequestParam(value = "eventDate")
                                                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventDate) {

        log.debug("RoomController.checkRoomsAvailability method has been called");

        List<RoomResponseDto> roomResponses = roomService.checkRoomsAvailabilityByRequestedProp(seats, eventDate);
        return ResponseEntity.ok(roomResponses);
    }

    @PatchMapping("/book/{roomId}")
    public ResponseEntity<RoomResponseDto> bookRoomForConference(@PathVariable(value = "roomId") String roomId,
                                                                 @RequestHeader(value = "conferenceId") String conferenceId) {

        log.debug("RoomController.checkRoomAvailability method has been called");

        RoomResponseDto roomResponse = roomService.bookRoomByConferenceId(roomId, conferenceId);
        return ResponseEntity.ok(roomResponse);
    }
}
