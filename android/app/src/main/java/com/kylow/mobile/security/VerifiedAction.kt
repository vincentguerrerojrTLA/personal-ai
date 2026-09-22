package com.kylow.mobile.security
import com.kylow.mobile.trust.ActionState

data class VerifiedAction(val id:String,val state:ActionState,val evidence:String?=null){
 init { require(id.isNotBlank()); if(state==ActionState.VERIFIED_SUCCESS) require(!evidence.isNullOrBlank()) }
}
object VerifiedActionFlow{
 fun requested(id:String)=VerifiedAction(id,ActionState.REQUESTED)
 fun authorized(a:VerifiedAction,approved:Boolean)=if(approved)a.copy(state=ActionState.AUTHORIZED) else a.copy(state=ActionState.FAILED)
 fun attempted(a:VerifiedAction)=if(a.state==ActionState.AUTHORIZED)a.copy(state=ActionState.ATTEMPTED) else a.copy(state=ActionState.FAILED)
 fun verified(a:VerifiedAction,success:Boolean,evidence:String?)=if(a.state==ActionState.ATTEMPTED&&success&&!evidence.isNullOrBlank())a.copy(state=ActionState.VERIFIED_SUCCESS,evidence=evidence) else a.copy(state=ActionState.FAILED,evidence=null)
}
