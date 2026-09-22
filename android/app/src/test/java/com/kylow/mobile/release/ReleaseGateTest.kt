package com.kylow.mobile.release
import org.junit.Assert.*
import org.junit.Test
class ReleaseGateTest{@Test fun criticalFailureBlocksRelease(){assertFalse(ReleaseGate.evaluate(true,true,false,true,true).allowed)} @Test fun allGatesPass(){assertTrue(ReleaseGate.evaluate(true,true,true,true,true).allowed)}}
