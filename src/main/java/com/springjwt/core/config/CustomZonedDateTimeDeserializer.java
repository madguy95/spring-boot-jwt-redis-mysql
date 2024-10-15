package com.springjwt.core.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CustomZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
    private final DateTimeFormatter formatter;

    public CustomZonedDateTimeDeserializer(DateTimeFormatter formatter) {
        super();
        this.formatter = formatter;
    }
    @Override
    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String date = p.getText();
        return ZonedDateTime.parse(date, formatter);
    }
}

