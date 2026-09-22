package com.kylow.mobile.compliance
object DependencyPolicy{
 private val blocked=setOf("UNKNOWN","UNLICENSED")
 fun allowed(name:String,license:String)=name.isNotBlank()&&license.isNotBlank()&&license.uppercase() !in blocked
}
