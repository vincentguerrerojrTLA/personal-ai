package com.kylow.mobile.security

import com.kylow.mobile.safety.SafetyClass

object ExecutionGuard{
 fun mayExecute(actionId:String,risk:SafetyClass,confirmation:Confirmation?,now:Long,permissionGranted:Boolean,deviceUsable:Boolean)=
  permissionGranted&&deviceUsable&&ConfirmationPolicy.valid(actionId,confirmation,risk,now)
}
