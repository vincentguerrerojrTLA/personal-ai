package com.kylow.mobile.audit

import java.time.Instant

data class AuditEvent(val action:String,val result:String,val at:Instant=Instant.now())
object AuditLog {
 private val events=mutableListOf<AuditEvent>()
 fun record(action:String,result:String){ events+=AuditEvent(action,result) }
 fun snapshot()=events.toList()
}
