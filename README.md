# weather-info
This API is a training task. It could be used as a backend for some sort of webpage
that uses either a map or alternatively a list of favorite locations as a way to choose
location to view weather prognosis for the next 7 days.

The weather service contains a single GET endpoint that returns a limited weather prognosis. 
Weather data is being cached for stored favorite locations as well as recent calls.
Circuit breaker pattern for calls to external API should be implemented as well.

The favorite service contains simple GET (all), POST and DELETE (by id) endpoints.

The one call API from https://openweathermap.org/ is used to retrieve the weather info.

Leaned down version of https://github.com/IngaElsta/spring_boot_task
