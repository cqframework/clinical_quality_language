package org.opencds.cqf.cql.engine.fhir.data

import kotlin.test.assertNull
import org.junit.jupiter.api.Test

internal class TestPrimitiveProfiles : FhirExecutionTestBase() {
    @Test
    fun profileCast() {
        engine.environment.registerDataProvider("http://hl7.org/fhir", r4Provider)
        val results = engine.evaluate { library(library!!.identifier!!) }.onlyResultOrThrow

        /*
        define UrlUri: FHIR.uri { value: 'http://example.org' }
        define CastToUrl: UrlUri as FHIR.url
        */
        var value = results["CastToUrl"]!!.value
        assertNull(value)

        /*
        define CanonicalUri: FHIR.uri { value: 'http://example.org/StructureDefinition/profile' }
        define CastToCanonical: CanonicalUri as FHIR.canonical
        */
        value = results["CastToCanonical"]!!.value
        assertNull(value)

        /*
        define UuidUri: FHIR.uri { value: 'urn:uuid:d27ceea4-e506-42a4-8111-f01c003a11c4' }
        define CastToUuid: UuidUri as FHIR.uuid
        */
        value = results["CastToUuid"]!!.value
        assertNull(value)

        /*
        define OidUri: FHIR.uri { value: 'urn:oid:2.16.840.1.113883.3.464.1004.1116' }
        define CastToOid: OidUri as FHIR.oid
        */
        value = results["CastToOid"]!!.value
        assertNull(value)

        /*
        define PositiveInt: FHIR.integer { value: 12 }
        define CastToPositiveInt: PositiveInt as FHIR.positiveInt
        */
        value = results["CastToPositiveInt"]!!.value
        assertNull(value)

        /*
        define UnsignedInt: FHIR.integer { value: 12 }
        define CastToUnsignedInt: UnsignedInt as FHIR.unsignedInt
        */
        value = results["CastToUnsignedInt"]!!.value
        assertNull(value)

        /*
        define CodeString: FHIR.string { value: '12345' }
        define CastToCode: CodeString as FHIR.code
        */
        value = results["CastToCode"]!!.value
        assertNull(value)

        /*
        define MarkdownString: FHIR.string { value: '# Markdown is [good](http://example.org)' }
        define CastToMarkdown: MarkdownString as FHIR.markdown
        */
        value = results["CastToMarkdown"]!!.value
        assertNull(value)

        /*
        define IdString: FHIR.string { value: 'fhir-string' }
        define CastToId: IdString as FHIR.id
        */
        value = results["CastToId"]!!.value
        assertNull(value)

        /*
        define SimpleQuantity: FHIR.Quantity { value: FHIR.decimal { value: 1.0 }, code: FHIR.code { value: 'mg' } }
        define CastToSimpleQuantity: FHIRQuantity as FHIR.SimpleQuantity
        */
        value = results["CastToSimpleQuantity"]!!.value
        assertNull(value)

        /*
        define AgeQuantity: FHIR.Quantity { value: FHIR.decimal { value: 10.0 }, code: FHIR.code { value: 'a' } }
        define CastToAge: AgeQuantity as FHIR.Age
        */
        value = results["CastToAge"]!!.value
        assertNull(value)

        /*
        define DistanceQuantity: FHIR.Quantity { value: FHIR.decimal { value: 1200.0 }, code: FHIR.code { value: 'km' } }
        define CastToDistance: DistanceQuantity as FHIR.Distance
        */
        value = results["CastToDistance"]!!.value
        assertNull(value)

        /*
        define DurationQuantity: FHIR.Quantity { value: FHIR.decimal { value: 12.0 }, code: FHIR.code { value: 'a' } }
        define CastToDuration: DurationQuantity as FHIR.Duration
        */
        value = results["CastToDuration"]!!.value
        assertNull(value)

        /*
        define CountQuantity: FHIR.Quantity { value: FHIR.decimal { value: 100 }, code: FHIR.code { value: '1' } }
        define CastToCount: CountQuantity as FHIR.Count
        */
        value = results["CastToCount"]!!.value
        assertNull(value)

        /*
        define MoneyQuantity: FHIR.Quantity { value: FHIR.decimal { value: 12000.00 }, code: FHIR.code { value: '$' } }
        define CastToMoney: MoneyQuantity as FHIR.MoneyQuantity
        */
        value = results["CastToMoney"]!!.value
        assertNull(value)
    }
}
