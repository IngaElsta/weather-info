package com.github.ingaelsta.weatherinfo.weather.controller;

import com.github.ingaelsta.weatherinfo.commons.Conversion;
import com.github.ingaelsta.weatherinfo.commons.model.Location;
import com.github.ingaelsta.weatherinfo.weather.exception.WeatherDataException;
import com.github.ingaelsta.weatherinfo.weather.model.Temperature;
import com.github.ingaelsta.weatherinfo.weather.model.WeatherConditions;
import com.github.ingaelsta.weatherinfo.weather.model.Wind;
import com.github.ingaelsta.weatherinfo.weather.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
class WeatherControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherServiceMock;
    private static final String URL = "/api/v1/weather-info/weather";
    private static final LocalDate date = Conversion.convertDate(1643536800).toLocalDate();

    private static class TestException extends RuntimeException {
        TestException(String message) {
            super(message);
        }
    }

    //get weather
    @Test
    public void When_NoLocationPassed_Then_getWeatherUsesDefaultValuesAndReturnsData() throws Exception {
        Temperature temperature = new Temperature(1.64, 1.09, -0.16, -0.94);
        Wind wind = new Wind(8.23, 17.56, "S");
        List<String> weatherDescriptions = new ArrayList<>();
        weatherDescriptions.add("rain and snow");

        Map<LocalDate, WeatherConditions> expected = new HashMap<>();
        expected.put(date, new WeatherConditions(
                date, weatherDescriptions, temperature, wind, new ArrayList<>()));

        when(weatherServiceMock.getWeather(new Location(56.95, 24.11)))
                .thenReturn(expected);

        this.mockMvc
                .perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("rain and snow")));
    }

    @Test
    public void When_ValidLocation_Then_getWeatherUsesPassedValuesAndReturnsData() throws Exception {
        Temperature temperature = new Temperature(1.64, 1.09, -0.16, -0.94);
        Wind wind = new Wind(8.23, 17.56, "S");
        List<String> weatherDescriptions = new ArrayList<>();
        weatherDescriptions.add("rain and snow");

        Map<LocalDate, WeatherConditions> expected = new HashMap<>();
        expected.put(date, new WeatherConditions(
                date, weatherDescriptions, temperature, wind, new ArrayList<>()));

        when(weatherServiceMock.getWeather(new Location(55.87, 26.52)))
                .thenReturn(expected);

        this.mockMvc
                .perform(get((String.format("%s?lat=55.87&lon=26.52", URL))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("rain and snow")));
    }

    @Test
    public void When_InvalidLocation_Then_getWeatherReturnsBadRequestError() throws Exception {
        this.mockMvc
                .perform(get((String.format("%s?lat=555&lon=26.52", URL))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("must be less than")));
    }

    @Test
    public void When_WeatherDataRetrievalUnsuccessful_Then_getWeatherReturnsServerError() throws Exception {
        when(weatherServiceMock.getWeather(new Location(55.87, 26.52)))
                .thenThrow(new WeatherDataException("placeholder") {});

        this.mockMvc
                .perform(get((String.format("%s?lat=55.87&lon=26.52", URL))))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().string(containsString("placeholder")));
    }

    @Test
    public void When_WeatherDataServiceThrowsOtherException_Then_getWeatherReturnsServerError() throws Exception {
        when(weatherServiceMock.getWeather(new Location(55.87, 26.52)))
                .thenThrow(new TestException("placeholder") {});

        this.mockMvc
                .perform(get((String.format("%s?lat=55.87&lon=26.52", URL))))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().string(not(containsString("placeholder"))))
                .andExpect(content().string(containsString("A server error has occurred")));
    }
}