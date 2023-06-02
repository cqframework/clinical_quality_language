package org.opencds.cqf.cql.engine.fhir.terminology;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;
import org.hl7.fhir.r4.model.ValueSet;
import org.opencds.cqf.cql.engine.exception.TerminologyProviderException;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

public class R4FhirTerminologyProvider implements TerminologyProvider {

    private static final String URN_UUID = "urn:uuid:";
    private static final String URN_OID = "urn:oid:";
    private static final String VALUE_SET = "ValueSet";

    private IGenericClient fhirClient;

    public R4FhirTerminologyProvider() {
    }

    /**
     *
     * @param fhirClient - an IGenericClient that has endpoint and authentication
     *                   already defined and set.
     */
    public R4FhirTerminologyProvider(IGenericClient fhirClient) {
        this.fhirClient = fhirClient;
    }

    public IGenericClient getFhirClient() {
        return this.fhirClient;
    }

    @Override
    public boolean in(Code code, ValueSetInfo valueSet) {
        try {
            String id = resolveValueSetId(valueSet);
            Parameters respParam;
            if (code.getSystem() != null) {
                respParam = fhirClient
                        .operation()
                        .onInstance(new IdType(VALUE_SET, id))
                        // .onType(ValueSet.class)
                        .named("validate-code")
                        .withParameter(Parameters.class, "code", new StringType(code.getCode()))
                        .andParameter("system", new StringType(code.getSystem()))
                        .useHttpGet()
                        .execute();
            } else {
                respParam = fhirClient
                        .operation()
                        .onInstance(new IdType(VALUE_SET, id))
                        // .onType(ValueSet.class)
                        .named("validate-code")
                        .withParameter(Parameters.class, "code", new StringType(code.getCode()))
                        .useHttpGet()
                        .execute();
            }
            return ((BooleanType) respParam.getParameter().get(0).getValue()).booleanValue();

        } catch (Exception e) {
            throw new TerminologyProviderException(
                    String.format("Error performing membership check of Code: %s in ValueSet: %s", code.toString(),
                            valueSet.getId()),
                    e);
        }

    }

    @Override
    public Iterable<Code> expand(ValueSetInfo valueSet) {
        try {
            String id = resolveValueSetId(valueSet);
            Parameters respParam = fhirClient
                    .operation()
                    .onInstance(new IdType(VALUE_SET, id))
                    .named("expand")
                    .withNoParameters(Parameters.class)
                    .useHttpGet()
                    .execute();

            ValueSet expanded = (ValueSet) respParam.getParameter().get(0).getResource();
            List<Code> codes = new ArrayList<>();
            for (ValueSet.ValueSetExpansionContainsComponent codeInfo : expanded.getExpansion().getContains()) {
                Code nextCode = new Code()
                        .withCode(codeInfo.getCode())
                        .withSystem(codeInfo.getSystem())
                        .withVersion(codeInfo.getVersion())
                        .withDisplay(codeInfo.getDisplay());
                codes.add(nextCode);
            }
            return codes;

        } catch (Exception e) {
            throw new TerminologyProviderException(
                    String.format("Error performing expansion of ValueSet: %s", valueSet.getId()), e);
        }
    }

    @Override
    public Code lookup(Code code, CodeSystemInfo codeSystem) {
        try {
            Parameters respParam = fhirClient
                    .operation()
                    .onType(CodeSystem.class)
                    .named("lookup")
                    .withParameter(Parameters.class, "code", new CodeType(code.getCode()))
                    .andParameter("system", new UriType(codeSystem.getId()))
                    .execute();

            StringType display = (StringType) respParam.getParameter("display").getValue();
            if (display != null) {
                code.withDisplay(display.getValue());
            }

            return code.withSystem(codeSystem.getId());

        } catch (Exception e) {
            throw new TerminologyProviderException(String.format(
                    "Error performing lookup of Code: %s in CodeSystem: %s", code.toString(), codeSystem.getId()), e);
        }
    }

    protected Bundle searchByUrl(String url) {
        return fhirClient.search().forResource(ValueSet.class)
        .where(ValueSet.URL.matches().value(url)).returnBundle(Bundle.class).execute();
    }

    protected Bundle searchByIdentifier(String identifier) {
        return fhirClient.search().forResource(ValueSet.class)
        .where(ValueSet.IDENTIFIER.exactly().code(identifier)).returnBundle(Bundle.class).execute();
    }

    protected Bundle searchById(String id) {
        if (id.startsWith(URN_OID)) {
            id = id.replace(URN_OID, "");
        } else if (id.startsWith(URN_UUID)) {
            id = id.replace(URN_UUID, "");
        }
        Bundle searchResults = new Bundle();
        // If we reached this point and it looks like it might
        // be a FHIR resource ID, we will try to read it.
        // See https://www.hl7.org/fhir/datatypes.html#id
        if (id.matches("[A-Za-z0-9\\-\\.]{1,64}")) {
            try {
                ValueSet vs = fhirClient.read().resource(ValueSet.class).withId(id).execute();
                searchResults.addEntry().setResource(vs);
            } catch (ResourceNotFoundException rnfe) {
                // intentionally empty
            }
        }

        return searchResults;
    }

    public String resolveValueSetId(ValueSetInfo valueSet) {
        if (valueSet.getVersion() != null
                || (valueSet.getCodeSystems() != null && !valueSet.getCodeSystems().isEmpty())) {
            throw new UnsupportedOperationException(String.format(
                    "Could not expand value set %s; version and code system bindings are not supported at this time.",
                    valueSet.getId()));
        }

        // https://github.com/DBCG/cql_engine/pull/462 - Use a search path of URL,
        // identifier, and then resource id
        Bundle searchResults = searchByUrl(valueSet.getId());
        if (!searchResults.hasEntry()) {
            searchResults = searchByIdentifier(valueSet.getId());
        }

        if (!searchResults.hasEntry()) {
            searchResults = searchById(valueSet.getId());
        }

        if (!searchResults.hasEntry() || searchResults.getEntry().isEmpty()) {
            throw new IllegalArgumentException(String.format("Could not resolve value set %s.", valueSet.getId()));
        } else if (searchResults.getEntry().size() > 1) {
            throw new IllegalArgumentException("Found more than 1 ValueSet with url: " + valueSet.getId());
        } else {
            return searchResults.getEntryFirstRep().getResource().getIdElement().getIdPart();
        }
    }
}
