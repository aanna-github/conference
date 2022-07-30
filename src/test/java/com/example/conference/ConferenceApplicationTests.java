package com.example.conference;

import com.example.conference.constants.TestRequestConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConferenceApplicationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Test
    void contextLoadsConferenceApi() throws Exception {
        ResponseEntity<String> response = restTemplate
                .getForEntity(new URL(String.format("%s%d%s%s%s",
                        TestRequestConstants.LOCAL_HOST, port, contextPath, TestRequestConstants.API_CONFERENCE,
                        TestRequestConstants.CHECK_PATH)).toString(), String.class);
        assertTrue(response.hasBody());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void contextLoadsRoomApi() throws Exception {
        ResponseEntity<String> response = restTemplate
                .getForEntity(new URL(String.format("%s%d%s%s%s", TestRequestConstants.LOCAL_HOST, port, contextPath,
                        TestRequestConstants.API_ROOM, TestRequestConstants.CHECK_PATH)).toString(), String.class);
        assertTrue(response.hasBody());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
