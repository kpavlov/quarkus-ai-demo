package org.acme.it

object TestEnvironment {
//    private val aiServerApp = KQuarkusAppContainer(
//        image = "acme/ai-server:latest",
//        name = "ai-server",
//    )
//    private val weatherApp = KQuarkusAppContainer(
//        image = "acme/weather-mcp-server:latest",
//        name = "weather",
//    )
    val dockerCompose = DockerCompose

    init {
        dockerCompose.start()
    }
}
