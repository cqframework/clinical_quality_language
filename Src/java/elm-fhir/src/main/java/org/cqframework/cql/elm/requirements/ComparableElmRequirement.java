package org.cqframework.cql.elm.requirements;

import static org.cqframework.cql.elm.evaluating.SimpleElmEvaluator.*;

import java.util.Iterator;
import org.cqframework.cql.cql2elm.tracking.Trackable;
import org.hl7.elm.r1.*;

public class ComparableElmRequirement {
    public ComparableElmRequirement(ElmRequirement requirement) {
        if (requirement == null) {
            throw new IllegalArgumentException("requirement is required");
        }

        this.requirement = requirement;
    }

    private ElmRequirement requirement;

    public ElmRequirement getRequirement() {
        return this.requirement;
    }

    @Override
    public int hashCode() {
        // Hashing only by the type/profile
        if (requirement.getElement() instanceof Retrieve) {
            Retrieve retrieve = (Retrieve) this.requirement.getElement();
            String typeUri = retrieve.getTemplateId() != null
                    ? retrieve.getTemplateId()
                    : (retrieve.getDataType() != null && retrieve.getDataType().getLocalPart() != null
                            ? retrieve.getDataType().getLocalPart()
                            : null);
            if (typeUri != null) {
                return typeUri.hashCode();
            }
        }

        return super.hashCode();
    }

    public static boolean codeFilterElementsEqual(CodeFilterElement left, CodeFilterElement right) {
        return stringsEqual(left.getProperty(), right.getProperty())
                && stringsEqual(left.getSearch(), right.getSearch())
                && stringsEqual(left.getComparator(), right.getComparator())
                && stringsEqual(left.getValueSetProperty(), right.getValueSetProperty())
                && codesEqual(left.getValue(), right.getValue());
    }

    public static boolean hasCodeFilter(Iterable<CodeFilterElement> left, CodeFilterElement right) {
        for (CodeFilterElement cfe : left) {
            if (codeFilterElementsEqual(cfe, right)) {
                return true;
            }
        }

        return false;
    }

    public static boolean codeFiltersEqual(Iterable<CodeFilterElement> left, Iterable<CodeFilterElement> right) {
        // TODO: Don't rely on order dependence here...
        Iterator<CodeFilterElement> leftIterator = left.iterator();
        Iterator<CodeFilterElement> rightIterator = right.iterator();

        while (leftIterator.hasNext()) {
            CodeFilterElement leftElement = leftIterator.next();

            if (!rightIterator.hasNext()) {
                return false;
            }

            CodeFilterElement rightElement = rightIterator.next();

            if (!codeFilterElementsEqual(leftElement, rightElement)) {
                return false;
            }
        }

        if (rightIterator.hasNext()) {
            return false;
        }

        return true;
    }

    public static boolean dateFilterElementsEqual(DateFilterElement left, DateFilterElement right) {
        return stringsEqual(left.getProperty(), right.getProperty())
                && stringsEqual(left.getLowProperty(), right.getLowProperty())
                && stringsEqual(left.getHighProperty(), right.getHighProperty())
                && stringsEqual(left.getSearch(), right.getSearch())
                && dateRangesEqual(left.getValue(), right.getValue());
    }

    public static boolean hasDateFilter(Iterable<DateFilterElement> left, DateFilterElement right) {
        for (DateFilterElement dfe : left) {
            if (dateFilterElementsEqual(dfe, right)) {
                return true;
            }
        }

        return false;
    }

    public static boolean dateFiltersEqual(Iterable<DateFilterElement> left, Iterable<DateFilterElement> right) {
        // TODO: Don't rely on order dependence here...
        Iterator<DateFilterElement> leftIterator = left.iterator();
        Iterator<DateFilterElement> rightIterator = right.iterator();

        while (leftIterator.hasNext()) {
            DateFilterElement leftElement = leftIterator.next();

            if (!rightIterator.hasNext()) {
                return false;
            }

            DateFilterElement rightElement = rightIterator.next();

            if (!dateFilterElementsEqual(leftElement, rightElement)) {
                return false;
            }
        }

        if (rightIterator.hasNext()) {
            return false;
        }

        return true;
    }

    // TODO: Handle types other than strings
    public static boolean otherFilterElementsEqual(OtherFilterElement left, OtherFilterElement right) {
        return stringsEqual(left.getProperty(), right.getProperty())
                && stringsEqual(left.getSearch(), right.getSearch())
                && stringsEqual(left.getComparator(), right.getComparator())
                && stringsEqual(left.getValue(), right.getValue());
    }

    public static boolean hasOtherFilter(Iterable<OtherFilterElement> left, OtherFilterElement right) {
        for (OtherFilterElement ofe : left) {
            if (otherFilterElementsEqual(ofe, right)) {
                return true;
            }
        }

        return false;
    }

    public static boolean otherFiltersEqual(Iterable<OtherFilterElement> left, Iterable<OtherFilterElement> right) {
        // TODO: Don't rely on order dependence here...
        Iterator<OtherFilterElement> leftIterator = left.iterator();
        Iterator<OtherFilterElement> rightIterator = right.iterator();

        while (leftIterator.hasNext()) {
            OtherFilterElement leftElement = leftIterator.next();

            if (!rightIterator.hasNext()) {
                return false;
            }

            OtherFilterElement rightElement = rightIterator.next();

            if (!otherFilterElementsEqual(leftElement, rightElement)) {
                return false;
            }
        }

        if (rightIterator.hasNext()) {
            return false;
        }

        return true;
    }

    public static boolean includeElementsEqual(IncludeElement left, IncludeElement right) {
        return left.getRelatedDataType() != null
                && right.getRelatedDataType() != null
                && stringsEqual(
                        left.getRelatedDataType().getNamespaceURI(),
                        right.getRelatedDataType().getNamespaceURI())
                && stringsEqual(
                        left.getRelatedDataType().getLocalPart(),
                        right.getRelatedDataType().getLocalPart())
                && stringsEqual(left.getRelatedProperty(), right.getRelatedProperty())
                && stringsEqual(left.getRelatedSearch(), right.getRelatedSearch());
    }

    public static boolean includeElementsEqual(Iterable<IncludeElement> left, Iterable<IncludeElement> right) {
        // TODO: Don't rely on order dependence here...
        Iterator<IncludeElement> leftIterator = left.iterator();
        Iterator<IncludeElement> rightIterator = right.iterator();

        while (leftIterator.hasNext()) {
            IncludeElement leftElement = leftIterator.next();

            if (!rightIterator.hasNext()) {
                return false;
            }

            IncludeElement rightElement = rightIterator.next();

            if (!includeElementsEqual(leftElement, rightElement)) {
                return false;
            }
        }

        if (rightIterator.hasNext()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ComparableElmRequirement) {
            return requirementsEquivalent(requirement, ((ComparableElmRequirement) other).getRequirement());
        }

        return false;
    }

    public static boolean requirementsEquivalent(ElmRequirement left, ElmRequirement right) {
        Retrieve retrieve = (Retrieve) left.getElement();
        Retrieve otherRetrieve = (Retrieve) right.getElement();

        return retrieve.getDataType() != null
                && retrieve.getDataType().equals(otherRetrieve.getDataType())
                && stringsEqual(retrieve.getTemplateId(), otherRetrieve.getTemplateId())
                && stringsEqual(retrieve.getContext(), otherRetrieve.getContext())
                && stringsEqual(retrieve.getContextProperty(), otherRetrieve.getContextProperty())
                && stringsEqual(retrieve.getContextSearch(), otherRetrieve.getContextSearch())
                && stringsEqual(retrieve.getCodeProperty(), otherRetrieve.getCodeProperty())
                && stringsEqual(retrieve.getCodeSearch(), otherRetrieve.getCodeSearch())
                && stringsEqual(retrieve.getCodeComparator(), otherRetrieve.getCodeComparator())
                && stringsEqual(retrieve.getValueSetProperty(), otherRetrieve.getValueSetProperty())
                && codesEqual(retrieve.getCodes(), otherRetrieve.getCodes())
                && stringsEqual(retrieve.getDateProperty(), otherRetrieve.getDateProperty())
                && stringsEqual(retrieve.getDateLowProperty(), otherRetrieve.getDateLowProperty())
                && stringsEqual(retrieve.getDateHighProperty(), otherRetrieve.getDateHighProperty())
                && stringsEqual(retrieve.getDateSearch(), otherRetrieve.getDateSearch())
                && dateRangesEqual(retrieve.getDateRange(), otherRetrieve.getDateRange())
                && stringsEqual(retrieve.getIdProperty(), otherRetrieve.getIdProperty())
                && stringsEqual(retrieve.getIdSearch(), otherRetrieve.getIdSearch())
                && stringsEqual(retrieve.getId(), otherRetrieve.getId())
                && codeFiltersEqual(retrieve.getCodeFilter(), otherRetrieve.getCodeFilter())
                && dateFiltersEqual(retrieve.getDateFilter(), otherRetrieve.getDateFilter())
                && otherFiltersEqual(retrieve.getOtherFilter(), otherRetrieve.getOtherFilter())
                // TODO: support for collapsing includes
                && stringsEqual(retrieve.getIncludedIn(), otherRetrieve.getIncludedIn())
                && includeElementsEqual(retrieve.getInclude(), otherRetrieve.getInclude());
    }

    private static boolean hasInclude(Retrieve retrieve, IncludeElement includeElement) {
        for (IncludeElement e : retrieve.getInclude()) {
            if (includeElementsEqual(e, includeElement)) {
                return true;
            }
        }

        return false;
    }

    public static ElmRequirement mergeRequirements(ElmRequirement existing, ElmRequirement required) {
        if (existing instanceof ElmDataRequirement) {
            if (required instanceof ElmDataRequirement) {
                if (existing.getElement() instanceof Retrieve) {
                    if (required.getElement() instanceof Retrieve) {
                        Retrieve existingRetrieve = (Retrieve) existing.getElement();
                        Retrieve requiredRetrieve = (Retrieve) required.getElement();
                        Retrieve newRetrieve = ElmCloner.clone(existingRetrieve);

                        var trackbacks = Trackable.INSTANCE.getTrackbacks(requiredRetrieve);
                        var newTrackbacks = Trackable.INSTANCE.getTrackbacks(newRetrieve);
                        newTrackbacks.addAll(trackbacks);

                        ElmDataRequirement newRequirement =
                                new ElmDataRequirement(existing.getLibraryIdentifier(), newRetrieve);
                        if (((ElmDataRequirement) existing).getProperties() != null) {
                            for (Property property : ((ElmDataRequirement) existing).getProperties()) {
                                newRequirement.addProperty(property);
                            }
                        }

                        // Merge mustSupport
                        if (((ElmDataRequirement) required).getProperties() != null) {
                            for (Property property : ((ElmDataRequirement) required).getProperties()) {
                                newRequirement.addProperty(property);
                            }
                        }

                        // Merge includes
                        for (IncludeElement includeElement : requiredRetrieve.getInclude()) {
                            if (!hasInclude(newRetrieve, includeElement)) {
                                newRetrieve.getInclude().add(ElmCloner.clone(includeElement));
                            }
                        }

                        return newRequirement;
                    }
                }
            }
        }

        return existing;
    }
}
