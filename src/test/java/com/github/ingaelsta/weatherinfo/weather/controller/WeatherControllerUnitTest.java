package com.github.ingaelsta.weatherinfo.weather.controller;

import com.github.ingaelsta.weatherinfo.commons.Conversion;
import com.github.ingaelsta.weatherinfo.commons.model.Location;
import com.github.ingaelsta.weatherinfo.weather.exception.WeatherDataException;
import com.github.ingaelsta.weatherinfo.weather.model.Temperature;
import com.github.ingaelsta.weatherinfo.weather.model.WeatherConditions;
import com.github.ingaelsta.weatherinfo.weather.model.Wind;
import com.github.ingaelsta.weatherinfo.weather.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class WeatherControllerUnitTest {
    private final WeatherService weatherServiceMock = Mockito.mock(WeatherService .class);

    private WeatherController weatherControllerMock;

    private final Double latitude = 55.87;
    private final Double longitude = 26.52;
    private final Location location = new Location(latitude, longitude);
    private final LocalDate date = Conversion.convertDate(1643536800).toLocalDate();

    @BeforeEach
    public void setup () {
        weatherControllerMock = new WeatherController(weatherServiceMock) ;
    }

    @Test
    public void When_WeatherDataSuccessfullyRetrieved_Then_getWeatherReturnsData() {
        Temperature temperature = new Temperature(1.64, 1.09, -0.16, -0.94);
        Wind wind = new Wind(8.23, 17.56, "S");
        List<String> weatherDescriptions = new ArrayList<>();
        weatherDescriptions.add("rain and snow");

        Map<LocalDate, WeatherConditions> expected = new HashMap<>();
        expected.put(date, new WeatherConditions(
                date, weatherDescriptions, temperature, wind, new ArrayList<>()));

        when(weatherServiceMock.getWeather(location))
                .thenReturn(expected);

        Map<LocalDate, WeatherConditions> result = weatherControllerMock.getWeather(latitude, longitude);

        assertEquals(expected, result);
    }

    @Test
    public void When_WeatherDataRetrievalUnsuccessful_Then_getWeatherReturnsWeatherDataException() {
        when(weatherServiceMock.getWeather(new Location(55.87, 26.52)))
                .thenThrow(new WeatherDataException("placeholder") {});

        assertThrows(WeatherDataException.class, () -> weatherControllerMock.getWeather(55.87, 26.52));
    }

}