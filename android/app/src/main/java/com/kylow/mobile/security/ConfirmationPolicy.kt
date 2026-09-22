package com.kylow.mobile.security

import com.kylow.mobile.safety.SafetyClass

data class Confirmation(val actionId:String,val approved:Boolean,val at:Long)
object ConfirmationPolicy{
 const val MAX_AGE_MS=120_000L
 fun valid(actionId:String,c:Confirmation?,risk:SafetyClass,now:Long):Boolean{
  if(risk==SafetyClass.NORMAL)return true
  return c!=null&&c.approved&&c.actionId==actionId&&now>=c.at&&now-c.at<=MAX_AGE_MS
 }
}
