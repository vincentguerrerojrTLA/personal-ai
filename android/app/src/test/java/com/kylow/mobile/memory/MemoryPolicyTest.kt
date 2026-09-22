package com.kylow.mobile.memory
import org.junit.Assert.*
import org.junit.Test
class MemoryPolicyTest{
 @Test fun consentRequired(){assertFalse(MemoryPolicy.mayStore("u","1","x",false))}
 @Test fun boundedItemAccepted(){assertTrue(MemoryPolicy.mayStore("u","1","hello",true))}
 @Test fun oversizedItemRejected(){assertFalse(MemoryPolicy.mayStore("u","1","x".repeat(MemoryPolicy.MAX_ITEM_CHARS+1),true))}
}
