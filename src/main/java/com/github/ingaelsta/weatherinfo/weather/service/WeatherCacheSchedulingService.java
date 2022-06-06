package com.github.ingaelsta.weatherinfo.weather.service;

import com.github.ingaelsta.weatherinfo.commons.model.Location;
import com.github.ingaelsta.weatherinfo.favorites.entity.FavoriteLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class WeatherCacheSchedulingService {

    private final WeatherService weatherService;
    private final WebClient webClient;

    private String favoriteURI = "http://localhost:8080/api/v1/weather-info/favorites";


    public WeatherCacheSchedulingService(WeatherService weatherService) {
        this.weatherService = weatherService;
        this.webClient = WebClient.create();
    }

    public void setFavoriteURI(String favoriteURI) {
        this.favoriteURI = favoriteURI;
    }

    @Scheduled(cron = "${cron.fetch}")
    public void cacheWeatherForFavoriteLocations () {
        List<FavoriteLocation> allFavoriteLocations = retrieveFavorites();
        System.out.println(allFavoriteLocations);
        allFavoriteLocations
                .forEach(favorite -> {
                    Location location = new Location(favorite.getLatitude(), favorite.getLongitude());
                    weatherService.evictWeatherCacheValue(location);
                    weatherService.getWeather(location);
                });
    }

    private List<FavoriteLocation> retrieveFavorites() {
        List<FavoriteLocation> favoriteList = webClient.get()
                .uri(favoriteURI)
                .retrieve()
                .bodyToFlux(FavoriteLocation.class)
                .collectList()
                .block(Duration.of(3000, ChronoUnit.MILLIS));
        System.out.println(favoriteList);
        return favoriteList;
    }

    @Scheduled(cron = "${cron.evict}")
    public void clearAllCache () {
        weatherService.evictAllWeatherCache();
    }

}
