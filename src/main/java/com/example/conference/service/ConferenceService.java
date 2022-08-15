package com.example.conference.service;

import com.example.conference.controller.payload.request.ConferenceDto;
import com.example.conference.controller.payload.request.ParticipantDto;
import com.example.conference.controller.payload.response.ConferenceResponseDto;
import com.example.conference.controller.payload.response.ParticipantResponseDto;
import com.example.conference.controller.payload.request.ConferenceUpdateDto;
import com.example.conference.controller.payload.response.RoomResponseDto;
import com.example.conference.dao.document.Conference;
import com.example.conference.dao.document.Participant;
import com.example.conference.dao.document.Room;
import com.example.conference.dao.repository.ConferenceRepository;
import com.example.conference.exception.DocumentNotFoundException;
import com.example.conference.exception.InvalidInputException;
import com.example.conference.utility.enumeration.ConferenceStatus;
import com.example.conference.utility.mapper.CommonMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ConferenceService {
    private final ConferenceRepository conferenceRepository;

    private final RoomService roomService;

    private final CommonMapper commonMapper;

    public ConferenceResponseDto saveConference(ConferenceDto conferenceDto) {
        log.debug("ConferenceService.saveConference method has been called");

        Conference conference = commonMapper.dtoToDao(conferenceDto);
        conference.setStatus(ConferenceStatus.SCHEDULED.name());
        if (conferenceDto.getRoomId() != null && !conferenceDto.getRoomId().isEmpty()) {
            final RoomResponseDto roomResponseDto = roomService.bookRoomByEventDate(conferenceDto.getRoomId(),
                    conferenceDto.getRequestedSeatsCount(), conferenceDto.getEventDate());

            log.info("The following room {} has been booked for the conference: " + roomResponseDto.getId());
            log.info(roomResponseDto.toString());

            conference.setRoom(roomService.findById(conferenceDto.getRoomId()));
        }

        conferenceRepository.save(conference);
        log.info("A new conference {} has been created successfully", conference.getId());
        return commonMapper.daoToDResponseDto(conference);

    }

    public ConferenceResponseDto updateConference(ConferenceUpdateDto conferenceUpdateDto, @NonNull String conferenceId) {
        log.debug("ConferenceService.updateConference method has been called. conferenceId: {}", conferenceId);

        validateStatus(conferenceUpdateDto);

        Optional<Conference> byId = conferenceRepository.findById(conferenceId);
        if (byId.isPresent()) {
            Conference conference = byId.get();
            ConferenceUtility.validateSeatsCount(conferenceUpdateDto.getRequestedSeatsCount(), conference.getRoom());
            commonMapper.updateConference(conferenceUpdateDto, conference);
            conferenceRepository.save(conference);
            return commonMapper.daoToDResponseDto(conference);
        } else {
            throw new DocumentNotFoundException("No Conference found for id: " + conferenceId);
        }
    }

    public ConferenceResponseDto cancelConference(@NonNull String conferenceId) {
        log.debug("ConferenceService.cancelConference method has been called. conferenceId: {}", conferenceId);

        Optional<Conference> byId = conferenceRepository.findById(conferenceId);
        if (byId.isPresent()) {
            Conference conference = byId.get();
            if (ConferenceStatus.CANCELED.name().equals(conference.getStatus())) {
                log.warn("The conference {} has been already canceled" + conference.getId());
                throw new InvalidInputException("The conference has been already canceled:" + conference.getId());
            }
            conference.setStatus(ConferenceStatus.CANCELED.name());
            conferenceRepository.save(conference);
            return commonMapper.daoToDResponseDto(conference);
        } else {
            throw new DocumentNotFoundException("No Conference found for id: " + conferenceId);
        }
    }

    public List<ConferenceResponseDto> getAllConferences() {
        log.debug("ConferenceService.getAllConferences method has been called");

        List<Conference> conferences = conferenceRepository.findAll();
        return commonMapper.daoListToConferenceResponseDtoList(conferences);
    }


    public ConferenceResponseDto getConferenceById(@NonNull String conferenceId) {
        log.debug("ConferenceService.getConferenceById {}  method has been called", conferenceId);

        return commonMapper.daoToDResponseDto(conferenceRepository.findById(conferenceId).orElseThrow(() -> new DocumentNotFoundException(
                String.format("No Conference found for id: %s", conferenceId))));
    }


    private void validateStatus(ConferenceUpdateDto conferenceUpdateDto) {
        log.debug("ConferenceService.validateStatus method has been called");

        if (conferenceUpdateDto.getStatus() != null && (conferenceUpdateDto.getStatus().isEmpty() || ConferenceStatus.findByName(conferenceUpdateDto.getStatus()) == null)) {
            throw new InvalidInputException(String.format("Given conference status is invalid:%s. Valid statuses are:%s",
                    conferenceUpdateDto.getStatus(), Arrays.toString(ConferenceStatus.values())));
        }
    }

    public ParticipantResponseDto addParticipant(ParticipantDto participantDto, @NonNull String conferenceId) {
        log.debug("ConferenceService.addParticipant method has been called. conferenceId: {}", conferenceId);

        Conference conference = conferenceRepository.findById(conferenceId).orElseThrow(() -> new DocumentNotFoundException(
                String.format("No Conference found for id: %sCould not add a participant for the conference", conferenceId)));

        if (ConferenceStatus.CANCELED.name().equals(conference.getStatus())) {
            throw new InvalidInputException("The conference has been canceled. Unable to add a participant: status: " + conference.getStatus());
        }

        if (conference.getParticipants().size() < conference.getRequestedSeatsCount()) {
            Participant participant = commonMapper.dtoToDao(participantDto);
            participant.setId(UUID.randomUUID().toString());
            conference.getParticipants().add(participant);
            conferenceRepository.save(conference);
            return commonMapper.daoToDResponseDto(participant);
        }
        throw new InvalidInputException("There is no free space to add new participant. seats: " + conference.getRequestedSeatsCount());
    }

    public ConferenceResponseDto removeParticipant(@NonNull String participantId, @NonNull String conferenceId) {
        log.debug("ConferenceService.removeParticipant method has been called. conferenceId: {} ,participantId: {}",
                conferenceId, participantId);

        Conference conference = conferenceRepository.findById(conferenceId).orElseThrow(() -> new DocumentNotFoundException(
                String.format("No Conference found for id: %s Could not remove a participant from the conference", conferenceId)));

        if (!ConferenceStatus.SCHEDULED.name().equals(conference.getStatus())) {
            throw new InvalidInputException(String.format("Unable to remove a participant from the conference which has %s. Status should be: %s",
                    conference.getStatus(), ConferenceStatus.SCHEDULED.name()));
        }

        if (conference.getParticipants() != null) {
            Participant participantToRemove = conference.getParticipants()
                    .stream()
                    .filter(participant -> participantId.equals(participant.getId()))
                    .findFirst()
                    .orElseThrow(() -> new DocumentNotFoundException(String.format(
                            "Could not find a participant with id for the conference. conferenceId: %s, participantId:%s",
                            conferenceId, participantId)));

            conference.getParticipants().remove(participantToRemove);
            conferenceRepository.save(conference);
            return commonMapper.daoToDResponseDto(conference);
        }

        throw new DocumentNotFoundException("There is no registered participants for the conference. conferenceId: " + conference.getId());
    }

    static class ConferenceUtility {
        private ConferenceUtility() {
        }

        private static void validateSeatsCount(Integer newCount, Room room) {
            if (newCount != null && room != null && room.getSeatsCount() < newCount) {
                log.info("New requested seats count for the conference {} ", newCount);
                log.error("The booked room for the conference doesn't have enough seats, room seats count {}", room.getSeatsCount());
                throw new InvalidInputException(String.format("The booked room %s for the conference doesn't have enough seats, room seats count:%d",
                        room.getId(), room.getSeatsCount()));
            }
        }
    }

}
