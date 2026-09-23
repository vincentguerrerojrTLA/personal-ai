package com.kylow.mobile

import java.util.concurrent.Executors

/**
 * LocalAiRuntime backed by Kylow's packaged native inference bridge.
 * Model work stays off the Android UI thread.
 */
class NativeLocalAiRuntime(
    private val modelManager: LocalModelManager,
    private val contextSize: Int = 2048
) : LocalAiRuntime {
    private val executor = Executors.newSingleThreadExecutor()
    @Volatile private var handle: Long = 0
    @Volatile private var runtimeState: RuntimeState = RuntimeState.Loading
    override val state: RuntimeState get() = runtimeState

    init {
        executor.execute {
            val model = modelManager.installed()
            runtimeState = when {
                model == null -> RuntimeState.Unavailable
                !NativeInferenceBridge.available -> RuntimeState.Error("Native inference engine is not packaged.")
                else -> runCatching {
                    handle = NativeInferenceBridge.create(model.file.absolutePath, contextSize)
                    require(handle != 0L) { "Native model initialization failed." }
                    RuntimeState.Ready
                }.getOrElse { RuntimeState.Error(it.message ?: "Model initialization failed.") }
            }
        }
    }

    override fun generate(
        request: InferenceRequest,
        onToken: (String) -> Unit,
        onComplete: (Result<String>) -> Unit
    ) {
        if (runtimeState !is RuntimeState.Ready || handle == 0L) {
            onComplete(Result.failure(IllegalStateException("Local model is not ready.")))
            return
        }
        executor.execute {
            val result = runCatching {
                NativeInferenceBridge.generate(
                    handle,
                    request.systemPrompt,
                    request.userText,
                    request.maxTokens
                )
            }
            result.getOrNull()?.let(onToken)
            onComplete(result)
        }
    }

    override fun close() {
        val current = handle
        handle = 0
        if (current != 0L && NativeInferenceBridge.available) {
            runCatching { NativeInferenceBridge.destroy(current) }
        }
        executor.shutdown()
    }
}
