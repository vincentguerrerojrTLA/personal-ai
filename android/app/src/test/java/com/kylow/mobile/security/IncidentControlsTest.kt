package com.kylow.mobile.security
import org.junit.Assert.*
import org.junit.Test
class IncidentControlsTest{
 @Test fun criticalIncidentLocksExecution(){val c=IncidentControls();c.report(Incident("credential_exposure",IncidentSeverity.CRITICAL,1));assertFalse(c.executionAllowed())}
 @Test fun unauthorizedUnlockFails(){val c=IncidentControls();c.report(Incident("x",IncidentSeverity.CRITICAL,1));c.unlock(false);assertFalse(c.executionAllowed());c.unlock(true);assertTrue(c.executionAllowed())}
}
