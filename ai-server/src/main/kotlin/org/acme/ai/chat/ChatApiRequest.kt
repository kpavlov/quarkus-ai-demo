package org.acme.ai.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatApiRequest(
    val systemPrompt: String,
    val userPrompt: String,
)
