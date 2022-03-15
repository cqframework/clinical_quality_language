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

    /**
     * An operator expression is literal if it is all of its operands are literal and it has no parameter or external data access
     * @return
     */
    @Override
    public boolean isLiteral() {
        // TODO: Determine parameter or external data access within the operator or function body
        boolean isLiteral = true;
        for (ElmRequirement r : requirements) {
            if (!(r instanceof ElmExpressionRequirement && ((ElmExpressionRequirement)r).isLiteral())) {
                isLiteral = false;
            }
        }
        return isLiteral;
    }
}
