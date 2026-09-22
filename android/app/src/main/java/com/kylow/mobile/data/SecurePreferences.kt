package com.kylow.mobile.data

import android.content.Context

class SecurePreferences(context:Context,private val ownerId:String,private val vault:EncryptedVault=EncryptedVault("kylow.settings.v1")){
 private val prefs=context.getSharedPreferences("kylow.secure.settings",Context.MODE_PRIVATE)
 init{require(ownerId.isNotBlank())}
 fun remove(key:String)=prefs.edit().remove("$ownerId:$key").commit()
 fun clearOwner():Boolean { val e=prefs.edit();prefs.all.keys.filter{it.startsWith("$ownerId:")}.forEach(e::remove);return e.commit() }
}
