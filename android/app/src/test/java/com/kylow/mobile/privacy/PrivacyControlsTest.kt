package com.kylow.mobile.privacy
import org.junit.Assert.*
import org.junit.Test
class PrivacyControlsTest{
 @Test fun ownerCanDelete(){assertTrue(PrivacyControls.authorize("u",PrivacyRequest("u",PrivacyRequest.Type.DELETE)).allowed)}
 @Test fun otherUserCannotExport(){assertFalse(PrivacyControls.authorize("x",PrivacyRequest("u",PrivacyRequest.Type.EXPORT)).allowed)}
 @Test fun secretsExcludedFromExport(){assertFalse(ExportPolicy.include("token"));assertTrue(ExportPolicy.include("memory"))}
}
