package com.kylow.mobile.data
import org.junit.Assert.*
import org.junit.Test
class VaultPolicyTest{
 @Test fun envelopeIsOwnerIsolated(){val e=VaultEnvelope("a","memory",byteArrayOf(1),byteArrayOf(2));assertTrue(e.readableBy("a"));assertFalse(e.readableBy("b"))}
 @Test(expected=IllegalArgumentException::class) fun blankOwnerRejected(){VaultEnvelope("","memory",byteArrayOf(1),byteArrayOf(2))}
}
