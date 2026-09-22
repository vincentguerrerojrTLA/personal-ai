package com.kylow.mobile.safety

enum class SafetyClass { NORMAL, SENSITIVE, HIGH_RISK }
data class SafetyDecision(val classification:SafetyClass,val requiresConfirmation:Boolean)
object SafetyEngine {
 fun classify(tags:Set<String>):SafetyDecision {
  val high=tags.any{it in setOf("credential","pc_control","financial_action","emergency")}
  val sensitive=tags.any{it in setOf("health","location","camera","microphone","private_file")}
  return when { high->SafetyDecision(SafetyClass.HIGH_RISK,true); sensitive->SafetyDecision(SafetyClass.SENSITIVE,true); else->SafetyDecision(SafetyClass.NORMAL,false) }
 }
}
