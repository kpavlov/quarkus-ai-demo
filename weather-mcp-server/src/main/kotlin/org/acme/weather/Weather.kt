package org.acme.weather

import io.quarkiverse.mcp.server.Tool
import io.quarkiverse.mcp.server.ToolArg
import io.quarkus.qute.Qute
import org.eclipse.microprofile.rest.client.inject.RestClient

internal class Weather {
    @RestClient
    lateinit var weatherClient: WeatherClient

    @Tool(description = "Get weather alerts for a US state.")
    fun getAlerts(@ToolArg(description = "Two-letter US state code (e.g. CA, NY)") state: String): String {
        return formatAlerts(weatherClient.getAlerts(state))
    }

    @Tool(description = "Get weather forecast for a location.")
    fun getForecast(
        @ToolArg(description = "Latitude of the location") latitude: Double,
        @ToolArg(description = "Longitude of the location") longitude: Double
    ): String {
        val points = weatherClient.getPoints(latitude, longitude)
        val url = Qute.fmt("{p.properties.forecast}", mapOf("p" to points))
        return formatForecast(weatherClient.getForecast(url))
    }

    private fun formatForecast(forecast: Forecast): String {
        return forecast.properties.periods.joinToString("\n---\n") { period ->
            Qute.fmt(
                """
                Temperature: {p.temperature}°{p.temperatureUnit}
                Wind: {p.windSpeed} {p.windDirection}
                Forecast: {p.detailedForecast}
                """.trimIndent(),
                mapOf("p" to period)
            )
        }
    }

    private fun formatAlerts(alerts: Alerts): String {
        return alerts.features.joinToString("\n---\n") { feature ->
            Qute.fmt(
                """
                Event: {p.event}
                Area: {p.areaDesc}
                Severity: {p.severity}
                Description: {p.description}
                Instructions: {p.instruction}
                """.trimIndent(),
                mapOf("p" to feature.properties)
            )
        }
    }


}
