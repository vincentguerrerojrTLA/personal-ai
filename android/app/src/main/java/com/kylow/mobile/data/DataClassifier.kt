package com.kylow.mobile.data

enum class DataClass { PUBLIC, PRIVATE, SENSITIVE, SECRET }
object DataClassifier {
 private val secret=setOf("credential","token","password","private_key")
 private val sensitive=setOf("health","location","biometric","voice","camera","photo","file","conversation","memory","device")
 fun classify(tag:String)=when(tag.lowercase()){ in secret->DataClass.SECRET; in sensitive->DataClass.SENSITIVE; else->DataClass.PRIVATE }
 fun loggable(c:DataClass)=c==DataClass.PUBLIC
}
