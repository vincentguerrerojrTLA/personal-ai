package com.kylow.mobile

import java.util.concurrent.Executors

/**
 * LocalAiRuntime backed by Kylow's packaged native inference bridge.
 * Only the exact pinned model is permitted to enter the native runtime.
 */
class NativeLocalAiRuntime(
    private val modelManager: LocalModelManager,
    private val manifest: ModelManifest = PINNED_MODEL,
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
                !InferenceBackendPolicy.supports(manifest) ->
                    RuntimeState.Error("Installed model is not approved for Kylow's inference backend.")
                model.sizeBytes != manifest.sizeBytes ||
                    !model.sha256.equals(manifest.sha256, ignoreCase = true) ->
                    RuntimeState.Error("Installed model failed integrity verification.")
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
                NativeInferenceBridge.generate(handle, request.systemPrompt, request.userText, request.maxTokens)
            }
            result.getOrNull()?.let(onToken)
            onComplete(result)
        }
    }

    override fun close() {
        val current = handle
        handle = 0
        if (current != 0L && NativeInferenceBridge.available) runCatching { NativeInferenceBridge.destroy(current) }
        executor.shutdown()
    }

    companion object {
        val PINNED_MODEL = ModelManifest(
            id = "qwen3-0.6b-q4-k-m",
            displayName = "Qwen3-0.6B-Q4_K_M",
            sha256 = "b0638f08417a2d3c8652760462eb5407c6e30173cf9608ad0820757a281eea0e",
            sizeBytes = 397_000_000L,
            license = "Apache-2.0",
            source = "https://huggingface.co/Qwen/Qwen3-0.6B-GGUF/resolve/1208e45d782fe18602c5eaf10e5758d5b0f24c03/Qwen3-0.6B-Q4_K_M.gguf"
        )
    }
}
