package org.opencds.cqf.cql.engine.fhir.data

import java.math.BigDecimal
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hl7.fhir.r4.model.Age
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.Count
import org.hl7.fhir.r4.model.Distance
import org.hl7.fhir.r4.model.Duration
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.MarkdownType
import org.hl7.fhir.r4.model.MoneyQuantity
import org.hl7.fhir.r4.model.OidType
import org.hl7.fhir.r4.model.PositiveIntType
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.UnsignedIntType
import org.hl7.fhir.r4.model.UriType
import org.hl7.fhir.r4.model.UrlType
import org.hl7.fhir.r4.model.UuidType
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
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(UrlType::class.java))
        MatcherAssert.assertThat((value as UriType).value, CoreMatchers.`is`("http://example.org"))

        /*
        define CanonicalUri: FHIR.uri { value: 'http://example.org/StructureDefinition/profile' }
        define CastToCanonical: CanonicalUri as FHIR.canonical
        */
        value = results["CastToCanonical"]!!.value
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(CanonicalType::class.java))
        MatcherAssert.assertThat(
            (value as CanonicalType).value,
            CoreMatchers.`is`("http://example.org/StructureDefinition/profile"),
        )

        /*
        define UuidUri: FHIR.uri { value: 'urn:uuid:d27ceea4-e506-42a4-8111-f01c003a11c4' }
        define CastToUuid: UuidUri as FHIR.uuid
        */
        value = results["CastToUuid"]!!.value
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(UuidType::class.java))
        MatcherAssert.assertThat(
            (value as UuidType).value,
            CoreMatchers.`is`("urn:uuid:d27ceea4-e506-42a4-8111-f01c003a11c4"),
        )

        /*
        define OidUri: FHIR.uri { value: 'urn:oid:2.16.840.1.113883.3.464.1004.1116' }
        define CastToOid: OidUri as FHIR.oid
        */
        value = results["CastToOid"]!!.value
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(OidType::class.java))
        MatcherAssert.assertThat(
            (value as OidType).value,
            CoreMatchers.`is`("urn:oid:2.16.840.1.113883.3.464.1004.1116"),
        )

        /*
        define PositiveInt: FHIR.integer { value: 12 }
        define CastToPositiveInt: PositiveInt as FHIR.positiveInt
        */
        value = results["CastToPositiveInt"]!!.value
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(PositiveIntType::class.java))
        MatcherAssert.assertThat((value as PositiveIntType).value, CoreMatchers.`is`(12))

        /*
        define UnsignedInt: FHIR.integer { value: 12 }
        define CastToUnsignedInt: UnsignedInt as FHIR.unsignedInt
        */
        value = results["CastToUnsignedInt"]!!.value
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(UnsignedIntType::class.java))
        MatcherAssert.assertThat((value as UnsignedIntType).value, CoreMatchers.`is`(12))

        /*
        define CodeString: FHIR.string { value: '12345' }
        define CastToCode: CodeString as FHIR.code
        */
        value = results["CastToCode"]!!.value
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(CodeType::class.java))
        MatcherAssert.assertThat((value as CodeType).value, CoreMatchers.`is`("12345"))

        /*
        define MarkdownString: FHIR.string { value: '# Markdown is [good](http://example.org)' }
        define CastToMarkdown: MarkdownString as FHIR.markdown
        */
        value = results["CastToMarkdown"]!!.value
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(MarkdownType::class.java))
        MatcherAssert.assertThat(
            (value as MarkdownType).value,
            CoreMatchers.`is`("# Markdown is [good](http://example.org)"),
        )

        /*
        define IdString: FHIR.string { value: 'fhir-string' }
        define CastToId: IdString as FHIR.id
        */
        value = results["CastToId"]!!.value
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(IdType::class.java))
        MatcherAssert.assertThat((value as IdType).value, CoreMatchers.`is`("fhir-string"))

        /*
        define SimpleQuantity: FHIR.Quantity { value: FHIR.decimal { value: 1.0 }, code: FHIR.code { value: 'mg' } }
        define CastToSimpleQuantity: FHIRQuantity as FHIR.SimpleQuantity
        */
        value = results["CastToSimpleQuantity"]!!.value
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(SimpleQuantity::class.java))
        MatcherAssert.assertThat(
            (value as SimpleQuantity).getValue().compareTo(BigDecimal("1.0")),
            CoreMatchers.`is`(0),
        )
        MatcherAssert.assertThat(value.getCode(), CoreMatchers.`is`("mg"))

        /*
        define AgeQuantity: FHIR.Quantity { value: FHIR.decimal { value: 10.0 }, code: FHIR.code { value: 'a' } }
        define CastToAge: AgeQuantity as FHIR.Age
        */
        value = results["CastToAge"]!!.value
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Age::class.java))
        MatcherAssert.assertThat(
            (value as Age).getValue().compareTo(BigDecimal("10.0")),
            CoreMatchers.`is`(0),
        )
        MatcherAssert.assertThat(value.getCode(), CoreMatchers.`is`("a"))

        /*
        define DistanceQuantity: FHIR.Quantity { value: FHIR.decimal { value: 1200.0 }, code: FHIR.code { value: 'km' } }
        define CastToDistance: DistanceQuantity as FHIR.Distance
        */
        value = results["CastToDistance"]!!.value
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Distance::class.java))
        MatcherAssert.assertThat(
            (value as Distance).getValue().compareTo(BigDecimal("1200.0")),
            CoreMatchers.`is`(0),
        )
        MatcherAssert.assertThat(value.getCode(), CoreMatchers.`is`("km"))

        /*
        define DurationQuantity: FHIR.Quantity { value: FHIR.decimal { value: 12.0 }, code: FHIR.code { value: 'a' } }
        define CastToDuration: DurationQuantity as FHIR.Duration
        */
        value = results["CastToDuration"]!!.value
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Duration::class.java))
        MatcherAssert.assertThat(
            (value as Duration).getValue().compareTo(BigDecimal("12.0")),
            CoreMatchers.`is`(0),
        )
        MatcherAssert.assertThat(value.getCode(), CoreMatchers.`is`("a"))

        /*
        define CountQuantity: FHIR.Quantity { value: FHIR.decimal { value: 100 }, code: FHIR.code { value: '1' } }
        define CastToCount: CountQuantity as FHIR.Count
        */
        value = results["CastToCount"]!!.value
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Count::class.java))
        MatcherAssert.assertThat(
            (value as Count).getValue().compareTo(BigDecimal("100")),
            CoreMatchers.`is`(0),
        )
        MatcherAssert.assertThat(value.getCode(), CoreMatchers.`is`("1"))

        /*
        define MoneyQuantity: FHIR.Quantity { value: FHIR.decimal { value: 12000.00 }, code: FHIR.code { value: '$' } }
        define CastToMoney: MoneyQuantity as FHIR.MoneyQuantity
        */
        value = results["CastToMoney"]!!.value
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(MoneyQuantity::class.java))
        MatcherAssert.assertThat(
            (value as MoneyQuantity).getValue().compareTo(BigDecimal("12000.00")),
            CoreMatchers.`is`(0),
        )
        MatcherAssert.assertThat(value.getCode(), CoreMatchers.`is`("$"))
    }
}
