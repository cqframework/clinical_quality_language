package org.cqframework.cql.elm.requirements;

import org.cqframework.cql.cql2elm.tracking.Trackable;
import org.cqframework.cql.elm.visiting.BaseElmLibraryVisitor;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.ListType;
import org.hl7.elm.r1.*;
import org.jetbrains.annotations.NotNull;

/*
This class implements an ELM Visitor to perform static analysis of data and dependency requirements
for ELM trees.

# Overall optimization/dependency tracing strategy:
Two different types of requirements, reported requirements and inferred requirements.
Reported requirements are tracked at the ExpressionDef level and rolled-up across expression defs
Inferred requirements are inferred through expressions and reported at query boundaries
  (or reported directly if the retrieve is not defined as part of a query source definition)

The visitor is focused on two main tasks:
* Gathering dependencies (any artifacts or declarations used)
* Inferring data requirements (the minimum set of retrieves required to achieve a successful evaluation)

Dependencies in any given visit are tracked cumulatively at the root
Data Requirements must be tracked as inferred per expression def

So in the context, when a requirement is reported, if it's a data requirement, it's tracked at the expression def level,
whereas if it's a dependency, it's always tracked at the root.

# Where clause optimization strategy:
Visit a where clause looking for sargeable conditions of the form:

    A op B

Where:
* A is an order-preserving expression with a single property reference to a property of some source in the current query context
* op is a positive relative comparison operation (=, >, <, >=, <=)
* B is a functional, repeatable, and deterministic context literal expression with respect to the current query context

Gather sargeable conditions as Lists of conditions. At an AND, combine conditions from sub-nodes.
At an OR, the result is separate lists of condition lists.
At an AND, if there are already lists of lists, the condition is too complex for analysis (i.e. it's not in DNF or CNF)

At a property, return an ElmPropertyRequirement
At a literal return an ElmExpressionRequirement w/ contextLiteral true
At a parameter return an ElmExpressionRequirement w/ contextLiteral true
At a unary expression, return an ElmExpressionRequirement w/ contextLiteral false
At a binary comparison expression, return ElmConditionRequirement if possible
At a logical expression, return ElmConjunctiveRequirement or ElmDisjunctiveRequirement

 */
public class ElmRequirementsVisitor extends BaseElmLibraryVisitor<ElmRequirement, ElmRequirementsContext> {

    public ElmRequirementsVisitor() {
        super();
    }

    @Override
    protected ElmRequirement defaultResult(@NotNull Element elm, ElmRequirementsContext context) {
        return null;
    }

    @Override
    public ElmRequirement aggregateResult(ElmRequirement result, ElmRequirement nextResult) {
        if (result == null) {
            return nextResult;
        }

        if (nextResult == null) {
            return result;
        }

        if (result instanceof ElmRequirements) {
            ((ElmRequirements) result).reportRequirement(nextResult);
            return result;
        }

        ElmRequirements requirements = new ElmRequirements(result.getLibraryIdentifier(), result.getElement());
        requirements.reportRequirement(result);
        requirements.reportRequirement(nextResult);
        return requirements;
    }

    @Override
    public ElmRequirement visitExpressionDef(ExpressionDef elm, ElmRequirementsContext context) {
        ElmRequirement result = null;
        context.enterExpressionDef(elm);
        boolean pertinenceTagFound = context.enterPertinenceContext(elm);

        try {
            result = super.visitExpressionDef(elm, context);
        } finally {
            context.exitExpressionDef(result);
            if (pertinenceTagFound) {
                context.exitPertinenceContext();
            }
        }
        return result;
    }

    @Override
    public ElmRequirement visitFunctionDef(FunctionDef elm, ElmRequirementsContext context) {
        context.reportFunctionDef(elm);
        return super.visitFunctionDef(elm, context);
    }

    @Override
    public ElmRequirement visitExpressionRef(ExpressionRef elm, ElmRequirementsContext context) {
        ElmRequirement result = null;
        if (elm instanceof FunctionRef) {
            result = visitFunctionRef((FunctionRef) elm, context);
        } else {
            result = context.reportExpressionRef(elm);
        }
        if (result != null) {
            // If the expression ref is to a retrieve or a single-source query, surface it
            // as an "inferred" requirement
            // in the referencing scope
            if (result instanceof ElmDataRequirement) {
                ElmDataRequirement inferredRequirement = ElmDataRequirement.inferFrom((ElmDataRequirement) result);
                // Should be being reported as a data requirement...
                // context.reportRetrieve(inferredRequirement.getRetrieve());
                result = inferredRequirement;
            } else if (result instanceof ElmQueryRequirement) {
                ElmDataRequirement inferredRequirement = ElmDataRequirement.inferFrom((ElmQueryRequirement) result);
                // Should be being reported as a data requirement...
                // context.reportRetrieve(inferredRequirement.getRetrieve());
                result = inferredRequirement;
            }
            return result;
        }
        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitFunctionRef(FunctionRef elm, ElmRequirementsContext context) {
        context.reportFunctionRef(elm);
        ElmRequirement result = super.visitFunctionRef(elm, context);

        // If the result is a data requirement and the function is cardinality-reducing,
        // Return as an operator requirement, rather than returning the result
        if (result instanceof ElmDataRequirement) {
            if (elm.getOperand().size() != 1
                    || (Trackable.INSTANCE.getResultType(elm.getOperand().get(0)) instanceof ListType
                            && !(Trackable.INSTANCE.getResultType(elm) instanceof ListType))) {
                // Note that the assumption here is that the data requirement has already been
                // reported to the context
                return new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm)
                        .combine((ElmDataRequirement) result);
            }
        }

        if (result != null) {
            return result;
        }

        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitParameterDef(ParameterDef elm, ElmRequirementsContext context) {
        context.reportParameterDef(elm);
        return super.visitParameterDef(elm, context);
    }

    @Override
    public ElmRequirement visitParameterRef(ParameterRef elm, ElmRequirementsContext context) {
        context.reportParameterRef(elm);
        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitRetrieve(Retrieve elm, ElmRequirementsContext context) {
        // TODO: childResult reporting?
        ElmPertinenceContext elmPertinenceContext = context.peekPertinenceContext();
        super.visitRetrieve(elm, context);

        ElmDataRequirement result = new ElmDataRequirement(context.getCurrentLibraryIdentifier(), elm);
        if (elmPertinenceContext != null) {
            result.pertinenceContext = elmPertinenceContext;
        }
        // If not analyzing requirements, or in a query context, report the data
        // requirement
        // If in a query context, the requirement will be reported as an inferred
        // requirement at the query boundary
        if (!context.getOptions().getAnalyzeDataRequirements() || !context.inQueryContext()) {
            context.reportRequirements(result, null);
        }
        return result;
    }

    @Override
    public ElmRequirement visitCodeSystemDef(CodeSystemDef elm, ElmRequirementsContext context) {
        context.reportCodeSystemDef(elm);
        return super.visitCodeSystemDef(elm, context);
    }

    @Override
    public ElmRequirement visitValueSetDef(ValueSetDef elm, ElmRequirementsContext context) {
        context.reportValueSetDef(elm);
        return super.visitValueSetDef(elm, context);
    }

    @Override
    public ElmRequirement visitCodeSystemRef(CodeSystemRef elm, ElmRequirementsContext context) {
        context.reportCodeSystemRef(elm);
        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitValueSetRef(ValueSetRef elm, ElmRequirementsContext context) {
        context.reportValueSetRef(elm);
        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitLibrary(Library elm, ElmRequirementsContext context) {
        context.enterLibrary(elm.getIdentifier());
        try {
            return super.visitLibrary(elm, context);
        } finally {
            context.exitLibrary();
        }
    }

    @Override
    public ElmRequirement visitIncludeDef(IncludeDef elm, ElmRequirementsContext context) {
        context.reportIncludeDef(elm);
        return super.visitIncludeDef(elm, context);
    }

    @Override
    public ElmRequirement visitContextDef(ContextDef elm, ElmRequirementsContext context) {
        context.reportContextDef(elm);
        return super.visitContextDef(elm, context);
    }

    @Override
    public ElmRequirement visitCodeRef(CodeRef elm, ElmRequirementsContext context) {
        context.reportCodeRef(elm);
        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitCodeDef(CodeDef elm, ElmRequirementsContext context) {
        context.reportCodeDef(elm);
        return super.visitCodeDef(elm, context);
    }

    @Override
    public ElmRequirement visitConceptRef(ConceptRef elm, ElmRequirementsContext context) {
        context.reportConceptRef(elm);
        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitConceptDef(ConceptDef elm, ElmRequirementsContext context) {
        context.reportConceptDef(elm);
        return super.visitConceptDef(elm, context);
    }

    /**
     * If both sides are column references that point to the same column in the same
     * alias
     * the condition is a tautology
     * If both sides are column references that point to different columns in the
     * same alias
     * the condition is a constraint
     * If both sides are column references that point to different aliases
     * the condition is a join
     * If one side or the other is a column reference
     * the condition is a potentially sargeable condition
     *
     * @param elm
     * @param context
     * @param left
     * @param right
     * @return
     */
    protected ElmRequirement inferConditionRequirement(
            Expression elm, ElmRequirementsContext context, ElmRequirement left, ElmRequirement right) {
        ElmPropertyRequirement leftProperty =
                left instanceof ElmPropertyRequirement ? (ElmPropertyRequirement) left : null;
        ElmPropertyRequirement rightProperty =
                right instanceof ElmPropertyRequirement ? (ElmPropertyRequirement) right : null;
        if (leftProperty != null && leftProperty.getInCurrentScope()) {
            if (rightProperty != null && rightProperty.getInCurrentScope()) {
                if (leftProperty.getSource() == rightProperty.getSource()) {
                    return new ElmConstraintRequirement(
                            context.getCurrentLibraryIdentifier(), elm, leftProperty, rightProperty);
                } else if (leftProperty.getSource() instanceof AliasedQuerySource
                        && rightProperty.getSource() instanceof AliasedQuerySource) {
                    return new ElmJoinRequirement(
                            context.getCurrentLibraryIdentifier(), elm, leftProperty, rightProperty);
                }
            }
            if (right instanceof ElmExpressionRequirement) {
                return new ElmConditionRequirement(
                        context.getCurrentLibraryIdentifier(), elm, leftProperty, (ElmExpressionRequirement) right);
            }
        } else if (rightProperty != null && rightProperty.getInCurrentScope()) {
            if (leftProperty != null && leftProperty.getInCurrentScope()) {
                if (leftProperty.getSource() == rightProperty.getSource()) {
                    return new ElmConstraintRequirement(
                            context.getCurrentLibraryIdentifier(), elm, leftProperty, rightProperty);
                } else if (leftProperty.getSource() instanceof AliasedQuerySource
                        && rightProperty.getSource() instanceof AliasedQuerySource) {
                    return new ElmJoinRequirement(
                            context.getCurrentLibraryIdentifier(), elm, leftProperty, rightProperty);
                }
            }
            if (left instanceof ElmExpressionRequirement) {
                return new ElmConditionRequirement(
                        context.getCurrentLibraryIdentifier(),
                        elm,
                        (ElmPropertyRequirement) right,
                        (ElmExpressionRequirement) left);
            }
        }

        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitFields(BinaryExpression elm, ElmRequirementsContext context) {
        // Override visit children behavior to determine whether to create an
        // ElmConditionRequirement
        if (elm.getOperand().size() != 2) {
            throw new IllegalArgumentException("BinaryExpression must have two operands.");
        }

        switch (elm.getClass().getSimpleName()) {
                /**
                 * Determine whether the condition is sargeable:
                 *
                 * A op B
                 *
                 * Where:
                 * * A is an order-preserving expression with a single property reference to a
                 * property of some source in the current query context
                 * * op is a positive relative comparison operation (=, >, <, >=, <=) or a
                 * membership operator (in, contains)
                 * * B is a functional, repeatable, and deterministic context literal expression
                 * with respect to the current query context
                 */
            case "Equal":
            case "Equivalent":
            case "SameAs":
            case "Greater":
            case "GreaterOrEqual":
            case "SameOrAfter":
            case "After":
            case "Less":
            case "LessOrEqual":
            case "SameOrBefore":
            case "Before":
            case "In":
            case "Contains": {
                ElmRequirement left = visitElement(elm.getOperand().get(0), context);
                ElmRequirement right = visitElement(elm.getOperand().get(1), context);
                return inferConditionRequirement(elm, context, left, right);
            }

                /**
                 * Gather sargeable conditions as Lists of conditions. At an AND, combine
                 * conditions from sub-nodes.
                 * At an OR, the result is separate lists of condition lists.
                 * At an AND, if there are already lists of lists, the condition is too complex
                 * for analysis (i.e. it's not in DNF or CNF)
                 */
                // TODO: Normalize to DNF
            case "And": {
                ElmRequirement left = visitElement(elm.getOperand().get(0), context);
                ElmRequirement right = visitElement(elm.getOperand().get(1), context);

                if (left instanceof ElmExpressionRequirement && right instanceof ElmExpressionRequirement) {
                    return new ElmConjunctiveRequirement(context.getCurrentLibraryIdentifier(), elm)
                            .combine((ElmExpressionRequirement) left)
                            .combine((ElmExpressionRequirement) right);
                } else if (left instanceof ElmExpressionRequirement && right == null) {
                    return left;
                } else if (right instanceof ElmExpressionRequirement && left == null) {
                    return right;
                }

                return aggregateResult(left, right);
            }

            case "Or": {
                ElmRequirement left = visitElement(elm.getOperand().get(0), context);
                ElmRequirement right = visitElement(elm.getOperand().get(1), context);

                if (left instanceof ElmExpressionRequirement && right instanceof ElmExpressionRequirement) {
                    return new ElmDisjunctiveRequirement(context.getCurrentLibraryIdentifier(), elm)
                            .combine((ElmExpressionRequirement) left)
                            .combine((ElmExpressionRequirement) right);
                } else if (left instanceof ElmExpressionRequirement && right == null) {
                    return left;
                } else if (right instanceof ElmExpressionRequirement && left == null) {
                    return right;
                }

                return aggregateResult(left, right);
            }

                // TODO: Rewrite
            case "Xor":
            case "Implies":
                // case "Not":
                // case "NotEqual":
            case "Starts":
            case "Ends":
            case "Includes":
            case "IncludedIn":
            case "Meets":
            case "MeetsBefore":
            case "MeetsAfter":
            case "Overlaps":
            case "OverlapsBefore":
            case "OverlapsAfter":
            case "ProperIncludes":
            case "ProperIncludedIn":
            default: {
                super.visitFields(elm, context);
                return new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm);
            }
        }
    }

    @Override
    public ElmRequirement visitTernaryExpression(TernaryExpression elm, ElmRequirementsContext context) {
        ElmRequirement requirements = super.visitTernaryExpression(elm, context);
        return new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm).combine(requirements);
    }

    @Override
    public ElmRequirement visitNaryExpression(NaryExpression elm, ElmRequirementsContext context) {
        ElmRequirement requirements = super.visitNaryExpression(elm, context);
        return new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm).combine(requirements);
    }

    @Override
    public ElmRequirement visitOperandRef(OperandRef elm, ElmRequirementsContext context) {
        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitIdentifierRef(IdentifierRef elm, ElmRequirementsContext context) {
        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitLiteral(Literal elm, ElmRequirementsContext context) {
        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitInterval(Interval elm, ElmRequirementsContext context) {
        ElmRequirement result = super.visitInterval(elm, context);
        ElmOperatorRequirement finalResult = new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm);
        finalResult.combine(result);
        return finalResult;
    }

    @Override
    public ElmRequirement visitIf(If elm, ElmRequirementsContext context) {
        // TODO: Rewrite the if as equivalent logic
        ElmOperatorRequirement result = new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm);
        ElmRequirement childResult = null;

        if (elm.getCondition() != null) {
            childResult = this.visitElement(elm.getCondition(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }

        if (elm.getThen() != null) {
            childResult = this.visitElement(elm.getThen(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }

        if (elm.getElse() != null) {
            childResult = this.visitElement(elm.getElse(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }

        return result;
    }

    @Override
    public ElmRequirement visitCase(Case elm, ElmRequirementsContext context) {
        // TODO: Rewrite the case as equivalent logic
        ElmOperatorRequirement result = new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm);
        ElmRequirement childResult = null;
        if (elm.getComparand() != null) {
            childResult = this.visitElement(elm.getComparand(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }

        for (CaseItem ci : elm.getCaseItem()) {
            childResult = this.visitElement(ci, context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }

        if (elm.getElse() != null) {
            childResult = this.visitElement(elm.getElse(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }

        return result;
    }

    @Override
    public ElmRequirement visitNull(Null elm, ElmRequirementsContext context) {
        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitSplit(Split elm, ElmRequirementsContext context) {
        // If the separator is a literal, infer based only on the string to split
        // argument
        if (elm.getSeparator() instanceof Literal) {
            return visitElement(elm.getStringToSplit(), context);
        }
        return super.visitSplit(elm, context);
    }

    @Override
    public ElmRequirement visitTimeOfDay(TimeOfDay elm, ElmRequirementsContext context) {
        return new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitToday(Today elm, ElmRequirementsContext context) {
        return new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitNow(Now elm, ElmRequirementsContext context) {
        return new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitDateTime(DateTime elm, ElmRequirementsContext context) {
        ElmOperatorRequirement result = new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm);
        if (elm.getYear() != null) {
            ElmRequirement childResult = visitExpression(elm.getYear(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        if (elm.getMonth() != null) {
            ElmRequirement childResult = visitExpression(elm.getMonth(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        if (elm.getDay() != null) {
            ElmRequirement childResult = visitExpression(elm.getDay(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        if (elm.getHour() != null) {
            ElmRequirement childResult = visitExpression(elm.getHour(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        if (elm.getMinute() != null) {
            ElmRequirement childResult = visitExpression(elm.getMinute(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        if (elm.getSecond() != null) {
            ElmRequirement childResult = visitExpression(elm.getSecond(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        if (elm.getMillisecond() != null) {
            ElmRequirement childResult = visitExpression(elm.getMillisecond(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        if (elm.getTimezoneOffset() != null) {
            ElmRequirement childResult = visitExpression(elm.getTimezoneOffset(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        return result;
    }

    @Override
    public ElmRequirement visitDate(Date elm, ElmRequirementsContext context) {
        ElmOperatorRequirement result = new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm);
        if (elm.getYear() != null) {
            ElmRequirement childResult = visitExpression(elm.getYear(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        if (elm.getMonth() != null) {
            ElmRequirement childResult = visitExpression(elm.getMonth(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        if (elm.getDay() != null) {
            ElmRequirement childResult = visitExpression(elm.getDay(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        return result;
    }

    @Override
    public ElmRequirement visitTime(Time elm, ElmRequirementsContext context) {
        ElmOperatorRequirement result = new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm);
        if (elm.getHour() != null) {
            ElmRequirement childResult = visitExpression(elm.getHour(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        if (elm.getMinute() != null) {
            ElmRequirement childResult = visitExpression(elm.getMinute(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        if (elm.getSecond() != null) {
            ElmRequirement childResult = visitExpression(elm.getSecond(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        if (elm.getMillisecond() != null) {
            ElmRequirement childResult = visitExpression(elm.getMillisecond(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement) childResult);
            }
        }
        return result;
    }

    @Override
    public ElmRequirement visitProperty(Property elm, ElmRequirementsContext context) {
        ElmRequirement visitResult = super.visitProperty(elm, context);

        // If the visit returns a property requirement, report as a qualified property
        if (visitResult instanceof ElmPropertyRequirement) {
            // The child is a property reference
            // Construct a new qualified property reference to report
            ElmPropertyRequirement visitPropertyRequirement = (ElmPropertyRequirement) visitResult;
            Property qualifiedProperty = new Property();
            Property sourceProperty = visitPropertyRequirement.getProperty();
            qualifiedProperty.setSource(sourceProperty.getSource());
            qualifiedProperty.setScope(sourceProperty.getScope());
            qualifiedProperty.setResultTypeName(elm.getResultTypeName());
            qualifiedProperty.setResultTypeSpecifier(elm.getResultTypeSpecifier());
            qualifiedProperty.setLocalId(sourceProperty.getLocalId());
            qualifiedProperty.setPath(sourceProperty.getPath() + "." + elm.getPath());
            Trackable.INSTANCE.setResultType(qualifiedProperty, Trackable.INSTANCE.getResultType(elm));
            return context.reportProperty(qualifiedProperty);
        }

        ElmPropertyRequirement propertyRequirement = context.reportProperty(elm);
        ElmRequirement result = aggregateResult(propertyRequirement, visitResult);
        return result;
    }

    @Override
    public ElmRequirement visitFields(AliasedQuerySource elm, ElmRequirementsContext context) {
        // Override visit behavior because we need to exit the definition context prior
        // to traversing the such that
        // condition
        // Such that traversal happens in the visitChildren relationship
        ElmRequirement result = defaultResult(elm, context);
        ElmQueryAliasContext aliasContext = null;
        context.getCurrentQueryContext().enterAliasDefinitionContext(elm);
        try {
            if (elm.getExpression() != null) {
                ElmRequirement childResult = visitElement(elm.getExpression(), context);
                result = aggregateResult(result, childResult);
            }
        } finally {
            aliasContext = context.getCurrentQueryContext().exitAliasDefinitionContext(result);
        }
        // If this is an operator requirement, report it directly to the context,
        // otherwise the context it contains will
        // not be reported
        // since query requirements are abstracted to an ElmDataRequirement
        if (result instanceof ElmOperatorRequirement) {
            context.reportRequirements(result, null);
        }
        return aliasContext.getRequirements();
    }

    @Override
    public ElmRequirement visitWith(With elm, ElmRequirementsContext context) {
        ElmRequirement result = visitFields((AliasedQuerySource) elm, context);

        if (elm.getSuchThat() != null) {
            ElmRequirement childResult = visitExpression(elm.getSuchThat(), context);
            context.getCurrentQueryContext().reportQueryRequirements(childResult);
            result = aggregateResult(result, childResult);
        }

        context.getCurrentQueryContext().descopeAlias(elm);

        return result;
    }

    @Override
    public ElmRequirement visitWithout(Without elm, ElmRequirementsContext context) {
        ElmRequirement result = visitFields((AliasedQuerySource) elm, context);

        if (elm.getSuchThat() != null) {
            ElmRequirement childResult = visitExpression(elm.getSuchThat(), context);
            result = aggregateResult(result, childResult);
        }

        context.getCurrentQueryContext().descopeAlias(elm);

        return result;
    }

    @Override
    public ElmRequirement visitAliasedQuerySource(AliasedQuerySource elm, ElmRequirementsContext context) {
        if (elm instanceof RelationshipClause) {
            return visitRelationshipClause((RelationshipClause) elm, context);
        }

        return visitFields(elm, context);
    }

    @Override
    public ElmRequirement visitLetClause(LetClause elm, ElmRequirementsContext context) {
        ElmRequirement result = defaultResult(elm, context);
        ElmQueryLetContext letContext = null;
        context.getCurrentQueryContext().enterLetDefinitionContext(elm);
        try {
            if (elm.getExpression() != null) {
                ElmRequirement childResult = super.visitLetClause(elm, context);
                result = aggregateResult(result, childResult);
            }
        } finally {
            letContext = context.getCurrentQueryContext().exitLetDefinitionContext(result);
        }
        return letContext.getRequirements();
    }

    protected ElmRequirement visitFields(Query elm, ElmRequirementsContext context) {
        ElmRequirement result = visitFields((Expression) elm, context);

        for (var source : elm.getSource()) {
            ElmRequirement childResult = visitAliasedQuerySource(source, context);
            result = aggregateResult(result, childResult);
        }
        for (var let : elm.getLet()) {
            ElmRequirement childResult = visitLetClause(let, context);
            result = aggregateResult(result, childResult);
        }

        for (var r : elm.getRelationship()) {
            ElmRequirement childResult = visitRelationshipClause(r, context);
            result = aggregateResult(result, childResult);
        }

        if (elm.getWhere() != null) {
            ElmRequirement childResult = visitExpression(elm.getWhere(), context);
            // This is the one line that's different between this implementation
            // and the super implementation
            context.getCurrentQueryContext().reportQueryRequirements(childResult);
            result = aggregateResult(result, childResult);
        }
        if (elm.getReturn() != null) {
            ElmRequirement childResult = visitReturnClause(elm.getReturn(), context);
            result = aggregateResult(result, childResult);
        }

        if (elm.getAggregate() != null) {
            ElmRequirement childResult = visitAggregateClause(elm.getAggregate(), context);
            result = aggregateResult(result, childResult);
        }

        if (elm.getSort() != null) {
            ElmRequirement childResult = visitSortClause(elm.getSort(), context);
            result = aggregateResult(result, childResult);
        }

        if (elm.getResultTypeSpecifier() != null) {
            ElmRequirement childResult = visitTypeSpecifier(elm.getResultTypeSpecifier(), context);
            result = aggregateResult(result, childResult);
        }
        return result;
    }

    @Override
    public ElmRequirement visitQuery(Query elm, ElmRequirementsContext context) {
        ElmRequirement childResult = null;
        ElmQueryContext queryContext = null;
        context.enterQueryContext(elm);
        try {
            childResult = visitFields(elm, context);
        } finally {
            queryContext = context.exitQueryContext();
        }
        ElmQueryRequirement result = queryContext.getQueryRequirement(childResult, context);
        result.analyzeDataRequirements(context);
        context.reportRequirements(result, null);
        return result;
    }

    @Override
    public ElmRequirement visitInCodeSystem(InCodeSystem elm, ElmRequirementsContext context) {
        if (elm.getCode() != null && (elm.getCodesystem() != null || elm.getCodesystemExpression() != null)) {
            ElmRequirement left = visitElement(elm.getCode(), context);
            ElmRequirement right = elm.getCodesystem() != null
                    ? visitElement(elm.getCodesystem(), context)
                    : visitElement(elm.getCodesystemExpression(), context);

            return inferConditionRequirement(elm, context, left, right);
        }
        return super.visitInCodeSystem(elm, context);
    }

    @Override
    public ElmRequirement visitAnyInCodeSystem(AnyInCodeSystem elm, ElmRequirementsContext context) {
        if (elm.getCodes() != null && (elm.getCodesystem() != null || elm.getCodesystemExpression() != null)) {
            ElmRequirement left = visitElement(elm.getCodes(), context);
            ElmRequirement right = elm.getCodesystem() != null
                    ? visitElement(elm.getCodesystem(), context)
                    : visitElement(elm.getCodesystemExpression(), context);

            return inferConditionRequirement(elm, context, left, right);
        }
        return super.visitAnyInCodeSystem(elm, context);
    }

    @Override
    public ElmRequirement visitInValueSet(InValueSet elm, ElmRequirementsContext context) {
        if (elm.getCode() != null && (elm.getValueset() != null || elm.getValuesetExpression() != null)) {
            ElmRequirement left = visitElement(elm.getCode(), context);
            ElmRequirement right = elm.getValueset() != null
                    ? visitElement(elm.getValueset(), context)
                    : visitElement(elm.getValuesetExpression(), context);

            return inferConditionRequirement(elm, context, left, right);
        }
        return super.visitInValueSet(elm, context);
    }

    @Override
    public ElmRequirement visitAnyInValueSet(AnyInValueSet elm, ElmRequirementsContext context) {
        if (elm.getCodes() != null && (elm.getValueset() != null || elm.getValuesetExpression() != null)) {
            ElmRequirement left = visitElement(elm.getCodes(), context);
            ElmRequirement right = elm.getValueset() != null
                    ? visitElement(elm.getValueset(), context)
                    : visitElement(elm.getValuesetExpression(), context);

            return inferConditionRequirement(elm, context, left, right);
        }
        return super.visitAnyInValueSet(elm, context);
    }

    @Override
    public ElmRequirement visitUsingDef(UsingDef elm, ElmRequirementsContext context) {
        context.reportUsingDef(elm);
        return super.visitUsingDef(elm, context);
    }
}
