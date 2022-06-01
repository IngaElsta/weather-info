package com.github.ingaelsta.weatherinfo.weather.service;

import com.github.ingaelsta.weatherinfo.commons.Conversion;
import com.github.ingaelsta.weatherinfo.commons.model.Location;
import com.github.ingaelsta.weatherinfo.weather.exception.WeatherDataException;
import com.github.ingaelsta.weatherinfo.weather.model.Alert;
import com.github.ingaelsta.weatherinfo.weather.model.Temperature;
import com.github.ingaelsta.weatherinfo.weather.model.WeatherConditions;
import com.github.ingaelsta.weatherinfo.weather.model.Wind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

    private final WeatherDataService weatherDataServiceMock = mock(WeatherDataService.class);

    private WeatherService weatherService;

    private final Double latitude = 55.87;
    private final Double longitude = 26.52;
    private final Location location = new Location(latitude, longitude);
    private final LocalDate date = Conversion.convertDate(1643536800).toLocalDate();

    private Map<LocalDate, WeatherConditions> weatherConditionsMap;

    @BeforeEach
    void setUp() {
        weatherService = new WeatherService(weatherDataServiceMock);

        {
            Temperature temperature = new Temperature(1.64, 1.09, -0.16, -0.94);
            Wind wind = new Wind(8.23, 17.56, "S");
            List<String> weatherDescriptions = new ArrayList<>();
            weatherDescriptions.add("rain and snow");

            List<Alert> alerts = new ArrayList<>();

            Alert alert1 = new Alert("Yellow Flooding Warning",
                    date.atStartOfDay().plusHours(3),
                    date.atStartOfDay().plusHours(7));
            Alert alert2 = new Alert("Red Wind Warning",
                    date.atStartOfDay().plusHours(0),
                    date.atStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59));

            alerts.add(alert1);
            alerts.add(alert2);

            weatherConditionsMap = new HashMap<>();
            weatherConditionsMap.put(date, new WeatherConditions(
                    date, weatherDescriptions, temperature, wind, alerts));
        }

    }

    //getWeather
    @Test
    public void When_weatherDataServiceReturnsData_then_getWeatherReturnsWeatherData() {

        when(weatherDataServiceMock.retrieveWeather(location))
                .thenReturn(weatherConditionsMap);

        Map<LocalDate, WeatherConditions> result = weatherService.getWeather(location);

        assertEquals(weatherConditionsMap, result);
    }

    @Test
    public void When_weatherDataServiceThrowsWeatherDataException_thenThrowsWeatherDataException() {
        Location location = new Location(55.87, 26.52);

        when(weatherDataServiceMock.retrieveWeather(location))
                .thenThrow(new WeatherDataException("Failed") {});

        assertThrows(WeatherDataException.class, () -> weatherService.getWeather(location));
    }

}
