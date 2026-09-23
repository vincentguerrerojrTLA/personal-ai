package com.kylow.mobile

/**
 * Stable boundary between Kylow's product UI and an on-device inference engine.
 * The engine implementation is deliberately replaceable so Kylow is not tied
 * to a single model vendor or runtime.
 */
interface LocalAiRuntime {
    val state: RuntimeState
    fun generate(request: InferenceRequest, onToken: (String) -> Unit, onComplete: (Result<String>) -> Unit)
    fun close()
}

data class InferenceRequest(
    val userText: String,
    val systemPrompt: String = "You are Kylow, a helpful, private, general-purpose AI assistant.",
    val maxTokens: Int = 512
)

sealed interface RuntimeState {
    data object Unavailable : RuntimeState
    data object Ready : RuntimeState
    data object Loading : RuntimeState
    data class Error(val message: String) : RuntimeState
}

/**
 * Safe temporary engine used only when no local model has been installed.
 * It never pretends to be model inference.
 */
class NoModelRuntime : LocalAiRuntime {
    override val state: RuntimeState = RuntimeState.Unavailable

    override fun generate(
        request: InferenceRequest,
        onToken: (String) -> Unit,
        onComplete: (Result<String>) -> Unit
    ) {
        onComplete(Result.failure(IllegalStateException("No local Kylow model is installed yet.")))
    }

    override fun close() = Unit
}
