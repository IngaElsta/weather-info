package com.github.ingaelsta.weatherinfo.weather.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class AlertTest {
    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void WhenAllValidParametersPassed_thenValidationSucceeds(){
        String alertType = "Yellow Flooding Warning";
        LocalDateTime alertStart = LocalDateTime.now();
        LocalDateTime alertEnd = LocalDateTime.now().plusHours(1);

        Alert alert = new Alert(alertType, alertStart, alertEnd);
        Set<ConstraintViolation<Alert>> violations = validator.validate(alert);
        assertTrue(violations.isEmpty());
    }

    @Test
    void WhenAnyAlertEndpointNotPassed_thenValidationFails(){
        String alertType = "Yellow Flooding Warning";
        LocalDateTime alertStart = LocalDateTime.now();
        LocalDateTime alertEnd = LocalDateTime.now().plusHours(1);

        Alert alert = new Alert(alertType, alertStart, null);
        Set<ConstraintViolation<Alert>> violations = validator.validate(alert);
        assertFalse(violations.isEmpty());

        alert = new Alert(alertType, null, alertEnd);
        violations = validator.validate(alert);
        assertFalse(violations.isEmpty());
    }

    @Test
    void WhenAlertTypeNotPassed_thenValidationFails(){
        String alertType = "";
        LocalDateTime alertStart = LocalDateTime.now();
        LocalDateTime alertEnd = LocalDateTime.now().plusHours(1);

        Alert alert = new Alert(alertType, alertStart, alertEnd);
        Set<ConstraintViolation<Alert>> violations = validator.validate(alert);
        assertFalse(violations.isEmpty());
    }

    @Test
    void ItIsPossibleToRetrieveAndResetAnyValue() {
        String alertType = "Yellow Flooding Warning";
        LocalDateTime alertStart = LocalDateTime.now();
        LocalDateTime alertEnd = LocalDateTime.now().plusHours(1);

        Alert alert = new Alert(alertType, alertStart, alertEnd);

        assertEquals(alert.getAlertType(), alertType);
        assertEquals(alert.getAlertStart(), alertStart);
        assertEquals(alert.getAlertEnd(),alertEnd);

        alert.setAlertType("Red Snow Warning");
        alert.setAlertStart(LocalDateTime.now().minusDays(1));
        alert.setAlertEnd(LocalDateTime.now().plusDays(2));
    }
}
