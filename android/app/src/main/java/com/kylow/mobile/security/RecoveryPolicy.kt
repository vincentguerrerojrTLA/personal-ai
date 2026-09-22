package com.kylow.mobile.security

data class RecoveryAttempt(val deviceId:String,val proofPresent:Boolean,val at:Long)
object RecoveryPolicy{
 const val WINDOW_MS=600_000L
 fun allowed(a:RecoveryAttempt,knownDevice:Boolean,revoked:Boolean,now:Long)=
  a.deviceId.isNotBlank()&&a.proofPresent&&knownDevice&&!revoked&&now>=a.at&&now-a.at<=WINDOW_MS
}
