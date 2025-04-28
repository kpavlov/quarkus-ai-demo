package org.acme.it

import org.testcontainers.containers.ComposeContainer
import java.io.File


object DockerCompose {

    private var environment: ComposeContainer = ComposeContainer(
        File("src/test/docker-compose.yml")
    )
        .withExposedService("weather-service", 8080)
        .withExposedService("ai-service", 8080)
        .withLocalCompose(true)
        .withLogConsumer("ai-service") {
            println("🧠🐳: ${it.utf8String}")
        }
        .withLogConsumer("weather-service") {
            println("🌧️️🐳: ${it.utf8String}")
        }

    fun start() {
        environment.start()
        Runtime.getRuntime().addShutdownHook(Thread { environment.close() })
    }

    fun getWeatherServiceEndpoint() = getServiceEndpoint("weather-service");
    fun getAiServiceEndpoint() = getServiceEndpoint("ai-service");

    private fun getServiceEndpoint(serviceName: String) =
        "http://localhost:${environment.getServicePort(serviceName, 8080)}"
}



