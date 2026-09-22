package com.kylow.mobile.security
import com.kylow.mobile.safety.SafetyClass
import org.junit.Assert.*
import org.junit.Test
class ExecutionGuardTest{
 @Test fun highRiskRequiresFreshMatchingConfirmation(){val now=1000L;assertFalse(ExecutionGuard.mayExecute("a",SafetyClass.HIGH_RISK,null,now,true,true));assertTrue(ExecutionGuard.mayExecute("a",SafetyClass.HIGH_RISK,Confirmation("a",true,now),now,true,true))}
 @Test fun staleConfirmationRejected(){val now=ConfirmationPolicy.MAX_AGE_MS+10;assertFalse(ExecutionGuard.mayExecute("a",SafetyClass.SENSITIVE,Confirmation("a",true,0),now,true,true))}
 @Test fun missingPermissionBlocks(){assertFalse(ExecutionGuard.mayExecute("a",SafetyClass.NORMAL,null,1,false,true))}
}
