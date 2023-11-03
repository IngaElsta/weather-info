package com.github.ingaelsta.weatherinfo.commons.configuration;

import com.github.ingaelsta.weatherinfo.commons.response.ErrorResponse;
import com.github.ingaelsta.weatherinfo.weather.exception.WeatherDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(Exception e) {
        List<String> errors = Arrays.asList(e.getMessage()
                .replace("getWeather.", "")
                .split(", "));

        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.BAD_REQUEST, errors),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.BAD_REQUEST, errors),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleOtherExpectedException(Exception e) {
        List<String> errors = new ArrayList<>();
        errors.add (e.getMessage());

        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.BAD_REQUEST, errors),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WeatherDataException.class)
    public ResponseEntity<ErrorResponse> handleWeatherDataException(Exception e) {
        List<String> errors = new ArrayList<>();
        errors.add (e.getMessage());

        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, errors),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // fallback method
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherException(Exception e) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();

        List<String> errors = new ArrayList<>();
        errors.add (e.getMessage());

        log.error(stackTrace);
        log.error(String.valueOf(errors));

        List<String> responseErrors = new ArrayList<>();
        responseErrors.add("A server error has occurred");

        return new ResponseEntity<>(
                new ErrorResponse(status, responseErrors),
                status
        );
    }
}