package com.kylow.mobile.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

data class EncryptedBlob(val iv:ByteArray,val ciphertext:ByteArray)

class EncryptedVault(private val alias:String="kylow.vault.v1"){
 private val store=KeyStore.getInstance("AndroidKeyStore").apply{load(null)}
 private fun key():SecretKey {
  (store.getKey(alias,null) as? SecretKey)?.let{return it}
  return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore").apply{
   init(KeyGenParameterSpec.Builder(alias,KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
    .setKeySize(256).build())
  }.generateKey()
 }
 fun encrypt(ownerId:String,plain:ByteArray):EncryptedBlob {
  require(ownerId.isNotBlank())
  val c=Cipher.getInstance("AES/GCM/NoPadding"); c.init(Cipher.ENCRYPT_MODE,key()); c.updateAAD(ownerId.toByteArray())
  return EncryptedBlob(c.iv,c.doFinal(plain))
 }
 fun decrypt(ownerId:String,blob:EncryptedBlob):ByteArray {
  require(ownerId.isNotBlank()&&blob.iv.isNotEmpty())
  val c=Cipher.getInstance("AES/GCM/NoPadding"); c.init(Cipher.DECRYPT_MODE,key(),GCMParameterSpec(128,blob.iv)); c.updateAAD(ownerId.toByteArray())
  return c.doFinal(blob.ciphertext)
 }
}
