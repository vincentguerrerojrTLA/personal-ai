package com.kylow.mobile

/**
 * Kylow's pinned inference-backend policy.
 * Backend source is built into the app; model weights remain separately
 * provisioned and integrity-verified before activation.
 */
object InferenceBackendPolicy {
    const val BACKEND = "llama.cpp"
    const val MODEL_FAMILY = "Qwen3-0.6B-GGUF"
    const val MODEL_LICENSE = "Apache-2.0"
    const val MIN_CONTEXT = 2048
    const val DEFAULT_CONTEXT = 4096

    fun supports(manifest: ModelManifest): Boolean =
        manifest.license.equals(MODEL_LICENSE, ignoreCase = true) &&
            manifest.displayName.contains("Qwen3", ignoreCase = true)
}
