package com.github.ingaelsta.weatherinfo.favorites.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FavoriteLocationTest {
    private Validator validator;
    private final Double latitude = 52.1;
    private final Double longitude = -0.78;

    private final String locationName = "A nice place to take a walk";

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void WhenAllDataValid_thenValidationSucceeds(){
        FavoriteLocation favoriteLocation = new FavoriteLocation(latitude, longitude, locationName);

        Set<ConstraintViolation<FavoriteLocation>> violations = validator.validate(favoriteLocation);
        assertTrue(violations.isEmpty());
    }

    @Test
    void WhenAnyVariableIsNull_thenValidationFails(){
        FavoriteLocation favoriteLocation = new FavoriteLocation(null, longitude,locationName);
        Set<ConstraintViolation<FavoriteLocation>> violations = validator.validate(favoriteLocation);
        assertFalse(violations.isEmpty());

        favoriteLocation = new FavoriteLocation(latitude, null, locationName);
        violations = validator.validate(favoriteLocation);
        assertFalse(violations.isEmpty());

        favoriteLocation = new FavoriteLocation(latitude, longitude, null);
        violations = validator.validate(favoriteLocation);
        assertFalse(violations.isEmpty());

    }

    @Test
    void WhenAnyLocationIsEmpty_thenValidationFails(){
        FavoriteLocation favoriteLocation = new FavoriteLocation(latitude, longitude, "");
        Set<ConstraintViolation<FavoriteLocation>> violations = validator.validate(favoriteLocation);
        assertFalse(violations.isEmpty());

    }

    @Test
    void WhenInvalidCoordinatesPassed_thenValidationFails() {
        Double longitude = 1900.9; //larger than max
        FavoriteLocation favoriteLocation = new FavoriteLocation(latitude, longitude, locationName);
        Set<ConstraintViolation<FavoriteLocation>> violations = validator.validate(favoriteLocation);
        assertFalse(violations.isEmpty());

        Double latitude = 100.0; //larger than max
        favoriteLocation = new FavoriteLocation(latitude, longitude, locationName);
        violations = validator.validate(favoriteLocation);
        assertFalse(violations.isEmpty());
    }

    @Test
    void ItIsPossibleToRetrieveAndResetValues() {
        FavoriteLocation favoriteLocation = new FavoriteLocation(latitude, longitude, locationName);

        assertEquals(favoriteLocation.getLatitude(), latitude);
        assertEquals(favoriteLocation.getLongitude(),longitude);
        assertEquals(favoriteLocation.getLocationName(), locationName);

        favoriteLocation.setLatitude(-63.81);
        favoriteLocation.setLongitude(-57.69);
        favoriteLocation.setLocationName("Watching Antarctic birds");
    }

}