package com.kylow.mobile

/**
 * Provenance/integrity metadata required before Kylow activates a model.
 * No model is trusted solely because a file exists on the device.
 */
data class ModelManifest(
    val id: String,
    val displayName: String,
    val sha256: String,
    val sizeBytes: Long,
    val license: String,
    val source: String
) {
    init {
        require(id.isNotBlank())
        require(displayName.isNotBlank())
        require(sha256.matches(Regex("[0-9a-fA-F]{64}")))
        require(sizeBytes > 0)
        require(license.isNotBlank())
        require(source.startsWith("https://"))
    }
}
