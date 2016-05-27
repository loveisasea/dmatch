package com.fym.core.util.jackson;


import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class ObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

    public ObjectMapper() {
        super();
        this.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        this.setDateFormat(dateFormat);
    }

}
