package com.kylow.mobile
import android.content.Context

data class SavedChat(val id:String,val title:String,val body:String,val pinned:Boolean,val updated:Long)

class ChatStore(c:Context){
 private val p=c.getSharedPreferences("kylow_chats",Context.MODE_PRIVATE)

 fun save(id:String,body:String){
  val first=body.lineSequence().drop(1).firstOrNull()?.take(42)?.ifBlank{"New chat"} ?: "New chat"
  val ids=(p.getStringSet("ids",emptySet()) ?: emptySet()).toMutableSet()
  ids.add(id)
  p.edit().putString("body_$id",body).putString("title_$id",first).putLong("time_$id",System.currentTimeMillis()).putStringSet("ids",ids).apply()
 }

 fun list():List<SavedChat>{
  val ids=p.getStringSet("ids",emptySet()) ?: emptySet()
  return ids.map { id ->
   SavedChat(id,p.getString("title_$id","New chat") ?: "New chat",p.getString("body_$id","") ?: "",p.getBoolean("pin_$id",false),p.getLong("time_$id",0))
  }.sortedWith(compareByDescending<SavedChat>{it.pinned}.thenByDescending{it.updated})
 }

 fun latest():SavedChat?=list().firstOrNull()
 fun pin(id:String,on:Boolean){p.edit().putBoolean("pin_$id",on).apply()}
 fun delete(id:String){
  val ids=(p.getStringSet("ids",emptySet()) ?: emptySet()).toMutableSet()
  ids.remove(id)
  p.edit().remove("body_$id").remove("title_$id").remove("time_$id").remove("pin_$id").putStringSet("ids",ids).apply()
 }
}