package com.example.demo;

//import org.apache.logging.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    private static final Logger LOGGER  = LoggerFactory.getLogger(MyService.class);

    public void logInfo(String message) {
        LOGGER.info(message);
    }

    public void logDebug(String message) {
        LOGGER.debug(message);
    }

    public void logError(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
    }

    public void logWarn(String message) {
        LOGGER.warn(message);
    }
    
}
