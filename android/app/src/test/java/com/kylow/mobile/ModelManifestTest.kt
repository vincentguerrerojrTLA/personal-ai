package com.kylow.mobile

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ModelManifestTest {
    @Test fun acceptsPinnedHttpsModelMetadata() {
        val manifest = ModelManifest(
            id = "test",
            displayName = "Test Model",
            sha256 = "a".repeat(64),
            sizeBytes = 42,
            license = "test-license",
            source = "https://example.invalid/model.gguf"
        )
        assertEquals("test", manifest.id)
    }

    @Test fun rejectsMalformedHash() {
        assertThrows(IllegalArgumentException::class.java) {
            ModelManifest("x", "X", "bad", 1, "license", "https://example.invalid/x")
        }
    }

    @Test fun rejectsInsecureSource() {
        assertThrows(IllegalArgumentException::class.java) {
            ModelManifest("x", "X", "a".repeat(64), 1, "license", "http://example.invalid/x")
        }
    }
}
