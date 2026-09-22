package com.kylow.mobile.memory
import org.junit.Assert.*
import org.junit.Test
class PrivateMemoryTest{
 @Test fun isolatesOwners(){ val m=PrivateMemory("a"); try{m.put(MemoryItem("b","1","secret",true));fail()}catch(_:IllegalArgumentException){} }
 @Test fun requiresConsent(){ val m=PrivateMemory("a"); try{m.put(MemoryItem("a","1","x",false));fail()}catch(_:IllegalArgumentException){} }
}
