package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.IncludeElement;
import org.hl7.elm.r1.Retrieve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollapsedElmRequirements {

    private List<ElmRequirement> uniqueRequirements = new ArrayList<ElmRequirement>();

    private Map<String, String> requirementIdMap = new HashMap<>();
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
            ElmRequirement newRequirement = ComparableElmRequirement.mergeRequirements(existing, requirement);
            mapRequirementId(requirement, newRequirement);
            uniqueRequirements.add(ComparableElmRequirement.mergeRequirements(existing, requirement));
        }
    }

    public Map<String, String> getRequirementIdMap() {
        return requirementIdMap;
    }

    private void mapRequirementId(ElmRequirement oldRequirement, ElmRequirement newRequirement) {
        if (oldRequirement.getElement().getLocalId() != null) {
            requirementIdMap.put(oldRequirement.getElement().getLocalId(), newRequirement.getElement().getLocalId());
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
