package com.github.ingaelsta.weatherinfo.weather.exception;

public abstract class WeatherDataException extends RuntimeException {
    public WeatherDataException(String message) {
        super(message);
    }
}
