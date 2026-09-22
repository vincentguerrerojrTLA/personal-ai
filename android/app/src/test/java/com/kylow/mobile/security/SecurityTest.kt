package com.kylow.mobile.security

import com.kylow.mobile.safety.*
import com.kylow.mobile.trust.ActionState
import org.junit.Assert.*
import org.junit.Test
class SecurityTest {
 @Test fun permissionDefaultsDenied(){ assertFalse(PermissionEngine.allowed(Permission.CAMERA)) }
 @Test fun revokedDeviceCannotOperate(){ val d=DeviceSecurity.revoke(DeviceSecurity.pair(DeviceIdentity())); assertFalse(DeviceSecurity.usable(d)) }
 @Test fun actionCannotSkipAuthorization(){ assertEquals(ActionState.FAILED,ActionAuthority.attempted(ActionAuthority.request("x")).state) }
 @Test fun sensitiveDataRequiresConfirmation(){ assertTrue(SafetyEngine.classify(setOf("location")).requiresConfirmation) }
}
