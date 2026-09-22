package com.kylow.mobile.compliance
import org.junit.Assert.*
import org.junit.Test
class ReleaseReadinessTest{
 @Test fun incompleteEvidenceBlocks(){assertFalse(ReleaseReadiness.allowed(ReleaseEvidence(true,true,true,true,true,false)))}
 @Test fun completeEvidencePasses(){assertTrue(ReleaseReadiness.allowed(ReleaseEvidence(true,true,true,true,true,true)))}
 @Test fun unknownLicenseRejected(){assertFalse(DependencyPolicy.allowed("dependency","UNKNOWN"))}
}
