package com.example.conference.controller;

import com.example.conference.controller.payload.request.ConferenceDto;
import com.example.conference.controller.payload.request.ParticipantDto;
import com.example.conference.controller.payload.response.ConferenceResponseDto;
import com.example.conference.controller.payload.response.ParticipantResponseDto;
import com.example.conference.controller.payload.request.ConferenceUpdateDto;
import com.example.conference.exception.InvalidInputException;
import com.example.conference.service.ConferenceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/conferences")
@AllArgsConstructor
@Slf4j
public class ConferenceController {

    private final ConferenceService conferenceService;

    @GetMapping(value = "/check")
    public ResponseEntity<String> checkStatus() {
        return ResponseEntity.ok("OK conference");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "To create a new conference. The room can be assigned during the conference creation or by book/{roomId} endpoint  ",
            authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<ConferenceResponseDto> addConference(@Valid @RequestBody ConferenceDto conferenceDto) {
        log.debug("ConferenceController.addConference method has been called");

        ConferenceResponseDto conferenceResponseDto = conferenceService.saveConference(conferenceDto);
        if (conferenceResponseDto != null) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(conferenceResponseDto.getId())
                    .toUri();

            log.info("A new conference has been created: {}", location);
            return ResponseEntity.created(location).body(conferenceResponseDto);
        }
        throw new InvalidInputException("Can not create the document relevant to that data.");
    }

    @PatchMapping(value = "/{conferenceId}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @ApiOperation(value = "To update a conferance", authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<ConferenceResponseDto> updateConference(@Valid @RequestBody ConferenceUpdateDto conferenceUpdateDto,
                                                                  @PathVariable("conferenceId") String conferenceId) {

        log.debug("ConferenceController.updateConference method has been called");

        ConferenceResponseDto conferenceResponseDto = conferenceService.updateConference(conferenceUpdateDto, conferenceId);
        if (conferenceResponseDto != null) {
            log.info("The conference has been updated successfully");
            return ResponseEntity.ok(conferenceResponseDto);
        }

        throw new InvalidInputException("Can not update the conference relevant to that data.");
    }

    @PatchMapping(value = "/cancel/{conferenceId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "To cancel a conferance ", authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<ConferenceResponseDto> cancelConference(@PathVariable("conferenceId") String conferenceId) {
        log.debug("ConferenceController.cancelConference method has been called");

        ConferenceResponseDto conferenceResponseDto = conferenceService.cancelConference(conferenceId);
        if (conferenceResponseDto != null) {
            log.info("The conference has been canceled successfully");
            return ResponseEntity.ok(conferenceResponseDto);
        }

        throw new InvalidInputException("Can not cancel the document relevant to that data.");
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @ApiOperation(value = "To see all conferances (canceled and sheduled)", authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<List<ConferenceResponseDto>> getAllConferences() {
        log.debug("ConferenceController.getAllConferences method has been called");

        List<ConferenceResponseDto> conferenceResponses = conferenceService.getAllConferences();
        if (conferenceResponses.isEmpty()) {
            log.warn("There are no any created conference");
            return new ResponseEntity<>(conferenceResponses, HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(conferenceResponses);
    }

    @GetMapping(value = "/{conferenceId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @ApiOperation(value = "To see conference by id", authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<ConferenceResponseDto> getConferenceById(@PathVariable("conferenceId") String conferenceId) {
        log.debug("ConferenceController.getConferenceById method has been called");

        final ConferenceResponseDto conferenceResponse = conferenceService.getConferenceById(conferenceId);
        return ResponseEntity.ok(conferenceResponse);
    }

    @PostMapping(value = "/{conferenceId}/participants")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @ApiOperation(value = "To add a new participant to the scheduled conference", authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<ParticipantResponseDto> addParticipant(@PathVariable("conferenceId") String conferenceId,
                                                                 @Valid @RequestBody ParticipantDto participantDto) {
        log.debug("ConferenceController.addParticipant method has been called");

        ParticipantResponseDto participantResponseDto = conferenceService.addParticipant(participantDto, conferenceId);
        if (participantResponseDto != null) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(participantResponseDto.getId())
                    .toUri();
            log.info("The new participant has been canceled successfully");
            return ResponseEntity.created(location).body(participantResponseDto);
        }

        throw new InvalidInputException("Can not update the document relevant to that data.");
    }

    @DeleteMapping(value = "/{conferenceId}/participants/{participantId}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @ApiOperation(value = "To remove participant from the sheduled conference", authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<ConferenceResponseDto> removeParticipant(@PathVariable("participantId") String participantId,
                                                                   @PathVariable("conferenceId") String conferenceId) {

        log.debug("ConferenceController.removeParticipant method has been called");

        ConferenceResponseDto conferenceResponseDto = conferenceService.removeParticipant(participantId, conferenceId);
        if (conferenceResponseDto != null) {
            log.info("The participant: {}  has been removed from conference: {} successfully", participantId, conferenceId);
            return ResponseEntity.ok(conferenceResponseDto);
        }

        throw new InvalidInputException("Can not update the document relevant to that data.");
    }
}
