package com.kylow.mobile.memory

data class MemoryItem(val ownerId:String,val id:String,val text:String,val consented:Boolean)
class PrivateMemory(private val ownerId:String){
 private val items=mutableMapOf<String,MemoryItem>()
 fun put(item:MemoryItem)=require(item.ownerId==ownerId&&item.consented).also{items[item.id]=item}
 fun get(id:String)=items[id]?.takeIf{it.ownerId==ownerId}
 fun delete(id:String)=items.remove(id)!=null
 fun export()=items.values.filter{it.ownerId==ownerId}.toList()
 fun clear()=items.clear()
}
