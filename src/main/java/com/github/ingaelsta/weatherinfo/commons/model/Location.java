package com.github.ingaelsta.weatherinfo.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
public class Location {

    @NotNull(message = "Latitude value should not be empty")
    @Min(value = -90, message = "Latitude should not be less than -90 (90 South)")
    @Max(value = 90, message = "Latitude should not be more than 90 (90 North)")
    private Double latitude;

    @NotNull(message = "Longitude value should not be empty")
    @Min(value = -180, message = "Longitude should not be less than -180 (180 East)")
    @Max(value = 180, message = "Longitude should not be less than 180 (180 West)")
    private Double longitude;
}
