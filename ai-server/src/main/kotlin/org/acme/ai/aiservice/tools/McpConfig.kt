package org.acme.ai.aiservice.tools

import io.smallrye.config.ConfigMapping
import java.net.URI

@ConfigMapping(prefix = "quarkus.mcp")
internal interface McpConfig {

    fun servers(): List<McpServer>

    interface McpServer{
        fun name(): String
        fun uri(): URI
    }

//      val client = SseClient("http://localhost:")
}
