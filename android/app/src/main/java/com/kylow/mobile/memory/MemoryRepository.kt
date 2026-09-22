package com.kylow.mobile.memory

import android.content.Context
import android.util.Base64
import com.kylow.mobile.data.EncryptedBlob
import com.kylow.mobile.data.EncryptedVault
import org.json.JSONObject

class MemoryRepository(context:Context, private val ownerId:String, private val vault:EncryptedVault=EncryptedVault()){
 private val prefs=context.getSharedPreferences("kylow.private.memory",Context.MODE_PRIVATE)
 init { require(ownerId.isNotBlank()) }

 fun put(id:String,text:String,consented:Boolean){
  require(id.isNotBlank()&&consented)
  val blob=vault.encrypt(ownerId,text.toByteArray(Charsets.UTF_8))
  val encoded=JSONObject()
   .put("iv",Base64.encodeToString(blob.iv,Base64.NO_WRAP))
   .put("data",Base64.encodeToString(blob.ciphertext,Base64.NO_WRAP)).toString()
  check(prefs.edit().putString(key(id),encoded).commit())
 }
 fun get(id:String):String? {
  val raw=prefs.getString(key(id),null)?:return null
  val json=JSONObject(raw)
  val blob=EncryptedBlob(Base64.decode(json.getString("iv"),Base64.NO_WRAP),Base64.decode(json.getString("data"),Base64.NO_WRAP))
  return vault.decrypt(ownerId,blob).toString(Charsets.UTF_8)
 }
 fun delete(id:String)=prefs.edit().remove(key(id)).commit()
 fun clearOwner():Boolean {
  val edit=prefs.edit(); prefs.all.keys.filter{it.startsWith(prefix())}.forEach(edit::remove); return edit.commit()
 }
 private fun prefix()="owner:$ownerId:"
 private fun key(id:String)=prefix()+id
}
