package com.dproduction.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.dproduction.service.profiling.MessageService;

@SpringBootTest
@ActiveProfiles("dev") // Activate the "test" profile for this test
public class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Test
    public void testGetMessage() {
        String expectedMessage = "This is the dev environment.";
        String actualMessage = messageService.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}