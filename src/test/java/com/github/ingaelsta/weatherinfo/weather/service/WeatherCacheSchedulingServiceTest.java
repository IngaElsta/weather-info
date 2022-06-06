package com.github.ingaelsta.weatherinfo.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ingaelsta.weatherinfo.commons.Conversion;
import com.github.ingaelsta.weatherinfo.commons.model.Location;
import com.github.ingaelsta.weatherinfo.favorites.entity.FavoriteLocation;
import com.github.ingaelsta.weatherinfo.weather.model.Alert;
import com.github.ingaelsta.weatherinfo.weather.model.Temperature;
import com.github.ingaelsta.weatherinfo.weather.model.WeatherConditions;
import com.github.ingaelsta.weatherinfo.weather.model.Wind;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@TestPropertySource(locations="classpath:test.properties") //doesn't work with yaml, might need a workaround
class WeatherCacheSchedulingServiceTest {
    private final WeatherService weatherServiceMock = mock(WeatherService.class);

    public static MockWebServer mockBackEnd;

    private static WeatherCacheSchedulingService weatherCacheSchedulingService;

    private final Location location1 = new Location(12.34, 56.67);
    private final Location location2 = new Location(-12.34, -56.78);

    private final FavoriteLocation favoriteLocation1 =
            new FavoriteLocation(12.34, 56.67, "favorite 1");
    private final FavoriteLocation favoriteLocation2 =
            new FavoriteLocation(-12.34, -56.78,"favorite 2");
    private final LocalDate date = Conversion.convertDate(1643536800).toLocalDate();
    private Map<LocalDate, WeatherConditions> weatherConditionsMap, weatherConditionsMapWithAlerts;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(8086);

    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void setup() {
        //Initializing data variables
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
                    date, weatherDescriptions, temperature, wind, new ArrayList<>()));


            weatherConditionsMapWithAlerts = new HashMap<>();
            weatherConditionsMapWithAlerts.put(date, new WeatherConditions(
                    date, weatherDescriptions, temperature, wind, alerts));

            favoriteLocation1.setId(1L);
            favoriteLocation2.setId(2L);
        }
        weatherCacheSchedulingService = new WeatherCacheSchedulingService(weatherServiceMock);
        weatherCacheSchedulingService.setFavoriteURI("http://localhost:8086/api/v1/weather-info/favorites");
    }

    @Test
    public void When_twoFavoriteLocationsStored_Then_GetWeatherAndEvictWeatherCacheValueIsCalledTwiceEach()
            throws JsonProcessingException {
        List<FavoriteLocation> favoriteLocations = new ArrayList<>();
        favoriteLocations.add(favoriteLocation1);
        favoriteLocations.add(favoriteLocation2);
        when(weatherServiceMock.getWeather(location1))
                .thenReturn(weatherConditionsMap);
        when(weatherServiceMock.getWeather(location2))
                .thenReturn(weatherConditionsMapWithAlerts);

        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(favoriteLocations))
                .addHeader("Content-Type", "application/json"));

        weatherCacheSchedulingService.cacheWeatherForFavoriteLocations();

        verify(weatherServiceMock, times(2)).getWeather(any());
        verify(weatherServiceMock, times(2)).evictWeatherCacheValue(any());
    }

    @Test
    public void When_clearAllCacheCalled_Then_evictAllWeatherCacheIsCalled() {
        weatherCacheSchedulingService.clearAllCache();

        verify(weatherServiceMock).evictAllWeatherCache();
    }

}