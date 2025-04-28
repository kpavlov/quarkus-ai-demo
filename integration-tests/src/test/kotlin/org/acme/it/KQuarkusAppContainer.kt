package org.acme.it

import org.testcontainers.containers.GenericContainer

class KQuarkusAppContainer(image: String,
                           name: String,
                           exposedPort: Int = 8080) :
    GenericContainer<KQuarkusAppContainer>(image) {
    init {
        withExposedPorts(exposedPort)
        withLogConsumer {
            println("🐳$name: ${it.utf8String}")
        }
    }

    fun mappedPort(): Int = getMappedPort(exposedPorts.first())
}

