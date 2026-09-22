package com.kylow.mobile
import android.content.Context
data class SavedChat(val id:String,val title:String,val body:String,val pinned:Boolean,val updated:Long)
class ChatStore(c:Context){
 private val p=c.getSharedPreferences("kylow_chats",Context.MODE_PRIVATE)
 fun save(id:String,body:String){val first=body.lineSequence().drop(1).firstOrNull()?.take(42)?.ifBlank{"New chat"}?:"New chat";p.edit().putString("body_$id",body).putString("title_$id",first).putLong("time_$id",System.currentTimeMillis()).putStringSet("ids",(p.getStringSet("ids",emptySet())?:emptySet())+id).apply()}
 fun list():List<SavedChat>=(p.getStringSet("ids",emptySet())?:emptySet()).map{SavedChat(it,p.getString("title_$it","New chat")!!,p.getString("body_$it","")!!,p.getBoolean("pin_$it",false),p.getLong("time_$it",0))}.sortedWith(compareByDescending<SavedChat>{it.pinned}.thenByDescending{it.updated})
 fun latest()=list().firstOrNull()
 fun pin(id:String,on:Boolean)=p.edit().putBoolean("pin_$id",on).apply()
 fun delete(id:String){p.edit().remove("body_$id").remove("title_$id").remove("time_$id").remove("pin_$id").putStringSet("ids",(p.getStringSet("ids",emptySet())?:emptySet())-id).apply()}
}