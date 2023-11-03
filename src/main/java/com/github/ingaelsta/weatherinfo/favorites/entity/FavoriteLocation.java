package com.github.ingaelsta.weatherinfo.favorites.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@Entity(name = "favorite")
@Table(name = "FAVORITE")
public class FavoriteLocation {
    @Id
    @SequenceGenerator(
            name = "favorite_sequence",
            sequenceName = "favorite_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator = "favorite_sequence"
    )
    private Long id;
    @NotNull(message = "Latitude value should not be empty")
    @Min(value = -90, message = "Latitude must be greater than or equal to -90 (90 South)")
    @Max(value = 90, message = "Latitude must be less than or equal to 90 (90 North)")
    private Double latitude;

    @NotNull(message = "Longitude value should not be empty")
    @Min(value = -180, message = "Longitude must be greater than or equal to -180 (180 East)")
    @Max(value = 180, message = "Longitude must be less than or equal to 180 (180 West)")
    private Double longitude;

    @NotBlank
    private String locationName;

    public FavoriteLocation(Double latitude, Double longitude, String locationName) {
        this();
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
    }
}
