package com.example.conference.controller;

import com.example.conference.controller.payload.request.RoomDto;
import com.example.conference.controller.payload.response.RoomResponseDto;
import com.example.conference.controller.payload.request.RoomUpdateDto;
import com.example.conference.exception.InvalidInputException;
import com.example.conference.service.RoomService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/rooms")
@AllArgsConstructor
@Slf4j
@ApiOperation(value = "Bearer ", authorizations = {@Authorization(value = "jwtToken")})
public class RoomController {

    private final RoomService roomService;

    @GetMapping(value = "/check")
    public ResponseEntity<String> checkStatus() {
        return ResponseEntity.ok("OK room");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Bearer ", authorizations = {@Authorization(value = "jwtToken")})
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
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Bearer ", authorizations = {@Authorization(value = "jwtToken")})
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
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @ApiOperation(value = "Bearer ", authorizations = {@Authorization(value = "jwtToken")})
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
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @ApiOperation(value = "Bearer ", authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<List<RoomResponseDto>> checkRoomsAvailability(@RequestParam(value = "seats") Integer seats,
                                                                        @RequestParam(value = "eventDate")
                                                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventDate) {

        log.debug("RoomController.checkRoomsAvailability method has been called");

        List<RoomResponseDto> roomResponses = roomService.checkRoomsAvailabilityByRequestedProp(seats, eventDate);
        return ResponseEntity.ok(roomResponses);
    }

    @PatchMapping("/book/{roomId}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @ApiOperation(value = "Bearer ", authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<RoomResponseDto> bookRoomForConference(@PathVariable(value = "roomId") String roomId,
                                                                 @RequestHeader(value = "conferenceId") String conferenceId) {

        log.debug("RoomController.checkRoomAvailability method has been called");

        RoomResponseDto roomResponse = roomService.bookRoomByConferenceId(roomId, conferenceId);
        return ResponseEntity.ok(roomResponse);
    }
}
