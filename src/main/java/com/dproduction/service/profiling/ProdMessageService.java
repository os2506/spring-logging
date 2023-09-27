package com.dproduction.service.profiling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Service
@Profile("prod")
public class ProdMessageService implements MessageService {
	
	private static final Logger logger = LoggerFactory.getLogger(ProdMessageService.class);

    @Value("${prod.message}")
    private String message;

    @Override
    public String getMessage() {
    	logger.info("ProdMessageService",message);
        return message;
    }
}