package org.acme.ai.aiservice.tools

import dev.langchain4j.agent.tool.ToolSpecification
import dev.langchain4j.service.tool.ToolExecutor
import dev.langchain4j.service.tool.ToolProvider
import dev.langchain4j.service.tool.ToolProviderRequest
import dev.langchain4j.service.tool.ToolProviderResult
import jakarta.enterprise.context.ApplicationScoped
import org.slf4j.LoggerFactory.getLogger
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

private val logger = getLogger(DynamicToolProviderSupplier::class.java)

/**
 * todo: make it dynamic
 */
@ApplicationScoped
internal class DynamicToolProviderSupplier : Supplier<ToolProvider> {
    private val toolProviders: Map<ToolSpecification, ToolExecutor> = ConcurrentHashMap()

    override fun get(): ToolProvider =
        ToolProvider {
            val builder = ToolProviderResult.builder()
            for (entry in toolProviders) {
                builder.add(entry.key, entry.value)
                logger.info("Provided tool: ${entry.key.name()}")
            }

            builder.build()
        }
}
