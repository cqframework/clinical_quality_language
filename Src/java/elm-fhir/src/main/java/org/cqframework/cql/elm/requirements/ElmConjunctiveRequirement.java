package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.VersionedIdentifier;

import java.util.ArrayList;
import java.util.List;

public class ElmConjunctiveRequirement extends ElmExpressionRequirement {
    public ElmConjunctiveRequirement(VersionedIdentifier libraryIdentifier, Expression expression) {
        super(libraryIdentifier, expression);
    }

    private List<ElmExpressionRequirement> arguments = new ArrayList<ElmExpressionRequirement>();
    public List<ElmExpressionRequirement> getArguments() {
        return arguments;
    }

    @Override
    public ElmExpressionRequirement combine(ElmRequirement requirement) {
        if (requirement instanceof ElmConjunctiveRequirement) {
            for (ElmExpressionRequirement argument : ((ElmConjunctiveRequirement)requirement).getArguments()) {
                arguments.add(argument);
            }
        }
        else if (requirement instanceof ElmDisjunctiveRequirement) {
            // Conjunction of disjunctions, too complex for analysis (i.e. not in DNF)
            return new ElmExpressionRequirement(this.libraryIdentifier, this.getExpression());
        }
        else if (requirement instanceof ElmExpressionRequirement) {
            arguments.add((ElmExpressionRequirement)requirement);
        }
        else if (requirement instanceof ElmRequirements) {
            for (ElmRequirement r : ((ElmRequirements)requirement).getRequirements()) {
                combine(r);
            }
        }
        return this;
    }
}
