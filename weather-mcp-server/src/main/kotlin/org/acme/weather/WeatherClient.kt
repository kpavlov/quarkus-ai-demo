package org.acme.weather

import io.quarkus.rest.client.reactive.Url
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.jboss.resteasy.reactive.RestPath

@RegisterRestClient(baseUri = "https://api.weather.gov")
interface WeatherClient {
    @GET
    @Path("/alerts/active/area/{state}")
    fun getAlerts(@RestPath state: String): Alerts

    @GET
    @Path("/points/{latitude},{longitude}")
    fun getPoints(
        @RestPath latitude: Double,
        @RestPath longitude: Double
    ): Map<String, Any>

    @GET
    @Path("/")
    fun getForecast(@Url url: String): Forecast
}

data class Properties(
    val id: String,
    val areaDesc: String,
    val event: String,
    val severity: String,
    val description: String,
    val instruction: String
)

data class Feature(
    val id: String,
    val type: String,
    val geometry: Any,
    val properties: Properties
)

data class Alerts(
    val context: List<String>,
    val type: String,
    val features: List<Feature>,
    val title: String,
    val updated: String
)

data class Period(
    val name: String,
    val temperature: Int,
    val temperatureUnit: String,
    val windSpeed: String,
    val windDirection: String,
    val detailedForecast: String
)

data class ForecastProperties(
    val periods: List<Period>
)

data class Forecast(
    val properties: ForecastProperties
)
