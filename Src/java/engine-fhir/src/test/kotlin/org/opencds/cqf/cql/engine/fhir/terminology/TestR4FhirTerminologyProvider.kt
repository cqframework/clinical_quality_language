package org.opencds.cqf.cql.engine.fhir.terminology

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.stream.Collectors
import java.util.stream.StreamSupport
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.ValueSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.exception.TerminologyProviderException
import org.opencds.cqf.cql.engine.fhir.R4FhirTest
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo

internal class TestR4FhirTerminologyProvider : R4FhirTest() {
    var provider: R4FhirTerminologyProvider? = null

    @BeforeEach
    fun initializeProvider() {
        provider = R4FhirTerminologyProvider(newClient())
    }

    @Test
    @Throws(Exception::class)
    fun resolveByUrlUsingUrlSucceeds() {
        val info = ValueSetInfo().withId("https://cts.nlm.nih.gov/fhir/ValueSet/1.2.3.4")

        val response = ValueSet()
        response.setId("1.2.3.4")
        response.setUrl(info.id)

        mockResolveSearchPath(info, response)

        val id = provider!!.resolveValueSetId(info)

        Assertions.assertEquals(id, response.getId())
    }

    @Test
    @Throws(Exception::class)
    fun resolveByUrlUsingIdentifierSucceeds() {
        val info = ValueSetInfo().withId("urn:oid:1.2.3.4")

        val response = ValueSet()
        response.setId("1.2.3.4")
        response.addIdentifier().setValue(info.id)

        mockResolveSearchPath(info, response)

        val id = provider!!.resolveValueSetId(info)

        Assertions.assertEquals(id, response.getId())
    }

    @Test
    @Throws(Exception::class)
    fun resolveByUrlUsingResourceIdSucceeds() {
        val info = ValueSetInfo().withId("1.2.3.4")

        val response = ValueSet()
        response.setId("1.2.3.4")

        mockResolveSearchPath(info, response)

        val id = provider!!.resolveValueSetId(info)

        Assertions.assertEquals(id, response.getId())
    }

    @Test
    @Throws(Exception::class)
    fun resolveByUrlNoMatchesThrowsException() {
        val info = ValueSetInfo().withId("urn:oid:1.2.3.4")

        mockResolveSearchPath(info, null)

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            provider!!.resolveValueSetId(info)
        }
    }

    @Test
    @Throws(Exception::class)
    fun expandByUrlNoMatchesThrowsException() {
        val info = ValueSetInfo().withId("urn:oid:1.2.3.4")

        mockResolveSearchPath(info, null)

        Assertions.assertThrows(TerminologyProviderException::class.java) {
            provider!!.expand(info)
        }
    }

    @Test
    fun nonNullVersionUnsupported() {
        val info = ValueSetInfo()
        info.id = "urn:oid:Test"
        info.version = "1.0.0."

        Assertions.assertThrows(UnsupportedOperationException::class.java) {
            provider!!.resolveValueSetId(info)
        }
    }

    @Test
    fun nonNullCodesystemsUnsupported() {
        val codeSystem = CodeSystemInfo()
        codeSystem.id = "SNOMED-CT"
        codeSystem.version = "2013-09"

        val info = ValueSetInfo()
        info.id = "urn:oid:Test"
        info.getCodeSystems().add(codeSystem)

        Assertions.assertThrows(UnsupportedOperationException::class.java) {
            provider!!.resolveValueSetId(info)
        }
    }

    @Test
    @Throws(Exception::class)
    fun urnOidPrefixIsStripped() {
        val info = ValueSetInfo()
        info.id = "urn:oid:Test"

        val valueSet = ValueSet()
        valueSet.setId("Test")
        valueSet.getExpansion().containsFirstRep.setSystem(TEST_SYSTEM).setCode(TEST_CODE)

        mockResolveSearchPath(info, valueSet)

        val id = provider!!.resolveValueSetId(info)
        Assertions.assertEquals("Test", id)
    }

    @Test
    @Throws(Exception::class)
    fun moreThanOneURLSearchResultIsError() {
        val info = ValueSetInfo()
        info.id = "http://localhost/fhir/ValueSet/1.2.3.4"

        val firstSet = ValueSet()
        firstSet.setId("1")
        firstSet.setUrl(info.id)

        val secondSet = ValueSet()
        secondSet.setId("1")
        secondSet.setUrl(info.id)

        mockFhirSearch("/ValueSet?url=" + urlEncode(info.id), firstSet, secondSet)

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            provider!!.resolveValueSetId(info)
        }
    }

    @Test
    @Throws(Exception::class)
    fun zeroURLSearchResultIsError() {
        val info = ValueSetInfo()
        info.id = "http://localhost/fhir/ValueSet/1.2.3.4"

        mockResolveSearchPath(info, null)

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            provider!!.resolveValueSetId(info)
        }
    }

    @Test
    @Throws(Exception::class)
    fun expandOperationReturnsCorrectCodesMoreThanZero() {
        val info = ValueSetInfo()
        info.id = "urn:oid:Test"

        val valueSet = ValueSet()
        valueSet.setId("Test")
        valueSet.getExpansion().containsFirstRep.setSystem(TEST_SYSTEM).setCode(TEST_CODE)

        mockResolveSearchPath(info, valueSet)

        val parameters = Parameters()
        parameters.parameterFirstRep.setName("return").setResource(valueSet)

        mockFhirRead($$"/ValueSet/Test/$expand", parameters)

        val codes = provider!!.expand(info)

        val list = StreamSupport.stream(codes.spliterator(), false).collect(Collectors.toList())
        Assertions.assertEquals(1, list.size)
        Assertions.assertEquals(TEST_SYSTEM, list[0]!!.system)
        Assertions.assertEquals(TEST_CODE, list[0]!!.code)
    }

    @Test
    @Throws(Exception::class)
    fun inOperationReturnsTrueWhenFhirReturnsTrue() {
        val info = ValueSetInfo()
        info.id = "urn:oid:Test"

        val valueSet = ValueSet()
        valueSet.setId("Test")
        valueSet.getExpansion().containsFirstRep.setSystem(TEST_SYSTEM).setCode(TEST_CODE)

        mockResolveSearchPath(info, valueSet)

        val code = Code()
        code.system = TEST_SYSTEM
        code.code = TEST_CODE
        code.display = TEST_DISPLAY

        val parameters = Parameters()
        parameters.parameterFirstRep.setName("result").setValue(BooleanType(true))

        mockFhirRead(
            ($$"/ValueSet/Test/$validate-code?code=" +
                urlEncode(code.code) +
                "&system=" +
                urlEncode(code.system)),
            parameters,
        )

        val result = provider!!.`in`(code, info)
        Assertions.assertTrue(result)
    }

    @Test
    @Throws(Exception::class)
    fun inOperationReturnsFalseWhenFhirReturnsFalse() {
        val info = ValueSetInfo()
        info.id = "urn:oid:Test"

        val valueSet = ValueSet()
        valueSet.setId("Test")

        mockResolveSearchPath(info, valueSet)

        val code = Code()
        code.system = TEST_SYSTEM
        code.code = TEST_CODE
        code.display = TEST_DISPLAY

        val parameters = Parameters()
        parameters.parameterFirstRep.setName("result").setValue(BooleanType(false))

        mockFhirRead(
            ($$"/ValueSet/Test/$validate-code?code=" +
                urlEncode(code.code) +
                "&system=" +
                urlEncode(code.system)),
            parameters,
        )

        val result = provider!!.`in`(code, info)
        Assertions.assertFalse(result)
    }

    @Test
    @Throws(Exception::class)
    fun inOperationHandlesNullSystem() {
        val info = ValueSetInfo()
        info.id = "urn:oid:Test"

        val valueSet = ValueSet()
        valueSet.setId("Test")

        mockResolveSearchPath(info, valueSet)

        val code = Code()
        code.code = TEST_CODE
        code.display = TEST_DISPLAY

        val parameters = Parameters()
        parameters.parameterFirstRep.setName("result").setValue(BooleanType(true))

        mockFhirRead($$"/ValueSet/Test/$validate-code?code=" + urlEncode(code.code), parameters)

        val result = provider!!.`in`(code, info)
        Assertions.assertTrue(result)
    }

    @Test
    @Throws(Exception::class)
    fun lookupOperationSuccess() {
        val info = CodeSystemInfo()
        info.id = TEST_SYSTEM
        info.version = TEST_SYSTEM_VERSION

        val code = Code()
        code.code = TEST_CODE
        code.system = TEST_SYSTEM
        code.display = TEST_DISPLAY

        val parameters = Parameters()
        parameters.addParameter().setName("name").setValue(StringType(code.code))
        parameters.addParameter().setName("version").setValue(StringType(info.version))
        parameters.addParameter().setName("display").setValue(StringType(code.display))

        mockFhirPost($$"/CodeSystem/$lookup", parameters)

        val result = provider!!.lookup(code, info)
        Assertions.assertNotNull(result)
        Assertions.assertEquals(result!!.system, code.system)
        Assertions.assertEquals(result.code, code.code)
        Assertions.assertEquals(result.display, code.display)
    }

    @Throws(UnsupportedEncodingException::class)
    private fun urlEncode(value: String): String? {
        return URLEncoder.encode(value, "utf-8")
    }

    @Throws(UnsupportedEncodingException::class)
    private fun mockResolveSearchPath(info: ValueSetInfo, valueSet: ValueSet?) {
        if (valueSet != null && valueSet.getUrl() != null) {
            mockFhirSearch("/ValueSet?url=" + urlEncode(info.id), valueSet)
        } else {
            mockFhirSearch("/ValueSet?url=" + urlEncode(info.id))
        }

        if (valueSet != null && valueSet.getIdentifier().isNotEmpty()) {
            mockFhirSearch("/ValueSet?identifier=" + urlEncode(info.id), valueSet)
        } else {
            mockFhirSearch("/ValueSet?identifier=" + urlEncode(info.id))
        }

        if (valueSet != null) {
            mockFhirRead("/ValueSet/" + valueSet.getId(), valueSet)
        } else {
            val parts: Array<String?> =
                info.id.split("[:/]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val expectedId = parts[parts.size - 1]
            mockNotFound("/ValueSet/$expectedId")
        }
    }

    companion object {
        private const val TEST_DISPLAY = "Display"
        private const val TEST_CODE = "425178004"
        private const val TEST_SYSTEM = "http://snomed.info/sct"
        private const val TEST_SYSTEM_VERSION = "2013-09"
    }
}
