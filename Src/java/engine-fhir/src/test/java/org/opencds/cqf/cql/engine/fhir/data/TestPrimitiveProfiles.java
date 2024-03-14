package org.opencds.cqf.cql.engine.fhir.data;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import org.hl7.fhir.r4.model.*;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.testng.annotations.Test;

public class TestPrimitiveProfiles extends FhirExecutionTestBase {

    @Test
    public void testProfileCast() {

        CqlEngine engine = getEngine();
        engine.getEnvironment().registerDataProvider("http://hl7.org/fhir", r4Provider);
        var results = engine.evaluate(library.getIdentifier());

        /*
        define UrlUri: FHIR.uri { value: 'http://example.org' }
        define CastToUrl: UrlUri as FHIR.url
        */
        var value = results.forExpression("CastToUrl").value();
        assertThat(value, instanceOf(UrlType.class));
        assertThat(((UriType) value).getValue(), is("http://example.org"));

        /*
        define CanonicalUri: FHIR.uri { value: 'http://example.org/StructureDefinition/profile' }
        define CastToCanonical: CanonicalUri as FHIR.canonical
        */
        value = results.forExpression("CastToCanonical").value();
        assertThat(value, instanceOf(CanonicalType.class));
        assertThat(((CanonicalType) value).getValue(), is("http://example.org/StructureDefinition/profile"));

        /*
        define UuidUri: FHIR.uri { value: 'urn:uuid:d27ceea4-e506-42a4-8111-f01c003a11c4' }
        define CastToUuid: UuidUri as FHIR.uuid
        */
        value = results.forExpression("CastToUuid").value();
        assertThat(value, instanceOf(UuidType.class));
        assertThat(((UuidType) value).getValue(), is("urn:uuid:d27ceea4-e506-42a4-8111-f01c003a11c4"));

        /*
        define OidUri: FHIR.uri { value: 'urn:oid:2.16.840.1.113883.3.464.1004.1116' }
        define CastToOid: OidUri as FHIR.oid
        */
        value = results.forExpression("CastToOid").value();
        assertThat(value, instanceOf(OidType.class));
        assertThat(((OidType) value).getValue(), is("urn:oid:2.16.840.1.113883.3.464.1004.1116"));

        /*
        define PositiveInt: FHIR.integer { value: 12 }
        define CastToPositiveInt: PositiveInt as FHIR.positiveInt
        */
        value = results.forExpression("CastToPositiveInt").value();
        assertThat(value, instanceOf(PositiveIntType.class));
        assertThat(((PositiveIntType) value).getValue(), is(12));

        /*
        define UnsignedInt: FHIR.integer { value: 12 }
        define CastToUnsignedInt: UnsignedInt as FHIR.unsignedInt
        */
        value = results.forExpression("CastToUnsignedInt").value();
        assertThat(value, instanceOf(UnsignedIntType.class));
        assertThat(((UnsignedIntType) value).getValue(), is(12));

        /*
        define CodeString: FHIR.string { value: '12345' }
        define CastToCode: CodeString as FHIR.code
        */
        value = results.forExpression("CastToCode").value();
        assertThat(value, instanceOf(CodeType.class));
        assertThat(((CodeType) value).getValue(), is("12345"));

        /*
        define MarkdownString: FHIR.string { value: '# Markdown is [good](http://example.org)' }
        define CastToMarkdown: MarkdownString as FHIR.markdown
        */
        value = results.forExpression("CastToMarkdown").value();
        assertThat(value, instanceOf(MarkdownType.class));
        assertThat(((MarkdownType) value).getValue(), is("# Markdown is [good](http://example.org)"));

        /*
        define IdString: FHIR.string { value: 'fhir-string' }
        define CastToId: IdString as FHIR.id
        */
        value = results.forExpression("CastToId").value();
        assertThat(value, instanceOf(IdType.class));
        assertThat(((IdType) value).getValue(), is("fhir-string"));

        /*
        define SimpleQuantity: FHIR.Quantity { value: FHIR.decimal { value: 1.0 }, code: FHIR.code { value: 'mg' } }
        define CastToSimpleQuantity: FHIRQuantity as FHIR.SimpleQuantity
        */
        value = results.forExpression("CastToSimpleQuantity").value();
        assertThat(value, instanceOf(SimpleQuantity.class));
        assertThat(((SimpleQuantity) value).getValue().compareTo(new BigDecimal("1.0")), is(0));
        assertThat(((SimpleQuantity) value).getCode(), is("mg"));

        /*
        define AgeQuantity: FHIR.Quantity { value: FHIR.decimal { value: 10.0 }, code: FHIR.code { value: 'a' } }
        define CastToAge: AgeQuantity as FHIR.Age
        */
        value = results.forExpression("CastToAge").value();
        assertThat(value, instanceOf(Age.class));
        assertThat(((Age) value).getValue().compareTo(new BigDecimal("10.0")), is(0));
        assertThat(((Age) value).getCode(), is("a"));

        /*
        define DistanceQuantity: FHIR.Quantity { value: FHIR.decimal { value: 1200.0 }, code: FHIR.code { value: 'km' } }
        define CastToDistance: DistanceQuantity as FHIR.Distance
        */
        value = results.forExpression("CastToDistance").value();
        assertThat(value, instanceOf(Distance.class));
        assertThat(((Distance) value).getValue().compareTo(new BigDecimal("1200.0")), is(0));
        assertThat(((Distance) value).getCode(), is("km"));

        /*
        define DurationQuantity: FHIR.Quantity { value: FHIR.decimal { value: 12.0 }, code: FHIR.code { value: 'a' } }
        define CastToDuration: DurationQuantity as FHIR.Duration
        */
        value = results.forExpression("CastToDuration").value();
        assertThat(value, instanceOf(Duration.class));
        assertThat(((Duration) value).getValue().compareTo(new BigDecimal("12.0")), is(0));
        assertThat(((Duration) value).getCode(), is("a"));

        /*
        define CountQuantity: FHIR.Quantity { value: FHIR.decimal { value: 100 }, code: FHIR.code { value: '1' } }
        define CastToCount: CountQuantity as FHIR.Count
        */
        value = results.forExpression("CastToCount").value();
        assertThat(value, instanceOf(Count.class));
        assertThat(((Count) value).getValue().compareTo(new BigDecimal("100")), is(0));
        assertThat(((Count) value).getCode(), is("1"));

        /*
        define MoneyQuantity: FHIR.Quantity { value: FHIR.decimal { value: 12000.00 }, code: FHIR.code { value: '$' } }
        define CastToMoney: MoneyQuantity as FHIR.MoneyQuantity
        */
        value = results.forExpression("CastToMoney").value();
        assertThat(value, instanceOf(MoneyQuantity.class));
        assertThat(((MoneyQuantity) value).getValue().compareTo(new BigDecimal("12000.00")), is(0));
        assertThat(((MoneyQuantity) value).getCode(), is("$"));
    }
}
