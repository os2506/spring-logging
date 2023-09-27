package com.dproduction.service.profiling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Service
@Profile("test")
public class TestMessageService implements MessageService {
	
	private static final Logger logger = LoggerFactory.getLogger(TestMessageService.class);

    @Value("${test.message}")
    private String message;

    @Override
    public String getMessage() {
    	logger.info("TestMessageService",message);
        return message;
    }
}
