package com.kylow.mobile.gateway

import android.content.Context
import android.util.Base64
import com.kylow.mobile.data.EncryptedBlob
import com.kylow.mobile.data.EncryptedVault

data class DesktopCredential(
    val endpoint:String,
    val tlsPin:String,
    val deviceId:String,
    val credential:String,
    val desktopName:String?=null
)

class DesktopCredentialStore(context:Context){
    private val prefs=context.getSharedPreferences("kylow.desktop.bridge.v1",Context.MODE_PRIVATE)
    private val vault=EncryptedVault("kylow.desktop.bridge.credential.v1")
    private val aad="kylow.desktop.bridge.v1"

    fun save(value:DesktopCredential){
        require(EndpointPolicy.allowed(value.endpoint))
        require(value.tlsPin.startsWith("sha256/"))
        require(value.deviceId.isNotBlank() && value.credential.isNotBlank())
        val encrypted=vault.encrypt(aad,value.credential.toByteArray(Charsets.UTF_8))
        prefs.edit()
            .putString("endpoint",value.endpoint.trimEnd('/'))
            .putString("tls_pin",value.tlsPin)
            .putString("device_id",value.deviceId)
            .putString("desktop_name",value.desktopName)
            .putString("credential_iv",Base64.encodeToString(encrypted.iv,Base64.NO_WRAP))
            .putString("credential_ciphertext",Base64.encodeToString(encrypted.ciphertext,Base64.NO_WRAP))
            .apply()
    }

    fun load():DesktopCredential? = runCatching {
        val endpoint=prefs.getString("endpoint",null)?:return null
        val pin=prefs.getString("tls_pin",null)?:return null
        val deviceId=prefs.getString("device_id",null)?:return null
        val iv=Base64.decode(prefs.getString("credential_iv",null)?:return null,Base64.NO_WRAP)
        val ciphertext=Base64.decode(prefs.getString("credential_ciphertext",null)?:return null,Base64.NO_WRAP)
        val secret=vault.decrypt(aad,EncryptedBlob(iv,ciphertext)).toString(Charsets.UTF_8)
        DesktopCredential(endpoint,pin,deviceId,secret,prefs.getString("desktop_name",null))
    }.getOrNull()

    fun clear(){ prefs.edit().clear().apply() }
    fun paired()=load()!=null
}
