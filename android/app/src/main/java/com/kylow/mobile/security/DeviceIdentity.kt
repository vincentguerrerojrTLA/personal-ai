package com.kylow.mobile.security

import java.util.UUID

data class DeviceIdentity(val id:String=UUID.randomUUID().toString(),val paired:Boolean=false,val revoked:Boolean=false)
object DeviceSecurity {
 fun usable(d:DeviceIdentity)=d.paired&&!d.revoked
 fun pair(d:DeviceIdentity)=d.copy(paired=true)
 fun revoke(d:DeviceIdentity)=d.copy(revoked=true)
}
