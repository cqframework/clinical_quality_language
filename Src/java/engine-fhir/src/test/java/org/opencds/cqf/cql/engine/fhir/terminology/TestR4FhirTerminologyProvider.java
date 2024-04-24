package org.opencds.cqf.cql.engine.fhir.terminology;

import static org.junit.jupiter.api.Assertions.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.ValueSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.exception.TerminologyProviderException;
import org.opencds.cqf.cql.engine.fhir.R4FhirTest;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;

class TestR4FhirTerminologyProvider extends R4FhirTest {

    private static final String TEST_DISPLAY = "Display";
    private static final String TEST_CODE = "425178004";
    private static final String TEST_SYSTEM = "http://snomed.info/sct";
    private static final String TEST_SYSTEM_VERSION = "2013-09";
    R4FhirTerminologyProvider provider;

    @BeforeEach
    void initializeProvider() {
        provider = new R4FhirTerminologyProvider(newClient());
    }

    @Test
    void resolveByUrlUsingUrlSucceeds() throws Exception {
        ValueSetInfo info = new ValueSetInfo().withId("https://cts.nlm.nih.gov/fhir/ValueSet/1.2.3.4");

        ValueSet response = new ValueSet();
        response.setId("1.2.3.4");
        response.setUrl(info.getId());

        mockResolveSearchPath(info, response);

        String id = provider.resolveValueSetId(info);

        assertEquals(id, response.getId());
    }

    @Test
    void resolveByUrlUsingIdentifierSucceeds() throws Exception {
        ValueSetInfo info = new ValueSetInfo().withId("urn:oid:1.2.3.4");

        ValueSet response = new ValueSet();
        response.setId("1.2.3.4");
        response.addIdentifier().setValue(info.getId());

        mockResolveSearchPath(info, response);

        String id = provider.resolveValueSetId(info);

        assertEquals(id, response.getId());
    }

    @Test
    void resolveByUrlUsingResourceIdSucceeds() throws Exception {
        ValueSetInfo info = new ValueSetInfo().withId("1.2.3.4");

        ValueSet response = new ValueSet();
        response.setId("1.2.3.4");

        mockResolveSearchPath(info, response);

        String id = provider.resolveValueSetId(info);

        assertEquals(id, response.getId());
    }

    @Test
    void resolveByUrlNoMatchesThrowsException() throws Exception {
        ValueSetInfo info = new ValueSetInfo().withId("urn:oid:1.2.3.4");

        mockResolveSearchPath(info, null);

        assertThrows(IllegalArgumentException.class, () -> provider.resolveValueSetId(info));
    }

    @Test
    void expandByUrlNoMatchesThrowsException() throws Exception {
        ValueSetInfo info = new ValueSetInfo().withId("urn:oid:1.2.3.4");

        mockResolveSearchPath(info, null);

        assertThrows(TerminologyProviderException.class, () -> provider.expand(info));
    }

    @Test
    void nonNullVersionUnsupported() {
        ValueSetInfo info = new ValueSetInfo();
        info.setId("urn:oid:Test");
        info.setVersion("1.0.0.");

        assertThrows(UnsupportedOperationException.class, () -> provider.resolveValueSetId(info));
    }

    @Test
    void nonNullCodesystemsUnsupported() {
        CodeSystemInfo codeSystem = new CodeSystemInfo();
        codeSystem.setId("SNOMED-CT");
        codeSystem.setVersion("2013-09");

        ValueSetInfo info = new ValueSetInfo();
        info.setId("urn:oid:Test");
        info.getCodeSystems().add(codeSystem);

        assertThrows(UnsupportedOperationException.class, () -> provider.resolveValueSetId(info));
    }

    @Test
    void urnOidPrefixIsStripped() throws Exception {
        ValueSetInfo info = new ValueSetInfo();
        info.setId("urn:oid:Test");

        ValueSet valueSet = new ValueSet();
        valueSet.setId("Test");
        valueSet.getExpansion().getContainsFirstRep().setSystem(TEST_SYSTEM).setCode(TEST_CODE);

        mockResolveSearchPath(info, valueSet);

        String id = provider.resolveValueSetId(info);
        assertEquals("Test", id);
    }

    @Test
    void moreThanOneURLSearchResultIsError() throws Exception {
        ValueSetInfo info = new ValueSetInfo();
        info.setId("http://localhost/fhir/ValueSet/1.2.3.4");

        ValueSet firstSet = new ValueSet();
        firstSet.setId("1");
        firstSet.setUrl(info.getId());

        ValueSet secondSet = new ValueSet();
        secondSet.setId("1");
        secondSet.setUrl(info.getId());

        mockFhirSearch("/ValueSet?url=" + urlEncode(info.getId()), firstSet, secondSet);

        assertThrows(IllegalArgumentException.class, () -> provider.resolveValueSetId(info));
    }

    @Test
    void zeroURLSearchResultIsError() throws Exception {
        ValueSetInfo info = new ValueSetInfo();
        info.setId("http://localhost/fhir/ValueSet/1.2.3.4");

        mockResolveSearchPath(info, null);

        assertThrows(IllegalArgumentException.class, () -> provider.resolveValueSetId(info));
    }

    @Test
    void expandOperationReturnsCorrectCodesMoreThanZero() throws Exception {
        ValueSetInfo info = new ValueSetInfo();
        info.setId("urn:oid:Test");

        ValueSet valueSet = new ValueSet();
        valueSet.setId("Test");
        valueSet.getExpansion().getContainsFirstRep().setSystem(TEST_SYSTEM).setCode(TEST_CODE);

        mockResolveSearchPath(info, valueSet);

        Parameters parameters = new Parameters();
        parameters.getParameterFirstRep().setName("return").setResource(valueSet);

        mockFhirRead("/ValueSet/Test/$expand", parameters);

        Iterable<Code> codes = provider.expand(info);

        List<Code> list = StreamSupport.stream(codes.spliterator(), false).collect(Collectors.toList());
        assertEquals(1, list.size());
        assertEquals(TEST_SYSTEM, list.get(0).getSystem());
        assertEquals(TEST_CODE, list.get(0).getCode());
    }

    @Test
    void inOperationReturnsTrueWhenFhirReturnsTrue() throws Exception {
        ValueSetInfo info = new ValueSetInfo();
        info.setId("urn:oid:Test");

        ValueSet valueSet = new ValueSet();
        valueSet.setId("Test");
        valueSet.getExpansion().getContainsFirstRep().setSystem(TEST_SYSTEM).setCode(TEST_CODE);

        mockResolveSearchPath(info, valueSet);

        Code code = new Code();
        code.setSystem(TEST_SYSTEM);
        code.setCode(TEST_CODE);
        code.setDisplay(TEST_DISPLAY);

        Parameters parameters = new Parameters();
        parameters.getParameterFirstRep().setName("result").setValue(new BooleanType(true));

        mockFhirRead(
                "/ValueSet/Test/$validate-code?code=" + urlEncode(code.getCode()) + "&system="
                        + urlEncode(code.getSystem()),
                parameters);

        boolean result = provider.in(code, info);
        assertTrue(result);
    }

    @Test
    void inOperationReturnsFalseWhenFhirReturnsFalse() throws Exception {
        ValueSetInfo info = new ValueSetInfo();
        info.setId("urn:oid:Test");

        ValueSet valueSet = new ValueSet();
        valueSet.setId("Test");

        mockResolveSearchPath(info, valueSet);

        Code code = new Code();
        code.setSystem(TEST_SYSTEM);
        code.setCode(TEST_CODE);
        code.setDisplay(TEST_DISPLAY);

        Parameters parameters = new Parameters();
        parameters.getParameterFirstRep().setName("result").setValue(new BooleanType(false));

        mockFhirRead(
                "/ValueSet/Test/$validate-code?code=" + urlEncode(code.getCode()) + "&system="
                        + urlEncode(code.getSystem()),
                parameters);

        boolean result = provider.in(code, info);
        assertFalse(result);
    }

    @Test
    void inOperationHandlesNullSystem() throws Exception {
        ValueSetInfo info = new ValueSetInfo();
        info.setId("urn:oid:Test");

        ValueSet valueSet = new ValueSet();
        valueSet.setId("Test");

        mockResolveSearchPath(info, valueSet);

        Code code = new Code();
        code.setCode(TEST_CODE);
        code.setDisplay(TEST_DISPLAY);

        Parameters parameters = new Parameters();
        parameters.getParameterFirstRep().setName("result").setValue(new BooleanType(true));

        mockFhirRead("/ValueSet/Test/$validate-code?code=" + urlEncode(code.getCode()), parameters);

        boolean result = provider.in(code, info);
        assertTrue(result);
    }

    @Test
    void lookupOperationSuccess() throws Exception {
        CodeSystemInfo info = new CodeSystemInfo();
        info.setId(TEST_SYSTEM);
        info.setVersion(TEST_SYSTEM_VERSION);

        Code code = new Code();
        code.setCode(TEST_CODE);
        code.setSystem(TEST_SYSTEM);
        code.setDisplay(TEST_DISPLAY);

        Parameters parameters = new Parameters();
        parameters.addParameter().setName("name").setValue(new StringType(code.getCode()));
        parameters.addParameter().setName("version").setValue(new StringType(info.getVersion()));
        parameters.addParameter().setName("display").setValue(new StringType(code.getDisplay()));

        mockFhirPost("/CodeSystem/$lookup", parameters);

        Code result = provider.lookup(code, info);
        assertNotNull(result);
        assertEquals(result.getSystem(), code.getSystem());
        assertEquals(result.getCode(), code.getCode());
        assertEquals(result.getDisplay(), code.getDisplay());
    }

    protected String urlEncode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "utf-8");
    }

    protected void mockResolveSearchPath(ValueSetInfo info, ValueSet valueSet) throws UnsupportedEncodingException {
        if (valueSet != null && valueSet.getUrl() != null) {
            mockFhirSearch("/ValueSet?url=" + urlEncode(info.getId()), valueSet);
        } else {
            mockFhirSearch("/ValueSet?url=" + urlEncode(info.getId()));
        }

        if (valueSet != null && valueSet.getIdentifier().size() > 0) {
            mockFhirSearch("/ValueSet?identifier=" + urlEncode(info.getId()), valueSet);
        } else {
            mockFhirSearch("/ValueSet?identifier=" + urlEncode(info.getId()));
        }

        if (valueSet != null) {
            mockFhirRead("/ValueSet/" + valueSet.getId(), valueSet);
        } else {
            String[] parts = info.getId().split("[:/]");
            String expectedId = parts[parts.length - 1];
            mockNotFound("/ValueSet/" + expectedId);
        }
    }
}
