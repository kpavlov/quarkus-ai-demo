package org.acme.ai.chat

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler
import dev.langchain4j.model.language.LanguageModel
import io.smallrye.mutiny.Multi
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.jboss.logging.Logger

private val logger = Logger.getLogger(ChatModelResource::class.java)

// TODO: THIS EXAMPLE IS NOT READY YET

@Path("/lc4j/streaming")
open class StreamingLc4jChatResource(
    val model: ChatModel
) {
        /*
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.SERVER_SENT_EVENTS)
    fun hello(request: ChatApiRequest): Multi<String> {
        logger.info("Received request: ${request.userPrompt}")
//        val multi: Multi<String> =
        val emitter: Emitter<String> = TODO()
        model.chat(
            request.userPrompt,
            object : StreamingChatResponseHandler {
                override fun onPartialResponse(partialResponse: String) {
                    logger.info("Received partial response: $partialResponse")
                    emitter.send(partialResponse)
                }

                override fun onCompleteResponse(
                    completeResponse: dev.langchain4j.model.chat.response.ChatResponse?,
                ) {
                    logger.info("Received complete response: $completeResponse")
                    emitter.send(completeResponse.toString())
                }

                override fun onError(error: Throwable) {
                    logger.error("Received error: ${error.message}", error)
                    emitter.error(java.lang.RuntimeException(error))
                }
            },
        )
        return TODO()
    }    */
}
