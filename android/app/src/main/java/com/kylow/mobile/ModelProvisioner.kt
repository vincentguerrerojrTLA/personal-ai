package com.kylow.mobile

import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

/**
 * Explicit first-run model provisioner.
 * Downloads to a temporary app-private file, verifies SHA-256, then atomically promotes it.
 */
class ModelProvisioner(private val modelManager: LocalModelManager) {
    data class Progress(val downloadedBytes: Long, val totalBytes: Long?)
    data class Installed(val file: File, val sizeBytes: Long, val sha256: String)

    fun download(
        manifest: ModelManifest,
        onProgress: (Progress) -> Unit = {}
    ): Result<Installed> = runCatching {
        require(manifest.source.startsWith("https://")) { "Model source must use HTTPS." }
        val destination = modelManager.destination()
        val temp = File(destination.parentFile, destination.name + ".part")
        temp.delete()

        val connection = (URL(manifest.source).openConnection() as HttpURLConnection).apply {
            instanceFollowRedirects = true
            connectTimeout = 15_000
            readTimeout = 30_000
            requestMethod = "GET"
        }
        try {
            connection.connect()
            require(connection.responseCode in 200..299) { "Model download failed: HTTP ${connection.responseCode}" }
            val total = connection.contentLengthLong.takeIf { it > 0 }
            val digest = MessageDigest.getInstance("SHA-256")
            var downloaded = 0L
            connection.inputStream.buffered().use { input ->
                temp.outputStream().buffered().use { output ->
                    val buffer = ByteArray(1024 * 1024)
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        if (count == 0) continue
                        output.write(buffer, 0, count)
                        digest.update(buffer, 0, count)
                        downloaded += count
                        onProgress(Progress(downloaded, total))
                    }
                }
            }
            val hash = digest.digest().joinToString("") { "%02x".format(it) }
            require(hash.equals(manifest.sha256, ignoreCase = true)) { "Downloaded model failed SHA-256 verification." }
            if (destination.exists()) require(destination.delete()) { "Could not replace existing model." }
            require(temp.renameTo(destination)) { "Could not install verified model." }
            Installed(destination, downloaded, hash)
        } catch (t: Throwable) {
            temp.delete()
            throw t
        } finally {
            connection.disconnect()
        }
    }
}
