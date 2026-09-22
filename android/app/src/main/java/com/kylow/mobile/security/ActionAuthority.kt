package com.kylow.mobile.security

import com.kylow.mobile.trust.ActionState

data class ActionReceipt(val id:String,val state:ActionState,val verified:Boolean=false)
object ActionAuthority {
 fun request(id:String)=ActionReceipt(id,ActionState.REQUESTED)
 fun authorize(r:ActionReceipt, approved:Boolean)=r.copy(state=if(approved) ActionState.AUTHORIZED else ActionState.FAILED)
 fun attempted(r:ActionReceipt)=if(r.state==ActionState.AUTHORIZED) r.copy(state=ActionState.ATTEMPTED) else r.copy(state=ActionState.FAILED)
 fun verify(r:ActionReceipt, success:Boolean)=r.copy(state=if(success) ActionState.VERIFIED_SUCCESS else ActionState.FAILED,verified=success)
}
