package com.kylow.mobile.security

enum class IncidentSeverity { LOW, MEDIUM, HIGH, CRITICAL }
data class Incident(val code:String,val severity:IncidentSeverity,val at:Long)
class IncidentControls{
 private var locked=false
 fun report(i:Incident){require(i.code.isNotBlank());if(i.severity==IncidentSeverity.CRITICAL)locked=true}
 fun executionAllowed()=!locked
 fun unlock(authorized:Boolean){if(authorized)locked=false}
}
