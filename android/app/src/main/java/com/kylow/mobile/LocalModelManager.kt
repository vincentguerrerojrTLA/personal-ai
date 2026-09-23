package com.kylow.mobile

import android.content.Context
import java.io.File
import java.security.MessageDigest

/**
 * Owns Kylow's private on-device model location and validation.
 * Models are app-private and never silently downloaded or trusted.
 */
class LocalModelManager(private val context: Context) {
    private val modelDir: File
        get() = File(context.filesDir, "models").apply { mkdirs() }

    data class InstalledModel(
        val file: File,
        val sizeBytes: Long,
        val sha256: String
    )

    fun installed(): InstalledModel? {
        val file = File(modelDir, MODEL_FILE)
        if (!file.isFile || file.length() < MIN_MODEL_BYTES) return null
        return InstalledModel(file, file.length(), sha256(file))
    }

    fun destination(): File = File(modelDir, MODEL_FILE)

    fun remove() {
        destination().takeIf { it.exists() }?.delete()
    }

    fun verify(expectedSha256: String): Boolean {
        val model = installed() ?: return false
        return model.sha256.equals(expectedSha256.trim(), ignoreCase = true)
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().buffered().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val count = input.read(buffer)
                if (count <= 0) break
                digest.update(buffer, 0, count)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val MODEL_FILE = "kylow.gguf"
        private const val MIN_MODEL_BYTES = 16L * 1024L * 1024L
    }
}
