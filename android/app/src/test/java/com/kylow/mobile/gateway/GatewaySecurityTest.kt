package com.kylow.mobile.gateway
import com.kylow.mobile.security.*
import org.junit.Assert.*
import org.junit.Test
class GatewaySecurityTest{
 @Test fun replayRejected(){val g=ReplayGuard();val n="1234567890123456";assertTrue(g.accept(n));assertFalse(g.accept(n))}
 @Test fun pairingExpires(){var t=1L;val p=PairingManager{t};val s=p.begin("phone");t=400_000L;assertNull(p.verify(s,s.challenge))}
 @Test fun sessionHasHardLifetime(){val s=SecureSession("phone",0,SessionPolicy.MAX_SESSION_MS+1);assertFalse(SessionPolicy.valid(s,1))}
}
