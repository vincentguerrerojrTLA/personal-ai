package com.kylow.mobile.gateway

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Base64

data class DesktopPairingLink(
    val endpoint:String,
    val tlsPin:String,
    val pairingCode:String,
    val desktopName:String?=null,
    val protocolVersion:Int=GatewayContract.PROTOCOL_VERSION
)

object DesktopPairingLinkParser {
    private val codePattern=Regex("^[A-Za-z0-9_-]{12,128}$")

    fun parse(raw:String):DesktopPairingLink {
        val uri=URI(raw.trim())
        require(uri.scheme.equals("kylow",true) && uri.host.equals("pair",true)) { "Invalid Kylow pairing link" }
        val q=query(uri.rawQuery)
        val endpoint=requireNotNull(q["endpoint"]) { "Missing endpoint" }.trimEnd('/')
        require(EndpointPolicy.allowed(endpoint)) { "HTTPS endpoint required" }
        val pin=requireNotNull(q["pin"]) { "Missing TLS pin" }
        require(validSha256Pin(pin)) { "Invalid TLS pin" }
        val code=requireNotNull(q["code"]) { "Missing pairing code" }
        require(codePattern.matches(code)) { "Invalid pairing code" }
        val version=q["v"]?.toIntOrNull() ?: GatewayContract.PROTOCOL_VERSION
        require(version==GatewayContract.PROTOCOL_VERSION) { "Unsupported protocol version" }
        return DesktopPairingLink(endpoint,pin,code,q["desktop"]?.take(80),version)
    }

    private fun validSha256Pin(pin:String):Boolean {
        if(!pin.startsWith("sha256/")) return false
        return runCatching { Base64.getDecoder().decode(pin.removePrefix("sha256/")).size==32 }.getOrDefault(false)
    }

    private fun query(raw:String?):Map<String,String> =
        raw.orEmpty().split('&').filter{it.isNotBlank()}.associate {
            val p=it.split('=',limit=2)
            decode(p[0]) to decode(p.getOrElse(1){""})
        }

    private fun decode(v:String)=URLDecoder.decode(v,StandardCharsets.UTF_8.name())
}
