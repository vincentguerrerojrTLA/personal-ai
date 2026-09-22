package com.kylow.mobile.gateway
import org.junit.Assert.*
import org.junit.Test
class EndpointPolicyTest{
 @Test fun httpsAllowed(){assertTrue(EndpointPolicy.allowed("https://example.com/api"))}
 @Test fun cleartextRejected(){assertFalse(EndpointPolicy.allowed("http://example.com/api"))}
 @Test fun embeddedCredentialsRejected(){assertFalse(EndpointPolicy.allowed("https://user:pass@example.com/api"))}
}
