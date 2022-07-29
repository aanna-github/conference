package com.example.conference.controller;

import com.example.conference.controller.dto.conference.create.ConferenceDto;
import com.example.conference.controller.dto.conference.create.ParticipantDto;
import com.example.conference.controller.dto.conference.response.ConferenceResponseDto;
import com.example.conference.controller.dto.conference.response.ParticipantResponseDto;
import com.example.conference.controller.dto.conference.update.ConferenceUpdateDto;
import com.example.conference.exception.InvalidInputException;
import com.example.conference.service.ConferenceService;
import com.example.conference.utility.enumeration.ConferenceStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/conferences")
@AllArgsConstructor
//@NoArgsConstructor
@Slf4j
public class ConferenceController {

    @Autowired
    private final ConferenceService conferenceService;

    @GetMapping(value = "/check")
    public ResponseEntity<String> checkStatus() {
        return ResponseEntity.ok("OK conference");
    }

    @PostMapping
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
    public ResponseEntity<ConferenceResponseDto> getConferenceById(@PathVariable("conferenceId") String conferenceId) {
        log.debug("ConferenceController.getConferenceById method has been called");

        final ConferenceResponseDto conferenceResponse = conferenceService.getConferenceById(conferenceId);
        return ResponseEntity.ok(conferenceResponse);
    }

    @PostMapping(value = "/{conferenceId}/participants")
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
