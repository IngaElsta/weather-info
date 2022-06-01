package com.github.ingaelsta.weatherinfo.weather.service;

import com.github.ingaelsta.weatherinfo.commons.Conversion;
import com.github.ingaelsta.weatherinfo.commons.model.Location;
import com.github.ingaelsta.weatherinfo.weather.model.Alert;
import com.github.ingaelsta.weatherinfo.weather.model.Temperature;
import com.github.ingaelsta.weatherinfo.weather.model.WeatherConditions;
import com.github.ingaelsta.weatherinfo.weather.model.Wind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WeatherCachingIntegrationTest {
    @Autowired
    CacheManager cacheManager;
    @Autowired
    WeatherService weatherServiceWrappedMock;
    private static final WeatherService weatherServiceMock = Mockito.mock(WeatherService.class);

    @Configuration
    @EnableCaching
    static class Config {

        @Bean
        WeatherService weatherServiceMock() {
            return weatherServiceMock;
        }

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("weather");
        }
    }

    private final LocalDate date = Conversion.convertDate(1643536800).toLocalDate();
    private final Location location1 = new Location(12.34, 56.67);
    private final Location location2 = new Location(-12.34, -56.78);

    private Map<LocalDate, WeatherConditions> weatherConditionsMap, weatherConditionsMapWithAlerts;

    @BeforeEach
    void setUp () {
        //initializing data variables
        {
            Temperature temperature = new Temperature(1.64, 1.09, -0.16, -0.94);
            Wind wind = new Wind(8.23, 17.56, "S");
            List<String> weatherDescriptions = new ArrayList<>();
            weatherDescriptions.add("rain and snow");


            List<Alert> alerts = new ArrayList<>();

            Alert alert1 = new Alert("Yellow Flooding Warning",
                    date.atStartOfDay().plusHours(3),
                    date.atStartOfDay().plusHours(7));
            alerts.add(alert1);

            Alert alert2 = new Alert("Red Wind Warning",
                    date.atStartOfDay().plusHours(0),
                    date.atStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59));
            alerts.add(alert2);

            weatherConditionsMap = new HashMap<>();
            weatherConditionsMap.put(date, new WeatherConditions(
                    date, weatherDescriptions, temperature, wind, new ArrayList<>()));

            weatherConditionsMapWithAlerts = new HashMap<>();
            weatherConditionsMapWithAlerts.put(date, new WeatherConditions(
                    date, weatherDescriptions, temperature, wind, alerts));
        }
        reset(weatherServiceMock);
        when(weatherServiceMock.getWeather(location1))
                .thenReturn(weatherConditionsMap);
        when(weatherServiceMock.getWeather(location2))
                .thenReturn(weatherConditionsMapWithAlerts);
        cacheManager.getCache("weather").clear();
    }

    @Test
    public void When_getWeatherIsCalled_Then_methodInvocationShouldBeCached() {
        //calls once for the same parameters
        Map<LocalDate, WeatherConditions> result1 = weatherServiceWrappedMock.getWeather(location1);
        assertEquals(result1, weatherConditionsMap);
        Map<LocalDate, WeatherConditions> result2 = weatherServiceWrappedMock.getWeather(location1);
        assertEquals(result2, weatherConditionsMap);
        verify(weatherServiceMock, times(1)).getWeather(location1);
        assertNotNull(cacheManager.getCache("weather").get(location1));

        //calls again for new parameters
        Map<LocalDate, WeatherConditions> result3 = weatherServiceWrappedMock.getWeather(location2);
        assertEquals(result3, weatherConditionsMapWithAlerts);
        verify(weatherServiceMock, times(1)).getWeather(location2);
        assertNotNull(cacheManager.getCache("weather").get(location2));
    }

    @Test
    public void When_evictWeatherCacheValueCalledOnCachedValue_Then_theEvictedValueCantBeFoundInCacheLater () {
        weatherServiceWrappedMock.getWeather(location1);
        weatherServiceWrappedMock.getWeather(location2);
        weatherServiceWrappedMock.evictWeatherCacheValue(location2);
        assertNotNull(cacheManager.getCache("weather").get(location1));
        assertNull(cacheManager.getCache("weather").get(location2));
    }

    @Test
    public void When_valueCachedAndEvictAllCacheCalled_Then_thePreviouslyStoredValueCantBeFoundInCacheLater () {
        weatherServiceWrappedMock.getWeather(location1);
        weatherServiceWrappedMock.getWeather(location2);
        weatherServiceWrappedMock.evictAllWeatherCache();
        assertNull(cacheManager.getCache("weather").get(location1));
        assertNull(cacheManager.getCache("weather").get(location2));
    }
}