package com.kylow.mobile.gateway

import java.security.SecureRandom
import java.util.Base64

data class PairingSession(val deviceId:String,val challenge:String,val expiresAt:Long,val verified:Boolean=false)
class PairingManager(private val now:()->Long={System.currentTimeMillis()}){
 private val random=SecureRandom()
 fun begin(deviceId:String):PairingSession { require(deviceId.isNotBlank()); val b=ByteArray(24); random.nextBytes(b); return PairingSession(deviceId,Base64.getUrlEncoder().withoutPadding().encodeToString(b),now()+300_000) }
 fun verify(session:PairingSession,response:String)=session.takeIf{now()<=it.expiresAt && constantTime(it.challenge,response)}?.copy(verified=true)
 private fun constantTime(a:String,b:String):Boolean { val aa=a.toByteArray(); val bb=b.toByteArray(); var d=aa.size xor bb.size; for(i in 0 until maxOf(aa.size,bb.size)) d=d or ((aa.getOrElse(i){0}).toInt() xor (bb.getOrElse(i){0}).toInt()); return d==0 }
}
