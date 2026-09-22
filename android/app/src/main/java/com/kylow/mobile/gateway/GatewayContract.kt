package com.kylow.mobile.gateway

import com.kylow.mobile.security.ActionReceipt

data class GatewayRequest(val deviceId:String,val action:String,val nonce:String)
data class GatewayResponse(val accepted:Boolean,val receipt:ActionReceipt?,val error:String?=null)
object GatewayContract { const val PROTOCOL_VERSION=1; fun valid(r:GatewayRequest)=r.deviceId.isNotBlank()&&r.action.isNotBlank()&&r.nonce.length>=16 }
