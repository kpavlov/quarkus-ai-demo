package org.acme.ai.chat

import dev.langchain4j.data.message.SystemMessage.systemMessage
import dev.langchain4j.data.message.UserMessage.userMessage
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.chat.chat
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.Logger
import kotlin.time.measureTimedValue

private val logger = Logger.getLogger(ChatModelResource::class.java)

/**
 * A RESTful resource class for handling chat operations using the LC4J framework.
 * Provides blocking and suspending endpoints for chat interactions.
 */
@Path("/lc4j")
open class ChatModelResource(
    private val chatModel: ChatLanguageModel
) {
    @Path("/blocking")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    internal fun blockingChat(request: ChatApiRequest): ChatApiResponse {
        logger.info("Received request: ${request.userPrompt}")
        val result =
            measureTimedValue {
                chatModel.chat(
                    systemMessage(request.systemPrompt),
                    userMessage(request.userPrompt),
                )
            }
        val reply = result.value.aiMessage().text()
        logger.info("Generated response in (${result.duration}): $reply")
        return ChatApiResponse(
            duration = result.duration,
            reply = reply,
        )
    }

    @Path("/suspend")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    internal suspend fun suspendChat(request: ChatApiRequest): ChatApiResponse {
        logger.info("Received Suspend request: ${request.userPrompt}")
        val result =
            measureTimedValue {
                chatModel.chat {
                    messages += systemMessage(request.systemPrompt)
                    messages += userMessage(request.userPrompt)
                }
            }
        val reply = result.value.aiMessage().text()
        logger.info("Generated response in (${result.duration}): $reply")
        return ChatApiResponse(
            duration = result.duration,
            reply = reply,
        )
    }
}
