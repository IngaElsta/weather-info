package com.github.ingaelsta.weatherinfo.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.ingaelsta.weatherinfo.commons.model.Location;
import com.github.ingaelsta.weatherinfo.weather.configuration.OWMConfiguration;
import com.github.ingaelsta.weatherinfo.weather.configuration.OWMObjectMapperConfiguration;
import com.github.ingaelsta.weatherinfo.weather.model.WeatherConditions;
import com.github.ingaelsta.weatherinfo.weather.exception.OWMDataException;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Slf4j
@Component
public class OWMDataService implements WeatherDataService {
    private final ObjectMapper objectMapper;
    private final OWMConfiguration owmConfiguration;
    private final WebClient webClient;
    @Getter
    private final CircuitBreaker owmCircuitBreaker;
    private final long timeout;

    public OWMDataService(OWMConfiguration owmConfiguration,
                          OWMObjectMapperConfiguration OWMobjectMapperConfiguration
    ) {
        this.owmConfiguration = owmConfiguration;
        this.objectMapper = OWMobjectMapperConfiguration.getObjectMapper();
        this.owmCircuitBreaker = CircuitBreaker.ofDefaults("OWMCircuitBreaker");
        this.webClient = WebClient.create();
        this.timeout = Long.parseLong(owmConfiguration.getTimeout());
    }

    public Map<LocalDate, WeatherConditions> retrieveWeather (Location location) {
        //todo: properly configure the circuit breaker
        URI owmURI = new UriTemplate(owmConfiguration.getOneApiUrl())
                .expand(location.getLatitude(), location.getLongitude(),
                        owmConfiguration.getAuthToken());
        String response = getOWMResponse(owmURI);
        return processWeatherData(response, objectMapper);
    }

    private String getOWMResponse(URI owmURI) {
        return owmCircuitBreaker.executeSupplier(() ->
                webClient.get()
                        .uri(owmURI)
                        .retrieve()
                        .onStatus(HttpStatus::isError, result -> {
                            result.toEntity(String.class).subscribe(
                                    error -> log.error("getOWMResponse: Failed to retrieve weather data {}", error)
                            );
                            throw new OWMDataException("Failed to retrieve weather data");
                        })
                        .bodyToMono(String.class)
                        .block(Duration.of(timeout, ChronoUnit.MILLIS)));
    }

    @SuppressWarnings("unchecked")
    private static Map<LocalDate, WeatherConditions> processWeatherData(
            String weatherJson,
            ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(weatherJson, Map.class);
        } catch (JsonProcessingException e) {
            log.error("processWeatherData: Failed to process weather data {}", weatherJson);
            throw new OWMDataException("Failed to process weather data received from OWM");
        }
    }

}
