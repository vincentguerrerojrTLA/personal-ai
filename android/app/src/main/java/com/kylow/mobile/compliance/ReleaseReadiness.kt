package com.kylow.mobile.compliance
data class ReleaseEvidence(val tests:Boolean,val privacy:Boolean,val security:Boolean,val safety:Boolean,val licenses:Boolean,val incidentControls:Boolean)
object ReleaseReadiness{
 fun failures(e:ReleaseEvidence)=buildList{if(!e.tests)add("tests");if(!e.privacy)add("privacy");if(!e.security)add("security");if(!e.safety)add("safety");if(!e.licenses)add("licenses");if(!e.incidentControls)add("incident_controls")}
 fun allowed(e:ReleaseEvidence)=failures(e).isEmpty()
}
