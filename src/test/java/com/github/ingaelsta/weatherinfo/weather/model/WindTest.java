package com.github.ingaelsta.weatherinfo.weather.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WindTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void WhenWindSpeedValuesAreAcceptable_thenValidationSucceeds(){
        Double speed = 5.4;
        Double gusts = 7.2;
        String direction = "NW";

        Wind wind = new Wind(speed, gusts, direction);
        Set<ConstraintViolation<Wind>> violations = validator.validate(wind);
        assertTrue(violations.isEmpty());
    }

    @Test
    void WhenWindDirectionIsEmpty_thenValidationSucceeds(){
        Double speed = 5.0;
        Double gusts = 7.2;

        Wind wind = new Wind(speed, gusts, null);
        Set<ConstraintViolation<Wind>> violations = validator.validate(wind);
        assertTrue(violations.isEmpty());
    }

    @Test
    void WhenGustSpeedNotPassed_thenValidationFails(){
        Double speed = 5.8;

        Wind wind = new Wind(speed, null, "NW");
        Set<ConstraintViolation<Wind>> violations = validator.validate(wind);
        assertFalse(violations.isEmpty());
    }

    @Test
    void WhenWindSpeedNotPassed_thenValidationFails(){
        Double gusts = 7.2;
        String direction = "NW";

        Wind wind = new Wind(null, gusts, direction);
        Set<ConstraintViolation<Wind>> violations = validator.validate(wind);
        assertFalse(violations.isEmpty());
    }

    @Test
    void WhenWindDirectionNotPassed_thenValidationFails(){
        Double speed = 5.0;
        Double gusts = 7.2;

        Wind wind = new Wind(speed, gusts, "");
        Set<ConstraintViolation<Wind>> violations = validator.validate(wind);
        assertTrue(violations.isEmpty());
    }

    @Test
    void WhenNegativeWindSpeedPassed_thenValidationFails() {
        Double speed = -1.9;
        Double gusts = 7.2;
        String direction = "NW";

        Wind wind = new Wind(speed, gusts, direction);
        Set<ConstraintViolation<Wind>> violations = validator.validate(wind);
        assertFalse(violations.isEmpty());
    }

    @Test
    void WhenNegativeGustSpeedPassed_thenValidationFails() {
        Double speed = -3.5;
        Double gusts = 1100.0;
        String direction = "NW";
        Wind wind = new Wind(speed, gusts, direction);
        Set<ConstraintViolation<Wind>> violations = validator.validate(wind);
        assertFalse(violations.isEmpty());
    }

    @Test
    void ItIsPossibleToRetrieveOrResetAnyValue() {
        Wind wind = new Wind(2.5, 6.0, "S");
        assertEquals(wind.getSpeed(), Double.valueOf(2.5));
        assertEquals(wind.getGusts(), Double.valueOf(6.0));
        assertEquals(wind.getDirection(), "S");

        wind.setSpeed(16.9);
        wind.setGusts(23.1);
        wind.setDirection("W");
    }

    @Test
    void WindDegreesAreSuccessfullyConvertedToMainDirections(){

        assertEquals(Wind.degreesToDirection(60), ("NE"));
        assertEquals(Wind.degreesToDirection(80), ("E"));
        assertEquals(Wind.degreesToDirection(115), ("SE"));
        assertEquals(Wind.degreesToDirection(202), ("S"));
        assertEquals(Wind.degreesToDirection(203), ("SW"));
        assertEquals(Wind.degreesToDirection(260), ("W"));
        assertEquals(Wind.degreesToDirection(300), ("NW"));
        assertEquals(Wind.degreesToDirection(0), ("N"));
        assertNull(Wind.degreesToDirection(-400));
    }

}
