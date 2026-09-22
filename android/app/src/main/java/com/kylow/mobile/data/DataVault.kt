package com.kylow.mobile.data

import java.security.MessageDigest

data class VaultRecord(val ownerId:String,val type:String,val payload:ByteArray)
object DataVault {
 fun ownerKey(ownerId:String)=MessageDigest.getInstance("SHA-256").digest(ownerId.toByteArray())
 fun canRead(requester:String, record:VaultRecord)=requester==record.ownerId
 fun redact(value:String)=if(value.isBlank()) "" else "[REDACTED]"
}
