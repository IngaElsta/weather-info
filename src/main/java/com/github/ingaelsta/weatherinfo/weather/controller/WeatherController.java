package com.github.ingaelsta.weatherinfo.weather.controller;

import com.github.ingaelsta.weatherinfo.commons.model.Location;
import com.github.ingaelsta.weatherinfo.weather.model.WeatherConditions;
import com.github.ingaelsta.weatherinfo.weather.service.WeatherService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDate;
import java.util.Map;

@RestController
@Validated
@RequestMapping("api/v1/weather-info/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public Map<LocalDate, WeatherConditions> getWeather(
            @RequestParam(value = "lat", required = false, defaultValue = "56.95")
            @Min(-90) @Max(90)
            Double latitude,
            @RequestParam (value = "lon", required = false, defaultValue = "24.11")
            @Min(-180) @Max(180)
            Double longitude) {
        return weatherService.getWeather(new Location(latitude, longitude));
    }
}
