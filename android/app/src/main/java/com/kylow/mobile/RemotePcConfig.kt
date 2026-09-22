package com.kylow.mobile
import android.content.Context
object RemotePcConfig {
 private const val PREF="kylow_remote_pc"
 fun save(context:Context, endpoint:String, token:String){context.getSharedPreferences(PREF,Context.MODE_PRIVATE).edit().putString("endpoint",endpoint.trimEnd('/')).putString("token",token).apply()}
 fun endpoint(context:Context)=context.getSharedPreferences(PREF,Context.MODE_PRIVATE).getString("endpoint",null)
 fun token(context:Context)=context.getSharedPreferences(PREF,Context.MODE_PRIVATE).getString("token",null)
 fun clear(context:Context)=context.getSharedPreferences(PREF,Context.MODE_PRIVATE).edit().clear().apply()
 fun paired(context:Context)=!endpoint(context).isNullOrBlank()&&!token(context).isNullOrBlank()
}
