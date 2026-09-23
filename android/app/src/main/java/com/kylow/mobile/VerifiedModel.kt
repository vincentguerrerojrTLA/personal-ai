package com.kylow.mobile

import java.io.File
import java.security.MessageDigest

/**
 * The only path from an installed model file to native inference.
 * Size and SHA-256 must match pinned provenance metadata.
 */
data class VerifiedModel(val file: File, val manifest: ModelManifest)

object ModelVerifier {
    fun verify(file: File, manifest: ModelManifest): Result<VerifiedModel> = runCatching {
        require(file.isFile) { "Model file is missing." }
        require(file.length() == manifest.sizeBytes) { "Model size does not match manifest." }
        require(sha256(file).equals(manifest.sha256, ignoreCase = true)) {
            "Model SHA-256 does not match manifest."
        }
        VerifiedModel(file, manifest)
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
}
