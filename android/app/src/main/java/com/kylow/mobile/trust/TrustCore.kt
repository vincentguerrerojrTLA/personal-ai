package com.kylow.mobile.trust

enum class ActionState { REQUESTED, AUTHORIZED, ATTEMPTED, VERIFIED_SUCCESS, FAILED }

data class Capability(val id:String, val risk:Risk, val enabled:Boolean=false)
enum class Risk { LOW, SENSITIVE, HIGH }

object TrustCore {
    const val VERSION = "1.0"
    val protectedPrinciples = setOf("privacy","consent","honesty","human_authority","accountability","reversibility")
    fun requiresExplicitAuthorization(capability: Capability) = capability.risk != Risk.LOW
    fun mayExecute(capability: Capability, authorized:Boolean) = capability.enabled && (!requiresExplicitAuthorization(capability) || authorized)
}
