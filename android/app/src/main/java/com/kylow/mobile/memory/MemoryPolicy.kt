package com.kylow.mobile.memory

object MemoryPolicy {
 const val MAX_ITEM_CHARS=100_000
 fun mayStore(ownerId:String,id:String,text:String,consented:Boolean)=ownerId.isNotBlank()&&id.isNotBlank()&&text.length<=MAX_ITEM_CHARS&&consented
}
