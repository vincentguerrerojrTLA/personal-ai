package com.kylow.mobile

import android.content.Context
import com.kylow.mobile.gateway.DesktopCredential
import com.kylow.mobile.gateway.DesktopCredentialStore

/**
 * Compatibility facade for Remote PC screens.
 * Secrets are delegated to the Android Keystore-backed DesktopCredentialStore.
 */
object RemotePcConfig {
    fun save(
        context:Context,
        endpoint:String,
        token:String,
        tlsPin:String,
        deviceId:String,
        desktopName:String?=null
    ){
        DesktopCredentialStore(context).save(
            DesktopCredential(endpoint,tlsPin,deviceId,token,desktopName)
        )
        // Remove credentials written by pre-secure builds.
        context.getSharedPreferences("kylow_remote_pc",Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun endpoint(context:Context)=DesktopCredentialStore(context).load()?.endpoint
    fun token(context:Context)=DesktopCredentialStore(context).load()?.credential
    fun tlsPin(context:Context)=DesktopCredentialStore(context).load()?.tlsPin
    fun deviceId(context:Context)=DesktopCredentialStore(context).load()?.deviceId
    fun clear(context:Context){
        DesktopCredentialStore(context).clear()
        context.getSharedPreferences("kylow_remote_pc",Context.MODE_PRIVATE).edit().clear().apply()
    }
    fun paired(context:Context)=DesktopCredentialStore(context).paired()
}
