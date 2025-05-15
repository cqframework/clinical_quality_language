package org.opencds.cqf.cql.engine.execution;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.elm.r1.CodeDef;
import org.hl7.elm.r1.CodeSystemDef;
import org.hl7.elm.r1.ConceptDef;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.IncludeDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.ParameterDef;
import org.hl7.elm.r1.ValueSetDef;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.exception.CqlException;

/**
 * This class provides static utility methods for resolving ELM elements from a ELM library.
 */
public class Libraries {

    private Libraries() {
        // intentionally empty
    }

    public static IncludeDef resolveLibraryRef(final String libraryName, final Library relativeTo) {
        for (IncludeDef includeDef : relativeTo.getIncludes().getDef()) {
            if (includeDef.getLocalIdentifier().equals(libraryName)) {
                return includeDef;
            }
        }

        throw new CqlException(String.format("Could not resolve library reference '%s'.", libraryName));
    }

    public static List<ExpressionDef> resolveAllExpressionRef(final String name, final Library relativeTo) {
        // Assumption: List of defs is sorted.
        var defs = relativeTo.getStatements().getDef();
        int index = Collections.binarySearch(
                defs, name, (x, k) -> ((ExpressionDef) x).getName().compareTo((String) k));

        if (index < 0) {
            return Collections.emptyList();
        }

        int first = index;
        int last = index + 1;

        while (first > 0 && defs.get(first - 1).getName().equals(name)) {
            first--;
        }

        while (last < defs.size() && defs.get(last).getName().equals(name)) {
            last++;
        }

        return defs.subList(first, last);
    }

    public static ExpressionDef resolveExpressionRef(final String name, final Library relativeTo) {
        // Assumption: List of defs is sorted.
        var result = Collections.binarySearch(relativeTo.getStatements().getDef(), name, (x, k) -> ((ExpressionDef) x)
                .getName()
                .compareTo((String) k));
        if (result >= 0) {
            return relativeTo.getStatements().getDef().get(result);
        }

        throw new CqlException(String.format(
                "Could not resolve expression reference '%s' in library '%s'.",
                name, relativeTo.getIdentifier().getId()));
    }

    public static CodeSystemDef resolveCodeSystemRef(final String name, final Library relativeTo) {
        for (CodeSystemDef codeSystemDef : relativeTo.getCodeSystems().getDef()) {
            if (codeSystemDef.getName().equals(name)) {
                return codeSystemDef;
            }
        }

        throw new CqlException(String.format(
                "Could not resolve code system reference '%s' in library '%s'.",
                name, relativeTo.getIdentifier().getId()));
    }

    public static ValueSetDef resolveValueSetRef(final String name, final Library relativeTo) {
        for (ValueSetDef valueSetDef : relativeTo.getValueSets().getDef()) {
            if (valueSetDef.getName().equals(name)) {
                return valueSetDef;
            }
        }

        throw new CqlException(String.format(
                "Could not resolve value set reference '%s' in library '%s'.",
                name, relativeTo.getIdentifier().getId()));
    }

    public static CodeDef resolveCodeRef(final String name, final Library relativeTo) {
        for (CodeDef codeDef : relativeTo.getCodes().getDef()) {
            if (codeDef.getName().equals(name)) {
                return codeDef;
            }
        }

        throw new CqlException(String.format(
                "Could not resolve code reference '%s' in library '%s'.",
                name, relativeTo.getIdentifier().getId()));
    }

    public static ParameterDef resolveParameterRef(final String name, final Library relativeTo) {
        for (ParameterDef parameterDef : relativeTo.getParameters().getDef()) {
            if (parameterDef.getName().equals(name)) {
                return parameterDef;
            }
        }

        throw new CqlException(String.format(
                "Could not resolve parameter reference '%s' in library '%s'.",
                name, relativeTo.getIdentifier().getId()));
    }

    public static ConceptDef resolveConceptRef(final String name, final Library relativeTo) {
        for (ConceptDef conceptDef : relativeTo.getConcepts().getDef()) {
            if (conceptDef.getName().equals(name)) {
                return conceptDef;
            }
        }

        throw new CqlException(String.format("Could not resolve concept reference '%s'.", name));
    }

    public static List<FunctionDef> getFunctionDefs(final String name, final Library relativeTo) {
        var defs = resolveAllExpressionRef(name, relativeTo);

        return defs.stream()
                .filter(FunctionDef.class::isInstance)
                .map(FunctionDef.class::cast)
                .collect(Collectors.toList());
    }

    public static VersionedIdentifier toVersionedIdentifier(IncludeDef includeDef) {
        return new VersionedIdentifier()
                .withSystem(NamespaceManager.getUriPart(includeDef.getPath()))
                .withId(NamespaceManager.getNamePart(includeDef.getPath()))
                .withVersion(includeDef.getVersion());
    }
}
