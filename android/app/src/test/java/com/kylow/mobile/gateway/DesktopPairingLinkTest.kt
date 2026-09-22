package com.kylow.mobile.gateway

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

class DesktopPairingLinkTest {
    private val pin="sha256/"+Base64.getEncoder().encodeToString(ByteArray(32){7})

    @Test fun parsesPinnedHttpsPairingLink(){
        val endpoint=enc("https://192.168.1.44:8765")
        val link=DesktopPairingLinkParser.parse("kylow://pair?endpoint=$endpoint&pin=${enc(pin)}&code=ABCDEF123456&desktop=Office-PC&v=1")
        assertEquals("https://192.168.1.44:8765",link.endpoint)
        assertEquals(pin,link.tlsPin)
        assertEquals("ABCDEF123456",link.pairingCode)
        assertEquals("Office-PC",link.desktopName)
    }

    @Test fun rejectsCleartextEndpoint(){
        assertThrows(IllegalArgumentException::class.java){
            DesktopPairingLinkParser.parse("kylow://pair?endpoint=${enc("http://192.168.1.44:8765")}&pin=${enc(pin)}&code=ABCDEF123456")
        }
    }

    @Test fun rejectsMissingOrWeakPairingCode(){
        assertThrows(IllegalArgumentException::class.java){
            DesktopPairingLinkParser.parse("kylow://pair?endpoint=${enc("https://192.168.1.44:8765")}&pin=${enc(pin)}&code=1234")
        }
    }

    @Test fun rejectsBadPin(){
        assertThrows(IllegalArgumentException::class.java){
            DesktopPairingLinkParser.parse("kylow://pair?endpoint=${enc("https://192.168.1.44:8765")}&pin=${enc("sha256/bad")}&code=ABCDEF123456")
        }
    }

    @Test fun rejectsUnknownProtocol(){
        assertThrows(IllegalArgumentException::class.java){
            DesktopPairingLinkParser.parse("kylow://pair?endpoint=${enc("https://192.168.1.44:8765")}&pin=${enc(pin)}&code=ABCDEF123456&v=2")
        }
    }

    private fun enc(v:String)=URLEncoder.encode(v,StandardCharsets.UTF_8.name())
}
