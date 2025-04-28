package org.acme.ai.aiservice

import io.smallrye.common.annotation.Blocking
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/lc4j/poem")
internal class PoetResource(
   private val  poetService: PoetService
) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    fun createPoem(request: PoemRequest): PoemResponse {
        val poem =
            poetService.writeAPoem(
                request.topic,
                request.lines,
            )
        return PoemResponse(poem)
    }
}
