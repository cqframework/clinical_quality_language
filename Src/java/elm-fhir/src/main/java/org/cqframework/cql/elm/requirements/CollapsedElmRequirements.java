package org.cqframework.cql.elm.requirements;

import java.util.ArrayList;
import java.util.List;

public class CollapsedElmRequirements {

    private List<ElmRequirement> uniqueRequirements = new ArrayList<ElmRequirement>();
    public Iterable<ElmRequirement> getUniqueRequirements() {
        return uniqueRequirements;
    }

    public void add(ElmRequirement requirement) {
        ElmRequirement existing = getEquivalent(requirement);
        if (existing == null) {
            uniqueRequirements.add(requirement);
        }
        else {
            uniqueRequirements.remove(existing);
            uniqueRequirements.add(ComparableElmRequirement.mergeRequirements(existing, requirement));
        }
    }

    public ElmRequirement getEquivalent(ElmRequirement requirement) {
        for (ElmRequirement existing : uniqueRequirements) {
            if (ComparableElmRequirement.requirementsEquivalent(existing, requirement)) {
                return existing;
            }
        }

        return null;
    }
}
