package com.kylow.mobile.trust

import org.junit.Assert.*
import org.junit.Test

class TrustCoreTest {
 @Test fun highRiskDefaultsOff() { assertFalse(TrustCore.mayExecute(Capability("pc.control", Risk.HIGH), false)) }
 @Test fun disabledCapabilityNeverExecutes() { assertFalse(TrustCore.mayExecute(Capability("camera", Risk.SENSITIVE, false), true)) }
 @Test fun principlesAreLocked() { assertTrue(TrustCore.protectedPrinciples.containsAll(setOf("privacy","consent","honesty","human_authority"))) }
}
