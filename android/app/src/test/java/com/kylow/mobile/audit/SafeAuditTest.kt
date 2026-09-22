package com.kylow.mobile.audit
import org.junit.Assert.*
import org.junit.Test
class SafeAuditTest{@Test fun secretsNeverEnterLogs(){assertEquals("[REDACTED]",SafeAudit.safeValue("password","hunter2"));assertEquals("[REDACTED]",SafeAudit.safeValue("health","private"))}}
