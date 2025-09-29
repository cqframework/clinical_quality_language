package org.cqframework.fhir.utilities

import java.net.URISyntaxException
import org.cqframework.fhir.utilities.Uris.parseOrNull
import org.hl7.fhir.r5.context.ILoggingService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestIGContext : ILoggingService {
    @Test
    @Throws(URISyntaxException::class)
    fun typesAndValuesIG() {
        val url = TestIGContext::class.java.getResource("types-and-values/ig.ini")
        Assertions.assertNotNull(url)
        val uri = parseOrNull(url!!.toURI().toString())
        Assertions.assertNotNull(uri)
        val path = uri!!.getSchemeSpecificPart()
        val igContext = IGContext(this)
        igContext.initializeFromIni(path)
        Assertions.assertEquals("fhir.cqf.typesandvalues", igContext.packageId)
        Assertions.assertEquals(
            "http://fhir.org/guides/cqf/typesandvalues",
            igContext.canonicalBase,
        )
    }

    override fun logMessage(s: String?) {
        println(s)
    }

    override fun logDebugMessage(logCategory: ILoggingService.LogCategory, s: String?) {
        println(String.format("%s: %s", logCategory.toString(), s))
    }

    @Deprecated("Deprecated in FHIR core")
    override fun isDebugLogging(): Boolean {
        return true
    }
}
