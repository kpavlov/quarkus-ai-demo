package org.acme.ai.aiservice

import kotlinx.serialization.Serializable

@Serializable
data class PoemResponse(
    val text: String,
)
