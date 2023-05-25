package org.opencds.cqf.cql.engine.fhir.data;


import org.hl7.fhirpath.TranslatorHelper;
import org.opencds.cqf.cql.engine.execution.CqlEngineVisitor;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.testng.annotations.Test;
import org.hl7.fhir.r4.model.*;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestPrimitiveProfiles extends FhirExecutionTestBase {

    @Test
    public void testProfileCast() {

        CqlEngineVisitor engineVisitor = TranslatorHelper.getEngineVisitor();
        engineVisitor.getState().registerDataProvider("http://hl7.org/fhir", r4Provider);
        EvaluationResult evaluationResult = engineVisitor.evaluate(library.getIdentifier(), getLibraryMap(),
                null, null, null, null, null);
        
        Object result;

        /*
        define UrlUri: FHIR.uri { value: 'http://example.org' }
        define CastToUrl: UrlUri as FHIR.url
        */
        result = evaluationResult.expressionResults.get("CastToUrl").value();
        assertThat(result, instanceOf(UrlType.class));
        assertThat(((UriType)result).getValue(), is("http://example.org"));

        /*
        define CanonicalUri: FHIR.uri { value: 'http://example.org/StructureDefinition/profile' }
        define CastToCanonical: CanonicalUri as FHIR.canonical
        */
        result = evaluationResult.expressionResults.get("CastToCanonical").value();
        assertThat(result, instanceOf(CanonicalType.class));
        assertThat(((CanonicalType)result).getValue(), is("http://example.org/StructureDefinition/profile"));

        /*
        define UuidUri: FHIR.uri { value: 'urn:uuid:d27ceea4-e506-42a4-8111-f01c003a11c4' }
        define CastToUuid: UuidUri as FHIR.uuid
        */
        result = evaluationResult.expressionResults.get("CastToUuid").value();
        assertThat(result, instanceOf(UuidType.class));
        assertThat(((UuidType)result).getValue(), is("urn:uuid:d27ceea4-e506-42a4-8111-f01c003a11c4"));

        /*
        define OidUri: FHIR.uri { value: 'urn:oid:2.16.840.1.113883.3.464.1004.1116' }
        define CastToOid: OidUri as FHIR.oid
        */
        result = evaluationResult.expressionResults.get("CastToOid").value();
        assertThat(result, instanceOf(OidType.class));
        assertThat(((OidType)result).getValue(), is("urn:oid:2.16.840.1.113883.3.464.1004.1116"));

        /*
        define PositiveInt: FHIR.integer { value: 12 }
        define CastToPositiveInt: PositiveInt as FHIR.positiveInt
        */
        result = evaluationResult.expressionResults.get("CastToPositiveInt").value();
        assertThat(result, instanceOf(PositiveIntType.class));
        assertThat(((PositiveIntType)result).getValue(), is(12));

        /*
        define UnsignedInt: FHIR.integer { value: 12 }
        define CastToUnsignedInt: UnsignedInt as FHIR.unsignedInt
        */
        result = evaluationResult.expressionResults.get("CastToUnsignedInt").value();
        assertThat(result, instanceOf(UnsignedIntType.class));
        assertThat(((UnsignedIntType)result).getValue(), is(12));

        /*
        define CodeString: FHIR.string { value: '12345' }
        define CastToCode: CodeString as FHIR.code
        */
        result = evaluationResult.expressionResults.get("CastToCode").value();
        assertThat(result, instanceOf(CodeType.class));
        assertThat(((CodeType)result).getValue(), is("12345"));

        /*
        define MarkdownString: FHIR.string { value: '# Markdown is [good](http://example.org)' }
        define CastToMarkdown: MarkdownString as FHIR.markdown
        */
        result = evaluationResult.expressionResults.get("CastToMarkdown").value();
        assertThat(result, instanceOf(MarkdownType.class));
        assertThat(((MarkdownType)result).getValue(), is("# Markdown is [good](http://example.org)"));

        /*
        define IdString: FHIR.string { value: 'fhir-string' }
        define CastToId: IdString as FHIR.id
        */
        result = evaluationResult.expressionResults.get("CastToId").value();
        assertThat(result, instanceOf(IdType.class));
        assertThat(((IdType)result).getValue(), is("fhir-string"));

        /*
        define SimpleQuantity: FHIR.Quantity { value: FHIR.decimal { value: 1.0 }, code: FHIR.code { value: 'mg' } }
        define CastToSimpleQuantity: FHIRQuantity as FHIR.SimpleQuantity
        */
        result = evaluationResult.expressionResults.get("CastToSimpleQuantity").value();
        assertThat(result, instanceOf(SimpleQuantity.class));
        assertThat(((SimpleQuantity)result).getValue().compareTo(new BigDecimal("1.0")), is(0));
        assertThat(((SimpleQuantity)result).getCode(), is("mg"));

        /*
        define AgeQuantity: FHIR.Quantity { value: FHIR.decimal { value: 10.0 }, code: FHIR.code { value: 'a' } }
        define CastToAge: AgeQuantity as FHIR.Age
        */
        result = evaluationResult.expressionResults.get("CastToAge").value();
        assertThat(result, instanceOf(Age.class));
        assertThat(((Age)result).getValue().compareTo(new BigDecimal("10.0")), is(0));
        assertThat(((Age)result).getCode(), is("a"));

        /*
        define DistanceQuantity: FHIR.Quantity { value: FHIR.decimal { value: 1200.0 }, code: FHIR.code { value: 'km' } }
        define CastToDistance: DistanceQuantity as FHIR.Distance
        */
        result = evaluationResult.expressionResults.get("CastToDistance").value();
        assertThat(result, instanceOf(Distance.class));
        assertThat(((Distance)result).getValue().compareTo(new BigDecimal("1200.0")), is(0));
        assertThat(((Distance)result).getCode(), is("km"));

        /*
        define DurationQuantity: FHIR.Quantity { value: FHIR.decimal { value: 12.0 }, code: FHIR.code { value: 'a' } }
        define CastToDuration: DurationQuantity as FHIR.Duration
        */
        result = evaluationResult.expressionResults.get("CastToDuration").value();
        assertThat(result, instanceOf(Duration.class));
        assertThat(((Duration)result).getValue().compareTo(new BigDecimal("12.0")), is(0));
        assertThat(((Duration)result).getCode(), is("a"));

        /*
        define CountQuantity: FHIR.Quantity { value: FHIR.decimal { value: 100 }, code: FHIR.code { value: '1' } }
        define CastToCount: CountQuantity as FHIR.Count
        */
        result = evaluationResult.expressionResults.get("CastToCount").value();
        assertThat(result, instanceOf(Count.class));
        assertThat(((Count)result).getValue().compareTo(new BigDecimal("100")), is(0));
        assertThat(((Count)result).getCode(), is("1"));

        /*
        define MoneyQuantity: FHIR.Quantity { value: FHIR.decimal { value: 12000.00 }, code: FHIR.code { value: '$' } }
        define CastToMoney: MoneyQuantity as FHIR.MoneyQuantity
        */
        result = evaluationResult.expressionResults.get("CastToMoney").value();
        assertThat(result, instanceOf(MoneyQuantity.class));
        assertThat(((MoneyQuantity)result).getValue().compareTo(new BigDecimal("12000.00")), is(0));
        assertThat(((MoneyQuantity)result).getCode(), is("$"));
    }
}
