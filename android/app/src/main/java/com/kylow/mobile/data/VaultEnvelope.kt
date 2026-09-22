package com.kylow.mobile.data

data class VaultEnvelope(val ownerId:String,val type:String,val iv:ByteArray,val ciphertext:ByteArray){
 init { require(ownerId.isNotBlank()); require(type.isNotBlank()); require(iv.isNotEmpty()); require(ciphertext.isNotEmpty()) }
 fun readableBy(requester:String)=requester==ownerId
}
