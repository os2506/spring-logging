package com.dproduction.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ErrorHandlingIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testInvalidRequest() {
        // Simulate an invalid request, e.g., missing parameters
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/users/load/", String.class);

        // Verify that the response has the expected error status code (e.g., 400 Bad Request)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testResourceNotFound() {
        // Simulate a request for a resource that does not exist
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/non-existent-resource", String.class);

        // Verify that the response has the expected error status code (e.g., 404 Not Found)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Add more error scenarios as needed
}
