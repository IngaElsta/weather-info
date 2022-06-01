package com.github.ingaelsta.weatherinfo.weather.model;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class WeatherConditions {
    @NotNull
    private LocalDate date;

    @NotEmpty
    private List<String> weatherDescriptions; //weather: description

    @NotNull
    private Temperature temperature;

    @NotNull
    private Wind wind;

    private List<Alert> alerts;

}
