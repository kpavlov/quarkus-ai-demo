package org.acme.it

import org.testcontainers.containers.ComposeContainer
import java.io.File


class DockerCompose(openaiPort: Int) {

    private var environment: ComposeContainer = ComposeContainer(
        File("src/test/docker-compose.yml")
    )
        .withEnv("QUARKUS_LANGCHAIN4J_OPENAI_API_BASE_URL", " http://host.docker.internal:$openaiPort/v1")
        .withExposedService("weather-service", 8080)
        .withExposedService("ai-service", 8080)
        .withLocalCompose(true)
        .withLogConsumer("ai-service") {
            print("🐳🧠: ${it.utf8String}")
        }
        .withLogConsumer("weather-service") {
            print("🐳🌧️️: ${it.utf8String}")
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



