package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.VersionedIdentifier;

import java.util.ArrayList;
import java.util.List;

public class ElmDisjunctiveRequirement extends ElmExpressionRequirement {
    public ElmDisjunctiveRequirement(VersionedIdentifier libraryIdentifier, Expression expression) {
        super(libraryIdentifier, expression);
    }

    private List<ElmExpressionRequirement> arguments = new ArrayList<ElmExpressionRequirement>();
    public List<ElmExpressionRequirement> getArguments() {
        return arguments;
    }

    @Override
    public ElmExpressionRequirement combine(ElmExpressionRequirement requirement) {
        if (requirement instanceof ElmDisjunctiveRequirement) {
            for (ElmExpressionRequirement argument : ((ElmDisjunctiveRequirement)requirement).getArguments()) {
                arguments.add(argument);
            }
        }
        else if (requirement instanceof ElmConjunctiveRequirement) {
            arguments.add(requirement);
        }
        else {
            arguments.add(requirement);
        }
        return this;
    }
}
