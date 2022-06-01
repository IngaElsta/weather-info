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
        if (degrees >= 23 && degrees <= 67) { return "NE"; }
        else if (degrees >= 68 && degrees <= 112) { return "E"; }
        else if (degrees >= 113 && degrees <= 157) { return "SE"; }
        else if (degrees >= 158 && degrees <= 202) { return "S"; }
        else if (degrees >= 203 && degrees <= 247) { return "SW"; }
        else if (degrees >= 248 && degrees <= 292) { return "W"; }
        else if (degrees >= 293 && degrees <= 337) { return "NW"; }
        else if ((degrees >= 338 && degrees <= 360) ||
                (degrees >= 0 && degrees <= 22)) { return "N"; }
        else { return null; }
    }
}
