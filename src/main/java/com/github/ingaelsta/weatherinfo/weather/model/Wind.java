package com.github.ingaelsta.weatherinfo.weather.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wind {
    private Double speed, gusts;
    private String direction;
}
