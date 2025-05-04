package org.acme.ai.aiservice

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/lc4j/poem")
internal class PoetResource(
    private val editorService: EditorService
) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
//    @ActivateRequestContext
    suspend fun createPoem(request: PoemRequest): PoemResponse {
        val poem = editorService.writeCopyrightedPoem(
            request.topic,
            request.lines,
        )

        return PoemResponse(poem)
    }
}
