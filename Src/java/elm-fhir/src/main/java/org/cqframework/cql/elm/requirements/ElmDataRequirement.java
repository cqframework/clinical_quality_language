package org.cqframework.cql.elm.requirements;

import org.hl7.cql.model.ClassType;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.ListType;
import org.hl7.cql.model.SearchType;
import org.hl7.elm.r1.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ElmDataRequirement extends ElmExpressionRequirement {
    public ElmDataRequirement(VersionedIdentifier libraryIdentifier, Retrieve element) {
        super(libraryIdentifier, element);
    }

    public ElmDataRequirement(VersionedIdentifier libraryIdentifier, Retrieve element, Retrieve inferredFrom) {
        super(libraryIdentifier, element);
        this.inferredFrom = inferredFrom;
    }

    public Retrieve getRetrieve() {
        return (Retrieve)element;
    }

    public Retrieve getElement() {
        return getRetrieve();
    }

    private Retrieve inferredFrom;
    public Retrieve getInferredFrom() {
        return inferredFrom;
    }

    /**
     * May be an AliasedQuerySource or a LetClause
     */
    private Element querySource;
    public Element getQuerySource() {
        return querySource;
    }
    public void setQuerySource(Element querySource) {
        this.querySource = querySource;
    }

    private ElmPertinenceContext pertinenceContext;
    public ElmPertinenceContext getPertinenceContext() {
        return pertinenceContext;
    }
    public void setPertinenceContext(ElmPertinenceContext pertinenceContext) {
        this.pertinenceContext = pertinenceContext;
    }

    public String getAlias() {
        if (querySource instanceof AliasedQuerySource) {
            return ((AliasedQuerySource)querySource).getAlias();
        }
        else if (querySource instanceof LetClause) {
            return ((LetClause)querySource).getIdentifier();
        }
        else {
            throw new IllegalArgumentException("Cannot determine alias from data requirement because source is not an AliasedQuerySource or LetClause");
        }
    }

    public Expression getExpression() {
        if (querySource instanceof AliasedQuerySource) {
            return ((AliasedQuerySource)querySource).getExpression();
        }
        else if (querySource instanceof LetClause) {
            return ((LetClause)querySource).getExpression();
        }
        else {
            throw new IllegalArgumentException("Cannot determine expression for data requirement because source is not an AliasedQuerySource or LetClause");
        }
    }

    private static ElmDataRequirement inferFrom(ElmDataRequirement requirement) {
        Retrieve inferredRetrieve = ElmCloner.clone(requirement.getRetrieve());
        ElmDataRequirement result = new ElmDataRequirement(requirement.libraryIdentifier, inferredRetrieve, requirement.getRetrieve());
        if (requirement.hasProperties()) {
            for (Property p : requirement.getProperties()) {
                result.addProperty(p);
            }
        }
        return result;
    }

    private static ElmDataRequirement inferFrom(ElmQueryRequirement requirement) {
        ElmDataRequirement singleSourceRequirement = null;
        for (ElmDataRequirement dataRequirement : requirement.getDataRequirements()) {
            if (singleSourceRequirement == null) {
                singleSourceRequirement = dataRequirement;
            }
            else {
                singleSourceRequirement = null;
                break;
            }
        }
        if (singleSourceRequirement != null) {
            return inferFrom(singleSourceRequirement);
        }

        return new ElmDataRequirement(requirement.libraryIdentifier, getRetrieve(requirement.getQuery()));
    }

    public static ElmDataRequirement inferFrom(ElmExpressionRequirement requirement) {
        if (requirement instanceof ElmDataRequirement) {
            return inferFrom((ElmDataRequirement)requirement);
        }
        if (requirement instanceof ElmQueryRequirement) {
            return inferFrom((ElmQueryRequirement)requirement);
        }
        return new ElmDataRequirement(requirement.libraryIdentifier, getRetrieve(requirement.getExpression()));
    }

    // This is an "inferred" retrieve but from an expression context and so can't be unambiguously tied to the
    // data layer, and is thus not subject to include optimizations
    private static Retrieve getRetrieve(Expression expression) {
        return (Retrieve)new Retrieve()
                .withLocalId(expression.getLocalId())
                .withLocator(expression.getLocator())
                .withResultTypeName(expression.getResultTypeName())
                .withResultTypeSpecifier(expression.getResultTypeSpecifier())
                .withResultType(expression.getResultType());
    }

    private Set<Property> propertySet;
    public Iterable<Property> getProperties() {
        return propertySet;
    }

    public boolean hasProperties() {
        return propertySet != null;
    }

    public void addProperty(Property property) {
        if (propertySet == null) {
            propertySet = new LinkedHashSet<Property>();
        }
        propertySet.add(property);
    }

    public void removeProperty(Property property) {
        if (propertySet != null) {
            propertySet.remove(property);
        }
    }

    public void reportProperty(ElmPropertyRequirement propertyRequirement) {
        if (propertySet == null) {
            propertySet = new LinkedHashSet<Property>();
        }
        propertySet.add(propertyRequirement.getProperty());
    }

    private ElmConjunctiveRequirement conjunctiveRequirement;
    public ElmConjunctiveRequirement getConjunctiveRequirement() {
        ensureConjunctiveRequirement();
        return conjunctiveRequirement;
    }

    private void ensureConjunctiveRequirement() {
        if (conjunctiveRequirement == null) {
            conjunctiveRequirement = new ElmConjunctiveRequirement(libraryIdentifier, new Null());
        }
    }

    public void addConditionRequirement(ElmConditionRequirement conditionRequirement) {
        ensureConjunctiveRequirement();
        conjunctiveRequirement.combine(conditionRequirement);
    }

    public void addJoinRequirement(ElmJoinRequirement joinRequirement) {
        ensureConjunctiveRequirement();
        conjunctiveRequirement.combine(joinRequirement);
    }

    // TODO:
    private void extractStatedRequirements(Retrieve retrieve) {
        if (retrieve.getIdProperty() != null || retrieve.getIdSearch() != null) {
            // Add as an OtherFilterElement
        }

        if (retrieve.getCodeProperty() != null || retrieve.getCodeSearch() != null) {
            // Build the left-hand as a Property (or Search) against the alias
            // The right-hand is the retrieve codes
            // Code comparator values are in, =, and ~ (may need to support ~in at some point...)
            //Property p = new Property().withScope(this.getAlias()).withPath(retrieve.getCodeProperty());
            //ElmPropertyRequirement pr = new ElmPropertyRequirement(this.libraryIdentifier, p, this.getQuerySource(), true);
            //reportProperty(pr);
            //ElmExpressionRequirement vs = new ElmExpressionRequirement(this.libraryIdentifier, retrieve.getCodes());
            //InValueSet ivs = new InValueSet().withCode(p);
            //if (retrieve.getCodes() instanceof ValueSetRef) {
            //    ivs.setValueset((ValueSetRef)retrieve.getCodes());
            //}
            //else {
            //    ivs.setValuesetExpression(retrieve.getCodes());
            //}
            //ElmConditionRequirement ecr = new ElmConditionRequirement(this.libraryIdentifier, ivs, pr, vs);
            //addConditionRequirement(ecr);
        }

        if (retrieve.getDateProperty() != null || retrieve.getDateSearch() != null || retrieve.getDateLowProperty() != null || retrieve.getDateHighProperty() != null) {
            // Build the left-hand as a Property (or Search) against the alias
            // The right-hand is the date range
            // The comparator is always during (i.e. X >= start and X <= end)
        }
    }

    private void applyConditionRequirementTo(ElmConditionRequirement conditionRequirement, Retrieve retrieve, ElmRequirementsContext context) {
        if (retrieve.getDataType() == null) {
            // If the retrieve has no data type, it is neither useful nor possible to apply requirements to it
            return;
        }

        if (!conditionRequirement.isTargetable()) {
            // If the comparand of the condition requirement is not targetable, requirements cannot be applied
            return;
        }

        // if the column is terminology-valued, express as a code filter
        // if the column is date-valued, express as a date filter
        // else express as an other filter
        Property property = conditionRequirement.getProperty().getProperty();
        //DataType propertyType = property.getResultType();
        // Use the comparison type due to the likelihood of conversion operators along the property
        DataType comparisonType = conditionRequirement.getComparand().getExpression().getResultType();
        if (comparisonType != null) {
            if (context.getTypeResolver().isTerminologyType(comparisonType)) {
                CodeFilterElement codeFilter = new CodeFilterElement();
                if (property instanceof Search) {
                    codeFilter.setSearch(property.getPath());
                }
                else {
                    codeFilter.setProperty(property.getPath());
                }
                switch (conditionRequirement.getElement().getClass().getSimpleName()) {
                    case "Equal":
                        codeFilter.setComparator("=");
                        break;
                    case "Equivalent":
                        codeFilter.setComparator("~");
                        break;
                    case "In":
                    case "InValueSet":
                    case "AnyInValueSet":
                        codeFilter.setComparator("in");
                        break;
                }
                if (codeFilter.getComparator() != null && (conditionRequirement.isTargetable())) {
                    codeFilter.setValue(conditionRequirement.getComparand().getExpression());
                    if (!ComparableElmRequirement.hasCodeFilter(retrieve.getCodeFilter(), codeFilter)) {
                        retrieve.getCodeFilter().add(codeFilter);
                    }
                }
            }
            else if (context.getTypeResolver().isDateType(comparisonType)) {
                DateFilterElement dateFilter = new DateFilterElement();
                if (property instanceof Search) {
                    dateFilter.setSearch(property.getPath());
                }
                else {
                    dateFilter.setProperty(property.getPath());
                }
                // Determine operation and appropriate range
                // If right is interval-valued
                // If the operation is equal, equivalent, same as, in, or included in, the date range is the comparand
                Expression comparand = conditionRequirement.getComparand().getExpression();
                if (conditionRequirement.isTargetable()) {
                    if (context.getTypeResolver().isIntervalType(comparisonType)) {
                        switch (conditionRequirement.getElement().getClass().getSimpleName()) {
                            case "Equal":
                            case "Equivalent":
                            case "SameAs":
                            case "In":
                            case "IncludedIn":
                                dateFilter.setValue(comparand);
                                break;
                            case "Before":
                                dateFilter.setValue(new Interval().withLowClosed(true).withHigh(new Start().withOperand(comparand)).withHighClosed(false));
                                break;
                            case "SameOrBefore":
                                dateFilter.setValue(new Interval().withLowClosed(true).withHigh(new Start().withOperand(comparand)).withHighClosed(true));
                                break;
                            case "After":
                                dateFilter.setValue(new Interval().withLow(new End().withOperand(comparand)).withLowClosed(false).withHighClosed(true));
                                break;
                            case "SameOrAfter":
                                dateFilter.setValue(new Interval().withLow(new End().withOperand(comparand)).withLowClosed(true).withHighClosed(true));
                                break;
                            case "Includes":
                            case "Meets":
                            case "MeetsBefore":
                            case "MeetsAfter":
                            case "Overlaps":
                            case "OverlapsBefore":
                            case "OverlapsAfter":
                            case "Starts":
                            case "Ends":
                                // TODO: Might be better to turn these into date-based conjunctive requirements as part of condition requirement inference
                                break;
                        }
                    } else {
                        switch (conditionRequirement.getElement().getClass().getSimpleName()) {
                            case "Equal":
                            case "Equivalent":
                            case "SameAs":
                                dateFilter.setValue(new Interval().withLow(comparand).withLowClosed(true).withHigh(comparand).withHighClosed(true));
                                break;
                            case "Less":
                            case "Before":
                                dateFilter.setValue(new Interval().withLowClosed(true).withHigh(comparand).withHighClosed(false));
                                break;
                            case "LessOrEqual":
                            case "SameOrBefore":
                                dateFilter.setValue(new Interval().withLowClosed(true).withHigh(comparand).withHighClosed(true));
                                break;
                            case "Greater":
                            case "After":
                                dateFilter.setValue(new Interval().withLow(comparand).withLowClosed(false).withHighClosed(true));
                                break;
                            case "GreaterOrEqual":
                            case "SameOrAfter":
                                dateFilter.setValue(new Interval().withLow(comparand).withLowClosed(true).withHighClosed(true));
                                break;
                        }
                    }
                }

                if (dateFilter.getValue() != null) {
                    if (!ComparableElmRequirement.hasDateFilter(retrieve.getDateFilter(), dateFilter)) {
                        retrieve.getDateFilter().add(dateFilter);
                    }
                }
            }
            else {

            }
        }
    }

    private ClassType getRetrieveType(ElmRequirementsContext context, Retrieve retrieve) {
        DataType elementType = retrieve.getResultType() instanceof ListType
                ? ((ListType)retrieve.getResultType()).getElementType()
                : retrieve.getResultType();
        if (elementType instanceof ClassType) {
            return (ClassType)elementType;
        }
        return null;
    }

    private void applyJoinRequirementTo(ElmJoinRequirement joinRequirement, Retrieve retrieve, ElmRequirementsContext context, ElmQueryRequirement queryRequirements) {
        ElmDataRequirement leftRequirement = queryRequirements.getDataRequirement(joinRequirement.getLeftProperty().getSource());
        ElmDataRequirement rightRequirement = queryRequirements.getDataRequirement(joinRequirement.getRightProperty().getSource());
        if (leftRequirement != null && rightRequirement != null) {
            Retrieve leftRetrieve = leftRequirement.getRetrieve();
            Retrieve rightRetrieve = rightRequirement.getRetrieve();
            // Only report include possibility if the retrieves can both be tied to the data model
            if (leftRetrieve.getDataType() != null && rightRetrieve.getDataType() != null) {
                ClassType leftRetrieveType = getRetrieveType(context, leftRetrieve);
                ClassType rightRetrieveType = getRetrieveType(context, rightRetrieve);
                if (leftRetrieveType != null && rightRetrieveType != null) {
                    SearchType leftSearch;
                    SearchType rightSearch;
                    for (SearchType search : leftRetrieveType.getSearches()) {
                        if (joinRequirement.getLeftProperty().getProperty().getPath().startsWith(search.getPath())) {
                            if (search.getType().isCompatibleWith(rightRetrieveType)) {
                                leftSearch = search;
                                break;
                            }
                        }
                    }
                    for (SearchType search : rightRetrieveType.getSearches()) {
                        if (joinRequirement.getRightProperty().getProperty().getPath().startsWith(search.getPath())) {
                            if (search.getType().isCompatibleWith(leftRetrieveType)) {
                                rightSearch = search;
                                break;
                            }
                        }
                    }

                    // Search from the model info should be used to inform the selection, but will in general resolve to multiple choices
                    // May be a choice better left to the capabilityStatement-informed planning phase anyway
                }

                // In the absence of search information, either of these formulations is correct, favor primary query sources over withs
                if (leftRetrieve.getLocalId() == null) {
                    leftRetrieve.setLocalId(context.generateLocalId());
                }
                if (rightRetrieve.getLocalId() == null) {
                    rightRetrieve.setLocalId(context.generateLocalId());
                }
                if (rightRequirement.getQuerySource() instanceof With) {
                    leftRetrieve.getInclude().add(
                            new IncludeElement()
                                    .withIncludeFrom(rightRetrieve.getLocalId())
                                    .withRelatedDataType(rightRetrieve.getDataType())
                                    .withRelatedProperty(joinRequirement.getLeftProperty().getProperty().getPath())
                                    .withIsReverse(false));
                    rightRetrieve.setIncludedIn(leftRetrieve.getLocalId());
                }
                else {
                    rightRetrieve.getInclude().add(
                            new IncludeElement()
                                    .withIncludeFrom(leftRetrieve.getLocalId())
                                    .withRelatedDataType(leftRetrieve.getDataType())
                                    .withRelatedProperty(joinRequirement.getRightProperty().getProperty().getPath())
                                    .withIsReverse(false));
                    leftRetrieve.setIncludedIn(rightRetrieve.getLocalId());
                }
            }
        }
    }

    private void applyTo(Retrieve retrieve, ElmRequirementsContext context, ElmQueryRequirement queryRequirements) {
        // for each ConditionRequirement
        // apply to the retrieve
        for (ElmExpressionRequirement conditionRequirement : getConjunctiveRequirement().getArguments()) {
            if (conditionRequirement instanceof ElmConditionRequirement) {
                applyConditionRequirementTo((ElmConditionRequirement)conditionRequirement, retrieve, context);
            }
            else if (conditionRequirement instanceof ElmJoinRequirement) {
                applyJoinRequirementTo(((ElmJoinRequirement)conditionRequirement), retrieve, context, queryRequirements);
            }
        }
    }

    public void applyDataRequirements(ElmRequirementsContext context, ElmQueryRequirement queryRequirements) {
        // If the source of the alias is a direct retrieve, query requirements can be applied directly
        // Otherwise, the query requirements are applied to an "inferred" retrieve representing the query source
        extractStatedRequirements(getRetrieve());
        applyTo(getRetrieve(), context, queryRequirements);
    }
}
