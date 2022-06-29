package com.github.ingaelsta.weatherinfo.weather.deserialize;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.ingaelsta.weatherinfo.commons.Conversion;
import com.github.ingaelsta.weatherinfo.weather.model.Temperature;
import com.github.ingaelsta.weatherinfo.weather.model.WeatherConditions;
import com.github.ingaelsta.weatherinfo.weather.model.Alert;
import com.github.ingaelsta.weatherinfo.weather.model.Wind;
import com.github.ingaelsta.weatherinfo.weather.exception.OWMDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OWMDeserializerTest {
    private JacksonTester<Map<LocalDate, WeatherConditions>> json;
    private File jsonNoAlerts;
    private File jsonWithAlerts;
    private File jsonWrongDataType;
    private File jsonMissingData;
    private File jsonMissingAlertData;
    private File jsonWrongAlertDataType;
    private File jsonNoDailyData;
    private File jsonEmptyDailyData;
    private OWMDeserializer deserializer = new OWMDeserializer();

    public OWMDeserializerTest() throws IOException {
    }

    @BeforeEach
    public void setup() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("OWMDeserializer",
                new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Map.class, deserializer);
        mapper.registerModule(module);

        JacksonTester.initFields(this, mapper);

        jsonNoAlerts = ResourceUtils.getFile(
                "classpath:weather/valid_single_day_no_alerts.json");
        jsonWithAlerts = ResourceUtils.getFile(
                "classpath:weather/valid_two_days_with_alerts.json");
        jsonWrongDataType = ResourceUtils.getFile(
                "classpath:weather/invalid_single_day_wrong_data_type.json");
        jsonWrongAlertDataType = ResourceUtils.getFile(
                "classpath:weather/invalid_single_day_wrong_data_type_in_alert.json");
        jsonMissingData = ResourceUtils.getFile(
                "classpath:weather/invalid_single_day_missing_data.json");
        jsonMissingAlertData = ResourceUtils.getFile(
                "classpath:weather/invalid_single_day_missing_alert_data.json");
        jsonNoDailyData = ResourceUtils.getFile(
                "classpath:weather/invalid_single_day_no_daily_weather_data.json");
        jsonEmptyDailyData = ResourceUtils.getFile(
                "classpath:weather/invalid_single_day_empty_daily_weather_data.json");
    }

    @Test
    void When_DataValidWithNoAlerts_Then_deserializeReturnsMapWithWeatherConditionsWithNoAlerts () throws IOException {
        String text = new String(Files.readAllBytes(jsonNoAlerts.toPath()));
        Map<LocalDate, WeatherConditions> weatherConditionsMap = this.json.parseObject(text);

        LocalDate date = Conversion.convertDate(1643536800).toLocalDate();
        Temperature temperature = new Temperature(1.64, 1.09, -0.16, -0.94);
        Wind wind = new Wind(8.23, 17.56, "S");
        List<String> weatherDescriptions = new ArrayList<>();
        weatherDescriptions.add("rain and snow");

        WeatherConditions conditions = new WeatherConditions(
                date, weatherDescriptions, temperature, wind, new ArrayList<>());

        Map<LocalDate, WeatherConditions> expected = new HashMap<>();
        expected.put(date, conditions);

        assertEquals(expected, weatherConditionsMap);
    }

    @Test
    void When_DataValidWithAlerts_Then_deserializeReturnsMapWithWeatherConditionsWithAlerts () throws IOException {
        String text = new String(Files.readAllBytes(jsonWithAlerts.toPath()));

        Map<LocalDate, WeatherConditions> weatherConditionsMap = this.json.parseObject(text);

        Map<LocalDate, WeatherConditions> expected = new LinkedHashMap<>();

        //2022-01-30
        LocalDate date = Conversion
                .convertDate(1643536800).toLocalDate();
        Temperature temperature = new Temperature(1.8, 1.19, -0.18, -0.47);
        Wind wind = new Wind(17.08, 21.9, "N");

        List<String> weatherDescriptions = new ArrayList<>();
        weatherDescriptions.add("rain and snow");

        List<Alert> alerts = new ArrayList<>();
        Alert alert1 = new Alert("Yellow Flooding Warning",
                Conversion.convertDate(1643364000),
                Conversion.convertDate(1643716800));

        Alert alert2 = new Alert("Red Wind Warning",
                Conversion.convertDate(1643518800),
                Conversion.convertDate(1643554800));

        Alert alert3 = new Alert("Orange Snow-Ice Warning",
                Conversion.convertDate(1643536800),
                Conversion.convertDate(1643590800));

        alerts.add(alert1);
        alerts.add(alert2);
        alerts.add(alert3);

        //2022-01-31
        WeatherConditions conditions1 = new WeatherConditions(date, weatherDescriptions, temperature, wind, alerts);
        expected.put(date, conditions1);

        date = Conversion.convertDate(1643623200).toLocalDate();
        temperature = new Temperature(-0.73, -0.26, -1.17, -1.92);
        wind = new Wind(12.78, 16.97, "N");
        weatherDescriptions = new ArrayList<>();
        weatherDescriptions.add("light snow");

        alerts = new ArrayList<>();
        alerts.add(alert1);
        alerts.add(alert3);

        WeatherConditions conditions2 = new WeatherConditions(date, weatherDescriptions, temperature, wind, alerts);
        expected.put(date, conditions2);

        assertEquals(expected, weatherConditionsMap);
    }

    @Test
    void When_DataValueMissing_Then_deserializeThrowsOWMDataException () throws IOException {
        String text = new String(Files.readAllBytes(jsonMissingData.toPath()));
        assertThrows(OWMDataException.class, () -> this.json.parseObject(text));
    }

    @Test
    void WhenAlertValueMissing_Then_deserializeThrowsOWMDataException () throws IOException {
        String text = new String(Files.readAllBytes(jsonMissingAlertData.toPath()));
        assertThrows(OWMDataException.class, () -> this.json.parseObject(text));
    }

    @Test
    void WhenDailyWeatherDataMissing_Then_deserializeThrowsOWMDataException () throws IOException {
        String text1 = new String(Files.readAllBytes(jsonNoDailyData.toPath()));
        assertThrows(OWMDataException.class, () -> this.json.parseObject(text1));
        String text2 = new String(Files.readAllBytes(jsonEmptyDailyData.toPath()));
        assertThrows(OWMDataException.class, () -> this.json.parseObject(text2));
    }

    @Test
    void WhenJsonHasNonNumericValueForNumber_Then_deserializeThrowsOWMDataException () throws IOException {
        String text = new String(Files.readAllBytes(jsonWrongDataType.toPath()));
        assertThrows(OWMDataException.class, () -> this.json.parseObject(text));
    }

    @Test
    void When_JsonHasNonNumericValueForNumberInAlert_Then_deserializeThrowsOWMDataException () throws IOException {
        String text = new String(Files.readAllBytes(jsonWrongAlertDataType.toPath()));
        assertThrows(OWMDataException.class, () -> this.json.parseObject(text));
    }

    @Test
    void WindDegreesAreSuccessfullyConvertedToMainDirections() throws IOException {
        assertEquals(deserializer.windDegreesToDirection(-4), new Wind (null, null, null));
        assertEquals(deserializer.windDegreesToDirection(0), new Wind (null, null, "N"));
        assertEquals(deserializer.windDegreesToDirection(60), new Wind (null, null, "NE"));
        assertEquals(deserializer.windDegreesToDirection(80), new Wind (null, null, "E"));
        assertEquals(deserializer.windDegreesToDirection(115), new Wind (null, null, "SE"));
        assertEquals(deserializer.windDegreesToDirection(202), new Wind (null, null, "S"));
        assertEquals(deserializer.windDegreesToDirection(203), new Wind (null, null, "SW"));
        assertEquals(deserializer.windDegreesToDirection(260), new Wind (null, null, "W"));
        assertEquals(deserializer.windDegreesToDirection(300), new Wind (null, null, "NW"));
        assertEquals(deserializer.windDegreesToDirection(350), new Wind (null, null, "N"));
        assertEquals(deserializer.windDegreesToDirection(400), new Wind (null, null, null));
    }
}
