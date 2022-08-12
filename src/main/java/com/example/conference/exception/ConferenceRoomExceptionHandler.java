package com.example.conference.exception;

import com.example.conference.controller.payload.response.ErrorResponseDto;
import com.example.conference.utility.enumeration.ErrorConstants;
import com.example.conference.utility.enumeration.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ConferenceRoomExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex) {
        final String message = ex.getCause() != null ? String.format("%s.%s", ex.getCause().getMessage(), ex.getMessage()) : ex.getMessage();
        log.error(String.format("Internal Server Exception: %s", ex.getMessage()), ex);
        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                ErrorConstants.INTERNAL_SERVER_ERROR_TITLE.getErrorMessage(), message, LocalDateTime.now().toString(),
                ErrorType.API_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {InvalidInputException.class})
    public ResponseEntity<ErrorResponseDto> handleInvalidInputException(InvalidInputException ex) {
        log.error(String.format("Invalid Input Exception: %s", ex.getMessage()), ex);
        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                ErrorConstants.INVALID_INPUT_ERROR_TITLE.getErrorMessage(), ex.getMessage(), LocalDateTime.now().toString(),
                ErrorType.INVALID_REQUEST_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {DocumentNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleDocumentNotFoundException(DocumentNotFoundException ex) {
        log.error(String.format("Document Not Found Exception: %s", ex.getMessage()), ex);
        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(HttpStatus.NOT_FOUND.value()),
                ErrorConstants.NOT_FOUND_ERROR_TITLE.getErrorMessage(), ex.getMessage(), LocalDateTime.now().toString(),
                ErrorType.INVALID_REQUEST_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {RoomBusyException.class})
    public ResponseEntity<ErrorResponseDto> handleRoomBusyException(RoomBusyException ex) {
        log.error(String.format("Room Busy Exception: %s", ex.getMessage()), ex);
        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                ErrorConstants.ROOM_BUSY_ERROR_TITLE.getErrorMessage(), ex.getMessage(), LocalDateTime.now().toString(),
                ErrorType.INVALID_REQUEST_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(String.format("Method Argument Not Valid Exception: %s", ex.getMessage()), ex);

        List<String> messages = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> String.format("%s %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());


        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                ErrorConstants.INVALID_INPUT_ERROR_TITLE.getErrorMessage(), messages.toString(), LocalDateTime.now().toString(),
                ErrorType.INVALID_REQUEST_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error(String.format("Constraint Violation Exception: %s", ex.getMessage()), ex);

        List<String> messages = ex.getConstraintViolations()
                .stream()
                .map(constraintViolation -> String.format("%s %s", constraintViolation.getPropertyPath(), constraintViolation.getMessage()))
                .collect(Collectors.toList());

        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                ErrorConstants.INVALID_INPUT_ERROR_TITLE.getErrorMessage(), messages.toString(), LocalDateTime.now().toString(),
                ErrorType.INVALID_REQUEST_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException ex) {
        log.error(String.format("Bad Credentials Exception: %s", ex.getMessage()), ex);
        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                ErrorConstants.BAD_CREDENTIALS.getErrorMessage(), ex.getMessage(), LocalDateTime.now().toString(),
                ErrorType.AUTHENTICATION_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException ex) {
        log.error(String.format("Access Denied Exception: %s", ex.getMessage()), ex);
        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                ErrorConstants.ACCESS_DENIED.getErrorMessage(), ex.getMessage(), LocalDateTime.now().toString(),
                ErrorType.AUTHORIZATION_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex) {
        log.error(String.format("Authentication Credentials Not FoundException: %s", ex.getMessage()), ex);
        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                ErrorConstants.NOT_FOUND_CREDENTIALS.getErrorMessage(), ex.getMessage(), LocalDateTime.now().toString(),
                ErrorType.AUTHORIZATION_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
