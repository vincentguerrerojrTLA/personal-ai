package com.kylow.mobile.gateway

class ReplayGuard(private val maxEntries:Int=2048){
 private val seen=LinkedHashSet<String>()
 @Synchronized fun accept(nonce:String):Boolean { if(nonce.length<16||!seen.add(nonce)) return false; if(seen.size>maxEntries) seen.remove(seen.first()); return true }
}
