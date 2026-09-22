package com.kylow.mobile.security

enum class Permission { CAMERA, MICROPHONE, LOCATION, FILES, PC_CONTROL, HEALTH_DATA }
data class Grant(val permission:Permission,val granted:Boolean,val sessionOnly:Boolean=true)
object PermissionEngine {
 private val grants=mutableMapOf<Permission,Grant>()
 fun grant(g:Grant){ grants[g.permission]=g }
 fun revoke(p:Permission){ grants.remove(p) }
 fun allowed(p:Permission)=grants[p]?.granted==true
 fun clearSession(){ grants.entries.removeIf{it.value.sessionOnly} }
}
