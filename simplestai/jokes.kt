//DEPS io.quarkus.platform:quarkus-bom:3.21.0@pom
//DEPS io.quarkus:quarkus-picocli
//DEPS io.quarkiverse.langchain4j:quarkus-langchain4j-openai:1.0.0.CR1

import dev.langchain4j.model.chat.ChatModel
import picocli.CommandLine.Command

@Command(name = "jokes")
class Jokes (val ai: ChatModel) : Runnable {

    override fun run() {
        println(ai.chat("tell me a joke about Kotlin and Denmark"))
    }
}

