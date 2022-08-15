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
        final HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(internalServerError.value()),
                ErrorConstants.INTERNAL_SERVER_ERROR_TITLE.getErrorMessage(), message, LocalDateTime.now().toString(),
                ErrorType.API_ERROR);
        return new ResponseEntity<>(errorResponse, internalServerError);
    }

    @ExceptionHandler(value = {InvalidInputException.class})
    public ResponseEntity<ErrorResponseDto> handleInvalidInputException(InvalidInputException ex) {
        log.error(String.format("Invalid Input Exception: %s", ex.getMessage()), ex);
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(badRequest.value()),
                ErrorConstants.INVALID_INPUT_ERROR_TITLE.getErrorMessage(), ex.getMessage(), LocalDateTime.now().toString(),
                ErrorType.INVALID_REQUEST_ERROR);
        return new ResponseEntity<>(errorResponse, badRequest);
    }

    @ExceptionHandler(value = {DocumentNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleDocumentNotFoundException(DocumentNotFoundException ex) {
        log.error(String.format("Document Not Found Exception: %s", ex.getMessage()), ex);
        final HttpStatus notFound = HttpStatus.NOT_FOUND;

        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(notFound.value()),
                ErrorConstants.NOT_FOUND_ERROR_TITLE.getErrorMessage(), ex.getMessage(), LocalDateTime.now().toString(),
                ErrorType.INVALID_REQUEST_ERROR);
        return new ResponseEntity<>(errorResponse, notFound);
    }

    @ExceptionHandler(value = {RoomBusyException.class})
    public ResponseEntity<ErrorResponseDto> handleRoomBusyException(RoomBusyException ex) {
        log.error(String.format("Room Busy Exception: %s", ex.getMessage()), ex);
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(badRequest.value()),
                ErrorConstants.ROOM_BUSY_ERROR_TITLE.getErrorMessage(), ex.getMessage(), LocalDateTime.now().toString(),
                ErrorType.INVALID_REQUEST_ERROR);
        return new ResponseEntity<>(errorResponse, badRequest);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(String.format("Method Argument Not Valid Exception: %s", ex.getMessage()), ex);
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        List<String> messages = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> String.format("%s %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());


        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(badRequest.value()),
                ErrorConstants.INVALID_INPUT_ERROR_TITLE.getErrorMessage(), messages.toString(), LocalDateTime.now().toString(),
                ErrorType.INVALID_REQUEST_ERROR);
        return new ResponseEntity<>(errorResponse, badRequest);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error(String.format("Constraint Violation Exception: %s", ex.getMessage()), ex);
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        List<String> messages = ex.getConstraintViolations()
                .stream()
                .map(constraintViolation -> String.format("%s %s", constraintViolation.getPropertyPath(), constraintViolation.getMessage()))
                .collect(Collectors.toList());

        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(badRequest.value()),
                ErrorConstants.INVALID_INPUT_ERROR_TITLE.getErrorMessage(), messages.toString(), LocalDateTime.now().toString(),
                ErrorType.INVALID_REQUEST_ERROR);
        return new ResponseEntity<>(errorResponse, badRequest);
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException ex) {
        log.error(String.format("Bad Credentials Exception: %s", ex.getMessage()), ex);
        final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;

        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(unauthorized.value()),
                ErrorConstants.BAD_CREDENTIALS.getErrorMessage(), ex.getMessage(), LocalDateTime.now().toString(),
                ErrorType.AUTHENTICATION_ERROR);
        return new ResponseEntity<>(errorResponse, unauthorized);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException ex) {
        log.error(String.format("Access Denied Exception: %s", ex.getMessage()), ex);
        final HttpStatus forbidden = HttpStatus.FORBIDDEN;

        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(forbidden.value()),
                ErrorConstants.ACCESS_DENIED.getErrorMessage(), ex.getMessage(), LocalDateTime.now().toString(),
                ErrorType.ACCESS_ERROR);
        return new ResponseEntity<>(errorResponse, forbidden);
    }

    @ExceptionHandler(value = {AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex) {
        log.error(String.format("Authentication Credentials Not FoundException: %s", ex.getMessage()), ex);
        final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;

        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(unauthorized.value()),
                ErrorConstants.NOT_FOUND_CREDENTIALS.getErrorMessage(), ex.getMessage(), LocalDateTime.now().toString(),
                ErrorType.AUTHORIZATION_ERROR);
        return new ResponseEntity<>(errorResponse, unauthorized);
    }
}
