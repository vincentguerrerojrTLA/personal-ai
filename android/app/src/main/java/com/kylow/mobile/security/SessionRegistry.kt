package com.kylow.mobile.security

class SessionRegistry{
 private val revoked=mutableSetOf<String>()
 @Synchronized fun revoke(deviceId:String){require(deviceId.isNotBlank());revoked+=deviceId}
 @Synchronized fun isRevoked(deviceId:String)=deviceId in revoked
 @Synchronized fun mayRecover(deviceId:String)=deviceId.isNotBlank()&&!isRevoked(deviceId)
}
