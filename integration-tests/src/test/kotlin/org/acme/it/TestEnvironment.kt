package org.acme.it

import me.kpavlov.aimocks.openai.MockOpenai

object TestEnvironment {

    val openai = MockOpenai(port = 8089, verbose = true)
    val dockerCompose = DockerCompose(openaiPort = openai.port())

    init {
        dockerCompose.start()
    }
}
