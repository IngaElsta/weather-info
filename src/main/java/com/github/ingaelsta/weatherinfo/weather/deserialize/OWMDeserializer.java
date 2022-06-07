package com.github.ingaelsta.weatherinfo.weather.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.ingaelsta.weatherinfo.commons.Conversion;
import com.github.ingaelsta.weatherinfo.weather.model.Alert;
import com.github.ingaelsta.weatherinfo.weather.model.Temperature;
import com.github.ingaelsta.weatherinfo.weather.model.WeatherConditions;
import com.github.ingaelsta.weatherinfo.weather.model.Wind;
import com.github.ingaelsta.weatherinfo.weather.exception.OWMDataException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class OWMDeserializer extends StdDeserializer<Map<LocalDate, WeatherConditions>> {

    public OWMDeserializer() {
        this(null);
    }

    public OWMDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Map<LocalDate, WeatherConditions> deserialize(
            JsonParser parser, DeserializationContext deserializer) throws IOException {

        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        List<Alert> allAlerts = processAlertNode(node.get("alerts"));

        return processDailyWeatherArray(node.get("daily"), allAlerts);
    }

    private static List<Alert> processAlertNode (JsonNode alertNode) {
        List<Alert> alerts = new ArrayList<>();
        if (alertNode != null && alertNode.isArray()) {
            alertNode.forEach(alertItem -> {
                Alert alert = processAlertNodeData(alertItem);
                alerts.add(alert);
            });
        }
        return alerts;
    }

    private static Alert processAlertNodeData(JsonNode alertItem) {
        try {
            String event = alertItem.get("event").asText();
            LocalDateTime start = Conversion.convertDate(
                    Long.parseLong(alertItem.get("start").asText()));
            LocalDateTime end = Conversion.convertDate(
                    Long.parseLong(alertItem.get("end").asText()));
            return new Alert(event, start, end);
        } catch (NullPointerException e) {
            log.error("OWMDeserializer: Node or element missing while processing alert in {}", alertItem);
            throw new OWMDataException("Failed to process weather data");
        } catch (NumberFormatException e) {
            log.error("OWMDeserializer: Numeric value unreadable while processing alert data in {}", alertItem);
            throw new OWMDataException("Failed to process weather data");
        }
    }

    private Map<LocalDate, WeatherConditions> processDailyWeatherArray(
            JsonNode DailyWeatherListNode, List<Alert> allAlerts) {
        Map<LocalDate, WeatherConditions> conditionsMap = new LinkedHashMap<>();
        if (DailyWeatherListNode == null
                || DailyWeatherListNode.isEmpty()
                || !DailyWeatherListNode.isArray()) {
            log.error("OWMDeserializer: Json did not contain daily weather data, {}", DailyWeatherListNode);
            throw new OWMDataException("Failed to process weather data");
        }
        DailyWeatherListNode.forEach(dailyWeatherNode -> {
            WeatherConditions conditions = processDailyWeatherNodeData(allAlerts,dailyWeatherNode);
            conditionsMap.put(conditions.getDate(), conditions);
        });

        return conditionsMap;
    }

    private WeatherConditions processDailyWeatherNodeData(List<Alert> allAlerts, JsonNode dailyWeatherNode) {
        try {
            LocalDate date = Conversion.convertDate(
                    Long.parseLong(dailyWeatherNode.get("dt").asText()))
                    .toLocalDate();
            WeatherConditions conditions = processWeatherConditions(date, dailyWeatherNode);
            conditions.setAlerts(gatherAlertDataForDay(allAlerts, date));
            return conditions;
        } catch (NullPointerException e) {
            log.error("OWMDeserializer: Node or element missing while processing daily data in {}",
                    dailyWeatherNode);
            throw new OWMDataException("Failed to process weather data");
        } catch (NumberFormatException e) {
            log.error("OWMDeserializer: Numeric value unreadable while processing daily data in {}",
                    dailyWeatherNode);
            throw new OWMDataException("Failed to process weather data");
        }
    }

    private WeatherConditions processWeatherConditions(LocalDate date, JsonNode dailyWeather) {
        Temperature temperature = gatherTemperatureData(dailyWeather.get("temp"));
        Wind wind = gatherWindData(dailyWeather);
        JsonNode weatherNode = dailyWeather.get("weather");
        List<String> weatherDescriptions = gatherWeatherDescriptions(weatherNode);
        return new WeatherConditions(
                date, weatherDescriptions, temperature, wind, null);
    }

    private List<String> gatherWeatherDescriptions(JsonNode weatherNode) {
        List<String> weatherDescriptions = new ArrayList<>();
        weatherNode.forEach(description -> weatherDescriptions.add(description.get("description").asText()));
        return weatherDescriptions;
    }

    private Wind gatherWindData(JsonNode dailyWeather) {
        Wind wind = new Wind(
                Double.parseDouble(dailyWeather.get("wind_speed").asText()),
                Double.parseDouble(dailyWeather.get("wind_gust").asText()),
                Wind.degreesToDirection(dailyWeather.get("wind_deg").asInt()));
        return wind;
    }

    private List<Alert> gatherAlertDataForDay(List<Alert> alerts, LocalDate date){
        List<Alert> dailyAlerts = new ArrayList<>();

        LocalDateTime beginningOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atStartOfDay().plusDays(1);
        alerts.forEach(alert -> {
            if ((alert.getAlertEnd().isAfter(beginningOfDay)) && (alert.getAlertStart().isBefore(endOfDay))) {
                dailyAlerts.add(alert);
            }
        });
        return dailyAlerts;
    }

    private Temperature gatherTemperatureData(JsonNode temperatureNode){
        return new Temperature(
                Double.parseDouble(temperatureNode.get("morn").asText()),
                Double.parseDouble(temperatureNode.get("day").asText()),
                Double.parseDouble(temperatureNode.get("eve").asText()),
                Double.parseDouble(temperatureNode.get("night").asText())
        );
    }
}
