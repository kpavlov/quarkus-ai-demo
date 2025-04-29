package org.acme.ai.aiservice

import dev.langchain4j.agent.tool.Tool
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage
import dev.langchain4j.service.V
import io.quarkiverse.langchain4j.RegisterAiService
import org.acme.ai.aiservice.tools.DynamicToolProviderSupplier
import org.eclipse.microprofile.faulttolerance.Fallback
import org.eclipse.microprofile.faulttolerance.Timeout
import java.time.temporal.ChronoUnit

@RegisterAiService(
    toolProviderSupplier = DynamicToolProviderSupplier::class,
)
internal interface PoetService {

    @Fallback(fallbackMethod = "fallback")
    @SystemMessage("You are a professional poet.")
    @UserMessage(
        """
            Write a poem about {topic}. The poem should be {lines} lines long.
            Use all available tools provided to you to make content more interesting.
        """,
    )

    @Timeout(value = 7, unit = ChronoUnit.SECONDS)
    fun writeAPoem(
        @V("topic") topic: String,
        @V("lines") lines: Int,

    ): String

    @Suppress("unused")
    fun fallback(
        topic: String,
        lines: Int,
    ): String = "I'm sorry, I can't write a poem about $topic"
}
