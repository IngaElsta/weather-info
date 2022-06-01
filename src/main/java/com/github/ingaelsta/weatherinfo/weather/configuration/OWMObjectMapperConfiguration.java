package com.github.ingaelsta.weatherinfo.weather.configuration;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.ingaelsta.weatherinfo.weather.deserialize.OWMDeserializer;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
public class OWMObjectMapperConfiguration {
    @Autowired
    private final ObjectMapper objectMapper;

    public OWMObjectMapperConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        SimpleModule module = new SimpleModule("OWMDeserializer",
                new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Map.class, new OWMDeserializer());
        objectMapper.registerModule(module);
    }
}
