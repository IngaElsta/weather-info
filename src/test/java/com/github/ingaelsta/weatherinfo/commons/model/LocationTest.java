package com.github.ingaelsta.weatherinfo.commons.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;

public class LocationTest {

    private final Double latitude = 52.1;
    private final Double longitude = -0.78;

    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void WhenCoordinatesAreAcceptable_thenValidationSucceeds(){
        Location location = new Location(latitude, longitude);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertTrue(violations.isEmpty());

    }

    @Test
    void WhenLongitudeNotPassed_thenValidationFails(){

        Location location = new Location(latitude, null);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertFalse(violations.isEmpty());
    }

    @Test
    void WhenLatitudeNotPassed_thenValidationFails(){

        Location location = new Location(null, longitude);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertFalse(violations.isEmpty());
    }

    @Test
    void WhenInvalidLongitudePassed_thenValidationFails() {
        Double longitude = 1900.9; //larger than max
        Location location = new Location(latitude, longitude);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertFalse(violations.isEmpty());
    }

    @Test
    void WhenInvalidLatitudePassed_thenValidationFails() {
        Double latitude = 100.0; //larger than max
        Location location = new Location(latitude, longitude);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        assertFalse(violations.isEmpty());
    }

    @Test
    void ItIsPossibleToRetrieveAndResetAnyValue() {
        Location location = new Location(latitude, longitude);

        assertEquals(location.getLatitude(), latitude);
        assertEquals(location.getLongitude(), longitude);

        location.setLatitude(50.0);
        location.setLongitude(20.1);
    }

}
