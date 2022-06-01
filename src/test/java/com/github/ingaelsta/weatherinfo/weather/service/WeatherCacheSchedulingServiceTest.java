package com.github.ingaelsta.weatherinfo.weather.service;

import com.github.ingaelsta.weatherinfo.commons.Conversion;
import com.github.ingaelsta.weatherinfo.commons.model.Location;
import com.github.ingaelsta.weatherinfo.favorites.entity.FavoriteLocation;
import com.github.ingaelsta.weatherinfo.favorites.service.FavoriteLocationService;
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

import static org.mockito.Mockito.*;

class WeatherCacheSchedulingServiceTest {
    private final WeatherService weatherServiceMock = mock(WeatherService.class);
    private final FavoriteLocationService favoriteLocationServiceMock = mock(FavoriteLocationService.class);

    private static WeatherCacheSchedulingService WeatherCacheSchedulingService;

    private final Location location1 = new Location(12.34, 56.67);
    private final Location location2 = new Location(-12.34, -56.78);

    private final FavoriteLocation favoriteLocation1 =
            new FavoriteLocation(12.34, 56.67, "favorite 1");
    private final FavoriteLocation favoriteLocation2 =
            new FavoriteLocation(-12.34, -56.78,"favorite 2");
    private final LocalDate date = Conversion.convertDate(1643536800).toLocalDate();
    private Map<LocalDate, WeatherConditions> weatherConditionsMap, weatherConditionsMapWithAlerts;

    @BeforeEach
    void setUp() {
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
        WeatherCacheSchedulingService = new WeatherCacheSchedulingService(weatherServiceMock, favoriteLocationServiceMock);
    }

    @Test
    public void When_twoFavoriteLocationsStored_Then_GetWeatherAndEvictWeatherCacheValueIsCalledTwiceEach() {
        List<FavoriteLocation> favoriteLocations = new ArrayList<>();
        favoriteLocations.add(favoriteLocation1);
        favoriteLocations.add(favoriteLocation2);

        when(weatherServiceMock.getWeather(location1))
                .thenReturn(weatherConditionsMap);
        when(weatherServiceMock.getWeather(location2))
                .thenReturn(weatherConditionsMapWithAlerts);

        when(favoriteLocationServiceMock.getAllFavorites())
                .thenReturn(favoriteLocations);

        WeatherCacheSchedulingService.cacheWeatherForFavoriteLocations();

        verify(weatherServiceMock, times(2)).getWeather(any());
        verify(weatherServiceMock, times(2)).evictWeatherCacheValue(any());
    }

    @Test
    public void When_clearAllCacheCalled_Then_evictAllWeatherCacheIsCalled() {
        WeatherCacheSchedulingService.clearAllCache();

        verify(weatherServiceMock).evictAllWeatherCache();
    }

}