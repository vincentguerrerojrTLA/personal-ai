package com.kylow.mobile.audit

object AuditRetention{
 const val MAX_EVENTS=5000
 fun trim(events:List<AuditEvent>)=if(events.size<=MAX_EVENTS)events else events.takeLast(MAX_EVENTS)
}
