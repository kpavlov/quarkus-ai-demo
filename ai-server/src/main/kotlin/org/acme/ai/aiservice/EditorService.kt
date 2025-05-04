package org.acme.ai.aiservice

import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

@ApplicationScoped
internal class EditorService(
    private val poetService: PoetService
) {
    private val aiServiceDispatcher =
        Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

    suspend fun writeCopyrightedPoem(
        topic: String,
        lines: Int,
    ): String {
        val originalPoem = withContext(aiServiceDispatcher) {
            poetService.writeAPoem(topic, lines)
        }

        val copyrightedPoem = originalPoem + "\n©️ AI-Server, ${java.time.Year.now()}"
        return copyrightedPoem
    }
}
