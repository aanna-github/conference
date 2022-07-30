package com.example.conference.controller;

import com.example.conference.controller.dto.error.ErrorResponseDto;
import com.example.conference.utility.enumeration.ErrorConstants;
import com.example.conference.utility.enumeration.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ApiIgnore
@RestController
@Slf4j
public class RestErrorController extends AbstractErrorController {

    private static final String ERROR_PATH = "/error";

    @Autowired
    public RestErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<ErrorResponseDto> handleErrors(HttpServletRequest request) {
        String errorMessage = "An error occurred during the request of URN or URN is not found. URN:"
                + request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        log.error(errorMessage);

        HttpStatus status = getStatus(request);
        ErrorResponseDto errorResponseDto;

        if (HttpStatus.UNAUTHORIZED.equals(status)) {
            errorResponseDto = ErrorResponseDto.builder()
                    .code(String.valueOf(status.value()))
                    .title(ErrorConstants.UNAUTHORIZED_ERROR_TITLE.getErrorMessage())
                    .message("The user doesn't have access for th endpoint")
                    .timeStamp(LocalDateTime.now().toString())
                    .type(ErrorType.AUTHENTICATION_ERROR)
                    .build();

        } else {
            errorResponseDto = ErrorResponseDto.builder()
                    .code(String.valueOf(status.value()))
                    .title(ErrorConstants.INVALID_REQUEST_ERROR_TITLE.getErrorMessage())
                    .message(errorMessage)
                    .timeStamp(LocalDateTime.now().toString())
                    .type(ErrorType.INVALID_REQUEST_ERROR)
                    .build();
        }

        return ResponseEntity.status(status).body(errorResponseDto);
    }
}