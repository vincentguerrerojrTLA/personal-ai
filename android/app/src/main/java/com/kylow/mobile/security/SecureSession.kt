package com.kylow.mobile.security

data class SecureSession(val deviceId:String,val issuedAt:Long,val expiresAt:Long,val revoked:Boolean=false)
object SessionPolicy {
 const val MAX_SESSION_MS=3_600_000L
 fun valid(s:SecureSession,now:Long)=!s.revoked&&s.deviceId.isNotBlank()&&now in s.issuedAt..s.expiresAt&&s.expiresAt-s.issuedAt<=MAX_SESSION_MS
}
