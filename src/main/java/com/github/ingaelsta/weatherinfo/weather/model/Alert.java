package com.github.ingaelsta.weatherinfo.weather.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Alert {
    @NotEmpty
    private String alertType; //alerts: event.

    @NotNull
    private LocalDateTime alertStart, alertEnd; //alerts: start, end
}
