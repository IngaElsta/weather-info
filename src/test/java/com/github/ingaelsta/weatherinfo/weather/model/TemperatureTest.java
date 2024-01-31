package com.github.ingaelsta.weatherinfo.weather.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

public class TemperatureTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void WhenAcceptableValuesPassed_thenValidationSucceeds(){
        Double  morning = -3.0;
        Double  day = 2.6;
        Double  evening = 0.5;
        Double  night = -5.7;

        Temperature temperature = new Temperature(morning, day, evening, night);
        Set<ConstraintViolation<Temperature>> violations = validator.validate(temperature);
        assertTrue(violations.isEmpty());
    }

    @Test
    void WhenNullValuesPassed_thenValidationFails(){
        Double  morning = -5.1;
        Double  night = -7.4;

        Temperature temperature = new Temperature(morning, null, null, night);
        Set<ConstraintViolation<Temperature>> violations = validator.validate(temperature);
        assertFalse(violations.isEmpty());
    }


    @Test
    void ItIsPossibleToRetrieveAndResetAnyValue(){
        Double morning = 1.5;
        Double day = 10.4;
        Double evening = 7.1;
        Double night = -2.05;
        Temperature temperature = new Temperature(morning, day, evening, night);
        assertEquals(temperature.getMorn(), morning);
        assertEquals(temperature.getDay(), day);
        assertEquals(temperature.getEve(), evening);
        assertEquals(temperature.getNight(), night);

        temperature.setMorn(0.0);
        temperature.setDay(4.5);
        temperature.setEve(2.1);
        temperature.setNight(-1.2);
    }
}
