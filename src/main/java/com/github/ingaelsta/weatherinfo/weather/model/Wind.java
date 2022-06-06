package com.github.ingaelsta.weatherinfo.weather.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Wind {
    @NotNull
    @Min(value = 0)
    private Double speed, gusts;

    private String direction;

    public static String degreesToDirection (int degrees) {
        if (degrees >= 0 && degrees <= 22) { return "N"; }
        if (degrees >= 23 && degrees <= 67) { return "NE"; }
        if (degrees >= 68 && degrees <= 112) { return "E"; }
        if (degrees >= 113 && degrees <= 157) { return "SE"; }
        if (degrees >= 158 && degrees <= 202) { return "S"; }
        if (degrees >= 203 && degrees <= 247) { return "SW"; }
        if (degrees >= 248 && degrees <= 292) { return "W"; }
        if (degrees >= 293 && degrees <= 337) { return "NW"; }
        if (degrees >= 338 && degrees <= 360) { return "N"; }
        return null;
    }
}
