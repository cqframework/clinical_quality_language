package org.opencds.cqf.cql.engine.fhir.searchparam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.RuntimeSearchParam;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.api.RestSearchParameterTypeEnum;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.QuantityParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;

public class SearchParameterResolver {

    private FhirContext context;

    public SearchParameterResolver(FhirContext context) {
        this.context = context;
    }

    public FhirContext getFhirContext() {
        return this.context;
    }

    public RuntimeSearchParam getSearchParameterDefinition(String dataType, String path) {
        return this.getSearchParameterDefinition(dataType, path, (RestSearchParameterTypeEnum)null);
    }

    public RuntimeSearchParam getSearchParameterDefinition(String dataType, String path, RestSearchParameterTypeEnum paramType) {
        if (dataType == null || path == null) {
            return null;
        }

        // Special case for system params. They need to be resolved by name.
        // TODO: All the others like "_language"
        String name = null;
        if (path.equals("id")) {
            name = "_id";
            path = "";
        }

        List<RuntimeSearchParam> params = this.context.getResourceDefinition(dataType).getSearchParams();

        for (RuntimeSearchParam param : params) {
            // If name matches, it's the one we want.
            if (name != null && param.getName().equals(name))
            {
                return param;
            }

            // Filter out parameters that don't match our requested type.
            if (paramType != null && !param.getParamType().equals(paramType)) {
                continue;
            }

            String normalizedPath = normalizePath(param.getPath());
            if (path.equals(normalizedPath) || path.equalsIgnoreCase(param.getName())) {
                return param;
            }
        }

        return null;
    }

    public Pair<String, IQueryParameterType> createSearchParameter(String context, String dataType, String path, String value) {

        RuntimeSearchParam searchParam = this.getSearchParameterDefinition(dataType, path);
        if (searchParam == null) {
            return null;
        }

        String name = searchParam.getName();

        switch (searchParam.getParamType()) {

            case TOKEN:
                return Pair.of(name, new TokenParam(value));
            case REFERENCE:
                return Pair.of(name, new ReferenceParam(context, null, value));
            case QUANTITY:
                return Pair.of(name, new QuantityParam(value));
            case STRING:
                return Pair.of(name, new StringParam(value));
            case NUMBER:
                return Pair.of(name, new NumberParam(value));
            case URI:
                return Pair.of(name, new UriParam(value));

            // Don't know how to handle these yet.
            case DATE:
            case HAS:
            case COMPOSITE:
            case SPECIAL:
        }

        return null;
    }

    // This is actually a lot of processing. We should cache search parameter resolutions.
    private String normalizePath(String path) {
        // TODO: What we really need is FhirPath parsing to just get the path
        //MedicationAdministration.medication.as(CodeableConcept)
        //MedicationAdministration.medication.as(Reference)
        //(MedicationAdministration.medication as CodeableConcept)
        //(MedicationAdministration.medication as Reference)
        //Condition.onset.as(Age) | Condition.onset.as(Range)
        //Observation.code | Observation.component.code

        // Trim off outer parens
        if (path.equals("(")) {
            path = path.substring(1, path.length() - 1);
        }

        Set<String> normalizedParts = new HashSet<String>();
        String [] orParts = path.split("\\|");
        for( String part : orParts ) {
            path = part.trim();

            // Trim off DataType
            path = path.substring(path.indexOf(".") + 1, path.length());


            // Split into components
            String[] pathSplit = path.split("\\.");
            List<String> newPathParts = new ArrayList<>();

            for (String p : pathSplit) {
                // Skip the "as(X)" part.
                if (p.startsWith("as(")) {
                    continue;
                }

                // Skip the "[x]" part.
                if (p.startsWith("[x]")) {
                    continue;
                }

                // Filter out spaces and everything after "medication as Reference"
                String[] ps = p.split(" ");
                if (ps != null && ps.length > 0){
                    newPathParts.add(ps[0]);
                }
            }

            path = String.join(".", newPathParts);
            normalizedParts.add(path);
        }

        // This handles cases such as /Condition?onset-age and /Condition?onset-date
        // where there are multiple underlying representations of the same property
        // (e.g. Condition.onset.as(Age) | Condition.onset.as(Range)), but
        // will punt on something like /Observation?combo-code where the underlying
        // representation maps to multiple places in a nested hierarchy (e.g.
        // Observation.code | Observation.component.code ).
        if( normalizedParts.size() == 1 ) {
            return normalizedParts.iterator().next();
        } else {
            return null;
        }
    }
}
