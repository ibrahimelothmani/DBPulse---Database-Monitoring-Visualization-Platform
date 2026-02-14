package com.ibrahim.DBPulse.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson configuration for JSON serialization/deserialization.
 * Configures how Java objects are converted to/from JSON.
 */
@Configuration
public class JacksonConfig {

    /**
     * Custom ObjectMapper configuration for better JSON handling.
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        // Register JavaTimeModule for Java 8 Date/Time API support
        objectMapper.registerModule(new JavaTimeModule());

        // Don't write dates as timestamps, use ISO-8601 format instead
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Ignore unknown properties during deserialization
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Don't include null values in JSON output
        // objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Pretty print JSON in development (disable in production for performance)
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        return objectMapper;
    }
}
