package com.kylow.mobile

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PinnedModelPolicyTest {
    @Test fun pinnedModelUsesHttpsAndExactSha256() {
        val model = NativeLocalAiRuntime.PINNED_MODEL
        assertTrue(model.source.startsWith("https://"))
        assertTrue(model.sha256.matches(Regex("[0-9a-f]{64}")))
        assertEquals("Apache-2.0", model.license)
        assertTrue(InferenceBackendPolicy.supports(model))
    }
}
