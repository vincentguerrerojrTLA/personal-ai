package com.kylow.mobile.audit
import com.kylow.mobile.data.DataClass
import com.kylow.mobile.data.DataClassifier

object SafeAudit {
 fun safeValue(tag:String,value:String):String = if(DataClassifier.classify(tag)==DataClass.PUBLIC) value else "[REDACTED]"
 fun record(action:String,result:String){ AuditLog.record(action,result.take(120)) }
}
