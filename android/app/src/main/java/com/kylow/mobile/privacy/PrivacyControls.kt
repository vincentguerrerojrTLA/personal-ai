package com.kylow.mobile.privacy

data class PrivacyRequest(val ownerId:String,val type:Type){ enum class Type{ACCESS,EXPORT,DELETE} }
data class PrivacyDecision(val allowed:Boolean,val reason:String)

object PrivacyControls{
 fun authorize(requester:String,r:PrivacyRequest):PrivacyDecision =
  if(requester==r.ownerId&&requester.isNotBlank()) PrivacyDecision(true,"owner_verified")
  else PrivacyDecision(false,"owner_mismatch")
}
