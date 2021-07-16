package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.VersionedIdentifier;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class ElmOperatorRequirement extends ElmExpressionRequirement {
    private HashSet<ElmRequirement> requirements = new LinkedHashSet<ElmRequirement>();
    public Iterable<ElmRequirement> getRequirements() {
        return requirements;
    }

    public ElmOperatorRequirement(VersionedIdentifier libraryIdentifier, Expression expression) {
        super(libraryIdentifier, expression);
    }

    @Override
    public ElmExpressionRequirement combine(ElmExpressionRequirement requirement) {
        if (requirement != null) {
            requirements.add(requirement);
        }
        return this;
    }
}
