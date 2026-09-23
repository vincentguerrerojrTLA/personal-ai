package com.kylow.mobile

/**
 * Narrow JNI boundary for Kylow's native local inference engine.
 * Native code is loaded only when packaged and available.
 */
internal object NativeInferenceBridge {
    val available: Boolean by lazy {
        runCatching {
            System.loadLibrary("kylow_inference")
            true
        }.getOrDefault(false)
    }

    external fun create(modelPath: String, contextSize: Int): Long
    external fun generate(
        handle: Long,
        systemPrompt: String,
        userText: String,
        maxTokens: Int
    ): String
    external fun destroy(handle: Long)
}
