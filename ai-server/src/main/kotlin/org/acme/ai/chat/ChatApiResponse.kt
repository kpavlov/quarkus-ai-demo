package org.acme.ai.chat

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class ChatApiResponse(
    val duration: Duration,
    val reply: String,
)
