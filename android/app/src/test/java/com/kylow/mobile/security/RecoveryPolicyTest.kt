package com.kylow.mobile.security
import org.junit.Assert.*
import org.junit.Test
class RecoveryPolicyTest{
 @Test fun revokedDeviceCannotRecover(){assertFalse(RecoveryPolicy.allowed(RecoveryAttempt("d",true,1),true,true,2))}
 @Test fun proofAndKnownDeviceRequired(){assertFalse(RecoveryPolicy.allowed(RecoveryAttempt("d",false,1),true,false,2));assertTrue(RecoveryPolicy.allowed(RecoveryAttempt("d",true,1),true,false,2))}
 @Test fun registryRevocationPersistsForProcess(){val r=SessionRegistry();r.revoke("d");assertTrue(r.isRevoked("d"));assertFalse(r.mayRecover("d"))}
}
