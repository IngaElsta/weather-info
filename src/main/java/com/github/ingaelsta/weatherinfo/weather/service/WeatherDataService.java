package com.github.ingaelsta.weatherinfo.weather.service;

import com.github.ingaelsta.weatherinfo.commons.model.Location;
import com.github.ingaelsta.weatherinfo.weather.model.WeatherConditions;

import java.time.LocalDate;
import java.util.Map;

public interface WeatherDataService {
    Map<LocalDate, WeatherConditions> retrieveWeather (Location location);
}
