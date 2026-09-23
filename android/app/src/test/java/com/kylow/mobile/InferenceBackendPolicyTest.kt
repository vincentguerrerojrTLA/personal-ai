package com.kylow.mobile

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InferenceBackendPolicyTest {
    private fun manifest(name: String, license: String) = ModelManifest(
        id = "test",
        displayName = name,
        sha256 = "a".repeat(64),
        sizeBytes = 1,
        license = license,
        source = "https://example.invalid/model.gguf"
    )

    @Test fun acceptsPinnedQwen3ApacheModel() {
        assertTrue(InferenceBackendPolicy.supports(manifest("Qwen3 0.6B", "Apache-2.0")))
    }

    @Test fun rejectsWrongLicense() {
        assertFalse(InferenceBackendPolicy.supports(manifest("Qwen3 0.6B", "unknown")))
    }

    @Test fun rejectsWrongFamily() {
        assertFalse(InferenceBackendPolicy.supports(manifest("Other Model", "Apache-2.0")))
    }
}
