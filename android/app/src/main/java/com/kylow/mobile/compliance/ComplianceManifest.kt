package com.kylow.mobile.compliance

data class Dependency(val name:String,val purpose:String,val required:Boolean,val telemetry:Boolean=false)
object ComplianceManifest {
 val dependencies=listOf(
  Dependency("AndroidX Core","Android platform compatibility",true),
  Dependency("AndroidX AppCompat","Android UI compatibility",true)
 )
 fun releaseAllowed()=dependencies.filter{it.required}.none{it.telemetry}
}
