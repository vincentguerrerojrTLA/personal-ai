package com.kylow.mobile.security
import com.kylow.mobile.trust.ActionState
import org.junit.Assert.*
import org.junit.Test
class VerifiedActionTest{
 @Test fun successRequiresEvidence(){var a=VerifiedActionFlow.requested("x");a=VerifiedActionFlow.authorized(a,true);a=VerifiedActionFlow.attempted(a);assertEquals(ActionState.FAILED,VerifiedActionFlow.verified(a,true,null).state)}
 @Test fun completeFlowCanVerify(){var a=VerifiedActionFlow.requested("x");a=VerifiedActionFlow.authorized(a,true);a=VerifiedActionFlow.attempted(a);a=VerifiedActionFlow.verified(a,true,"remote_ack");assertEquals(ActionState.VERIFIED_SUCCESS,a.state)}
}
