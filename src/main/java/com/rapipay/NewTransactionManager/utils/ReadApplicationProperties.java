package com.rapipay.NewTransactionManager.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Configuration
@PropertySource("classpath:application.properties")
public class ReadApplicationProperties {
    private static final Logger log = LogManager.getLogger(ReadApplicationProperties.class);

    @Autowired
    private Environment env;
    public ReadApplicationProperties() {
    }

    public String getPropertyData1(String readKey) {
        try {
            return env.getProperty(readKey);
        } catch (Exception e) {
            log.error("Exception >>>>>>>>>>>>>>>>>>>>>>>>>>>> {} ", e.getStackTrace()[3]);
            log.error("ReadApplicationProperties:getPropertyData1(): {} ", e.getMessage());
            return "";
        }
    }
}

