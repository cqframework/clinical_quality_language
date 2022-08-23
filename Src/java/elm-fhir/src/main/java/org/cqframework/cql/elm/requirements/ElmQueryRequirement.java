package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ElmQueryRequirement extends ElmExpressionRequirement {
    public ElmQueryRequirement(VersionedIdentifier libraryIdentifier, Query query) {
        super(libraryIdentifier, query);
    }

    public Query getQuery() {
        return (Query)element;
    }

    public Query getElement() {
        return getQuery();
    }

    private Set<ElmDataRequirement> dataRequirements = new LinkedHashSet<ElmDataRequirement>();
    public Iterable<ElmDataRequirement> getDataRequirements() {
        return dataRequirements;
    }

    public void addDataRequirements(ElmDataRequirement dataRequirement) {
        if (dataRequirement == null) {
            throw new IllegalArgumentException("dataRequirement must be provided");
        }
        if (dataRequirement.getQuerySource() == null) {
            throw new IllegalArgumentException("Data requirement must be associated with an alias to be added to a query requirements");
        }
        dataRequirements.add(dataRequirement);
    }

    public ElmDataRequirement getDataRequirement(Element querySource) {
        if (querySource instanceof AliasedQuerySource) {
            return getDataRequirement(((AliasedQuerySource)querySource).getAlias());
        }

        if (querySource instanceof LetClause) {
            return getDataRequirement(((LetClause)querySource).getIdentifier());
        }

        return null;
    }

    public ElmDataRequirement getDataRequirement(String alias) {
        for (ElmDataRequirement dataRequirement : dataRequirements) {
            if (dataRequirement.getAlias().equals(alias)) {
                return dataRequirement;
            }
        }

        return null;
    }

    @Override
    public boolean hasRequirement(ElmRequirement requirement) {
        boolean superHasRequirement = super.hasRequirement(requirement);
        if (!superHasRequirement) {
            for (ElmDataRequirement dataRequirement : dataRequirements) {
                if (dataRequirement.hasRequirement(requirement)) {
                    return true;
                }
            }
        }
        return superHasRequirement;
    }

    private void distributeConditionRequirement(ElmConditionRequirement requirement) {
        ElmDataRequirement dataRequirement = getDataRequirement(requirement.getProperty().getSource());
        if (dataRequirement != null) {
            dataRequirement.addConditionRequirement(requirement);
        }
    }

    /**
     * Determine which side of the join to apply the requirement
     * Should we apply it to both? Let the decision be made later?
     * Selecting left for now, arbitrary
     * @param requirement
     */
    private void distributeJoinRequirement(ElmJoinRequirement requirement) {
        ElmDataRequirement leftDataRequirement = getDataRequirement(requirement.getLeftProperty().getSource());
        ElmDataRequirement rightDataRequirement = getDataRequirement(requirement.getRightProperty().getSource());
        if (leftDataRequirement != null && rightDataRequirement != null) {
            leftDataRequirement.addJoinRequirement(requirement);
        }
    }

    private void distributeNestedConditionRequirement(ElmDataRequirement dataRequirement, String path, ElmConditionRequirement requirement) {
        Property qualifiedProperty = new Property();
        Property nestedProperty = requirement.getProperty().getProperty();
        qualifiedProperty.setPath(String.format("%s.%s", path, nestedProperty.getPath()));
        qualifiedProperty.setScope(dataRequirement.getAlias());
        qualifiedProperty.setResultType(nestedProperty.getResultType());
        qualifiedProperty.setResultTypeName(nestedProperty.getResultTypeName());
        qualifiedProperty.setResultTypeSpecifier(nestedProperty.getResultTypeSpecifier());
        ElmPropertyRequirement qualifiedPropertyRequirement = new ElmPropertyRequirement(libraryIdentifier, qualifiedProperty, dataRequirement.getQuerySource(), true);
        // TODO: Validate that the comparand is context literal and scope stable
        ElmConditionRequirement qualifiedCondition = new ElmConditionRequirement(libraryIdentifier, requirement.getExpression(), qualifiedPropertyRequirement, requirement.getComparand());
        dataRequirement.addConditionRequirement(qualifiedCondition);
    }

    /**
     * Distributes a nested query requirement through this query by detecting references from the nested query
     * to aliases in this query. This may result in a nested query against a property of an alias in this query
     * being absorbed as a qualified reference, or it may result in a nested query being absorbed as a joined alias.
     * Returns true if the requirement was absorbed (and should therefore not be reported up the context), false
     * if the requirement could not be absorbed and should continue to be reported as a query requirement.
     * @param requirement
     * @param context
     * @return
     */
    private boolean distributeQueryRequirement(ElmQueryRequirement requirement, ElmRequirementsContext context) {
        // If the query is single source and the source is a property reference to an alias in the current query
        // distribute the conjunctive requirements of the nested query as conjunctive requirements against the
        // qualified property
        if (requirement.dataRequirements.size() == 1) {
            for (ElmDataRequirement nestedAlias : requirement.dataRequirements) {
                if (nestedAlias.getExpression() instanceof Property) {
                    Property sourceProperty = (Property)nestedAlias.getExpression();
                    if (sourceProperty.getScope() != null) {
                        ElmDataRequirement aliasDataRequirement = getDataRequirement(sourceProperty.getScope());
                        if (aliasDataRequirement != null && nestedAlias.getConjunctiveRequirement() != null) {
                            for (ElmExpressionRequirement nestedRequirement : nestedAlias.getConjunctiveRequirement().getArguments()) {
                                // A conjunctive requirement against a nested query that is based on a property of the current query
                                // can be inferred as a conjunctive requirement against the qualified property in the current query
                                if (nestedRequirement instanceof ElmConditionRequirement) {
                                    distributeNestedConditionRequirement(aliasDataRequirement, sourceProperty.getPath(), (ElmConditionRequirement)nestedRequirement);
                                    return true;
                                }
                            }
                        }
                    }
                }
                // If the query is a single source and is a retrieve that has a condition requirement relating to a property in the current
                // query, the nested requirement can be distributed to this query context as a join requirement
                else if (nestedAlias.getExpression() instanceof Retrieve) {
                    for (ElmExpressionRequirement nestedRequirement : nestedAlias.getConjunctiveRequirement().getArguments()) {
                        if (nestedRequirement instanceof ElmConditionRequirement) {
                            ElmConditionRequirement nestedConditionRequirement = (ElmConditionRequirement)nestedRequirement;
                            if (nestedConditionRequirement.getComparand() instanceof ElmPropertyRequirement) {
                                ElmPropertyRequirement comparand = (ElmPropertyRequirement)nestedConditionRequirement.getComparand();
                                ElmDataRequirement relatedRequirement = this.getDataRequirement(comparand.getSource());
                                if (relatedRequirement != null) {
                                    // Create a new data requirement referencing the retrieve
                                    ElmDataRequirement newRequirement = new ElmDataRequirement(libraryIdentifier, nestedAlias.getRetrieve());
                                    // Create a new and unique alias for this data requirement
                                    String localId = context.generateLocalId();
                                    AliasedQuerySource aqs = new AliasedQuerySource()
                                            .withExpression(newRequirement.getRetrieve())
                                            .withLocalId(localId)
                                            .withAlias(localId);
                                    newRequirement.setQuerySource(aqs);
                                    // Infer the condition requirement as a join requirement
                                    Property leftProperty = ElmCloner.clone(nestedConditionRequirement.getProperty().getProperty());
                                    leftProperty.setSource(null);
                                    leftProperty.setScope(localId);
                                    ElmPropertyRequirement leftPropertyRequirement = new ElmPropertyRequirement(libraryIdentifier, leftProperty, aqs, true);
                                    ElmJoinRequirement joinRequirement = new ElmJoinRequirement(libraryIdentifier, nestedConditionRequirement.getExpression(), leftPropertyRequirement, comparand);
                                    newRequirement.getConjunctiveRequirement().combine(joinRequirement);
                                    // Report the new data requirement to this query
                                    addDataRequirements(newRequirement);
                                    // Indicate that this query requirement overall does not need to be reported
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public void addChildRequirements(ElmRequirement childRequirements) {
        // TODO: Placeholder to support processing child requirements gathered during the query context processing
        // The property requirements have already been reported and processed, so this is currently unnecessary
    }

    public boolean distributeExpressionRequirement(ElmExpressionRequirement requirement, ElmRequirementsContext context) {
        if (requirement instanceof ElmConjunctiveRequirement) {
            for (ElmExpressionRequirement expressionRequirement : ((ElmConjunctiveRequirement)requirement).getArguments()) {
                distributeExpressionRequirement(expressionRequirement, context);
            }
            return true;
        }
        else if (requirement instanceof ElmDisjunctiveRequirement) {
            // TODO: Distribute disjunctive requirements (requires union rewrite)
            return true;
        }
        else if (requirement instanceof ElmConditionRequirement) {
            distributeConditionRequirement((ElmConditionRequirement)requirement);
            return true;
        }
        else if (requirement instanceof ElmJoinRequirement) {
            distributeJoinRequirement((ElmJoinRequirement)requirement);
            return true;
        }
        else if (requirement instanceof ElmQueryRequirement) {
            return distributeQueryRequirement((ElmQueryRequirement)requirement, context);
        }

        return false;
    }

    public void analyzeDataRequirements(ElmRequirementsContext context) {

        // apply query requirements to retrieves
        for (ElmDataRequirement dataRequirement : dataRequirements) {
            dataRequirement.applyDataRequirements(context, this);
        }
    }
}
