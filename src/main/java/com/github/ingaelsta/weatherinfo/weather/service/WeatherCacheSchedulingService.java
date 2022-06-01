package com.github.ingaelsta.weatherinfo.weather.service;

import com.github.ingaelsta.weatherinfo.commons.model.Location;
import com.github.ingaelsta.weatherinfo.favorites.entity.FavoriteLocation;
import com.github.ingaelsta.weatherinfo.favorites.service.FavoriteLocationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherCacheSchedulingService {

    private final WeatherService weatherService;

    //todo: might be better to call favorite location controller API instead of service and make those functionalities independent
    private final FavoriteLocationService favoriteLocationService;


    public WeatherCacheSchedulingService(WeatherService weatherService, FavoriteLocationService favoriteLocationService) {
        this.weatherService = weatherService;
        this.favoriteLocationService = favoriteLocationService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void cacheWeatherForFavoriteLocations () {
        List<FavoriteLocation> allFavoriteLocations =  favoriteLocationService.getAllFavorites();
        allFavoriteLocations
                .forEach(favorite -> {
                    Location location = new Location(favorite.getLatitude(), favorite.getLongitude());
                    weatherService.evictWeatherCacheValue(location);
                    weatherService.getWeather(location);
                });
    }

    @Scheduled(cron = "*/2 * * * * *")
    public void clearAllCache () {
        weatherService.evictAllWeatherCache();
    }

}
