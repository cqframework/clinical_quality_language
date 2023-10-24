package org.cqframework.cql.elm.requirements;

import org.cqframework.cql.elm.visiting.ElmBaseLibraryVisitor;
import org.hl7.cql.model.ListType;
import org.hl7.elm.r1.*;

import javax.xml.namespace.QName;

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
public class ElmRequirementsVisitor extends ElmBaseLibraryVisitor <ElmRequirement, ElmRequirementsContext>{

    public ElmRequirementsVisitor() {
        super();
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
            ((ElmRequirements)result).reportRequirement(nextResult);
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
        }
        finally {
            context.exitExpressionDef(result);
            if(pertinenceTagFound) {
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
            result = visitFunctionRef((FunctionRef)elm, context);
        }
        else {
            result = context.reportExpressionRef(elm);
        }
        if (result != null) {
            // If the expression ref is to a retrieve or a single-source query, surface it as an "inferred" requirement
            // in the referencing scope
            if (result instanceof ElmDataRequirement) {
                ElmDataRequirement inferredRequirement = ElmDataRequirement.inferFrom((ElmDataRequirement)result);
                // Should be being reported as a data requirement...
                //context.reportRetrieve(inferredRequirement.getRetrieve());
                result = inferredRequirement;
            }
            else if (result instanceof ElmQueryRequirement) {
                ElmDataRequirement inferredRequirement = ElmDataRequirement.inferFrom((ElmQueryRequirement)result);
                // Should be being reported as a data requirement...
                //context.reportRetrieve(inferredRequirement.getRetrieve());
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
            if (elm.getOperand().size() != 1 || (elm.getOperand().get(0).getResultType() instanceof ListType && !(elm.getResultType() instanceof ListType))) {
                // Note that the assumption here is that the data requirement has already been reported to the context
                return new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm).combine((ElmDataRequirement)result);
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
        if(elmPertinenceContext != null) {
            result.setPertinenceContext(elmPertinenceContext);
        }
        // If not analyzing requirements, or in a query context, report the data requirement
        // If in a query context, the requirement will be reported as an inferred requirement at the query boundary
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
    public ElmRequirement visitCodeSystemRef(CodeSystemRef elm, ElmRequirementsContext context){
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
        }
        finally {
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
    public ElmRequirement visitCodeRef(CodeRef elm, ElmRequirementsContext context){
        context.reportCodeRef(elm);
        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitCodeDef(CodeDef elm, ElmRequirementsContext context){
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

    @Override
    public ElmRequirement visitExpression(Expression elm, ElmRequirementsContext context) {
        return super.visitExpression(elm, context);
    }

    @Override
    public ElmRequirement visitOperatorExpression(OperatorExpression elm, ElmRequirementsContext context) {
        return super.visitOperatorExpression(elm, context);
    }

    @Override
    public ElmRequirement visitUnaryExpression(UnaryExpression elm, ElmRequirementsContext context) {
        return super.visitUnaryExpression(elm, context);
    }

    /**
     * If both sides are column references that point to the same column in the same alias
     *   the condition is a tautology
     * If both sides are column references that point to different columns in the same alias
     *   the condition is a constraint
     * If both sides are column references that point to different aliases
     *   the condition is a join
     * If one side or the other is a column reference
     *   the condition is a potentially sargeable condition
     * @param elm
     * @param context
     * @param left
     * @param right
     * @return
     */
    protected ElmRequirement inferConditionRequirement(Expression elm, ElmRequirementsContext context, ElmRequirement left, ElmRequirement right) {
        ElmPropertyRequirement leftProperty = left instanceof ElmPropertyRequirement ? (ElmPropertyRequirement)left : null;
        ElmPropertyRequirement rightProperty = right instanceof ElmPropertyRequirement ? (ElmPropertyRequirement)right : null;
        if (leftProperty != null && leftProperty.getInCurrentScope()) {
            if (rightProperty != null && rightProperty.getInCurrentScope()) {
                if (leftProperty.getSource() == rightProperty.getSource()) {
                    return new ElmConstraintRequirement(context.getCurrentLibraryIdentifier(), elm, leftProperty, rightProperty);
                }
                else if (leftProperty.getSource() instanceof AliasedQuerySource && rightProperty.getSource() instanceof AliasedQuerySource) {
                    return new ElmJoinRequirement(context.getCurrentLibraryIdentifier(), elm, leftProperty, rightProperty);
                }
            }
            if (right instanceof ElmExpressionRequirement) {
                return new ElmConditionRequirement(context.getCurrentLibraryIdentifier(), elm, leftProperty, (ElmExpressionRequirement)right);
            }
        }
        else if (rightProperty != null && rightProperty.getInCurrentScope()) {
            if (leftProperty != null && leftProperty.getInCurrentScope()) {
                if (leftProperty.getSource() == rightProperty.getSource()) {
                    return new ElmConstraintRequirement(context.getCurrentLibraryIdentifier(), elm, leftProperty, rightProperty);
                }
                else if (leftProperty.getSource() instanceof AliasedQuerySource && rightProperty.getSource() instanceof AliasedQuerySource) {
                    return new ElmJoinRequirement(context.getCurrentLibraryIdentifier(), elm, leftProperty, rightProperty);
                }
            }
            if (left instanceof ElmExpressionRequirement) {
                return new ElmConditionRequirement(context.getCurrentLibraryIdentifier(), elm, (ElmPropertyRequirement)right, (ElmExpressionRequirement)left);
            }
        }

        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitChildren(BinaryExpression elm, ElmRequirementsContext context) {
        // Override visit children behavior to determine whether to create an ElmConditionRequirement
        if (elm.getOperand().size() != 2) {
            return super.visitChildren(elm, context);
        }

        switch (elm.getClass().getSimpleName()) {
            /**
             * Determine whether the condition is sargeable:
             *
             *     A op B
             *
             * Where:
             * * A is an order-preserving expression with a single property reference to a property of some source in the current query context
             * * op is a positive relative comparison operation (=, >, <, >=, <=) or a membership operator (in, contains)
             * * B is a functional, repeatable, and deterministic context literal expression with respect to the current query context
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
             * Gather sargeable conditions as Lists of conditions. At an AND, combine conditions from sub-nodes.
             * At an OR, the result is separate lists of condition lists.
             * At an AND, if there are already lists of lists, the condition is too complex for analysis (i.e. it's not in DNF or CNF)
             */
            // TODO: Normalize to DNF
            case "And": {
                ElmRequirement left = visitElement(elm.getOperand().get(0), context);
                ElmRequirement right = visitElement(elm.getOperand().get(1), context);

                if (left instanceof ElmExpressionRequirement && right instanceof ElmExpressionRequirement) {
                    return new ElmConjunctiveRequirement(context.getCurrentLibraryIdentifier(), elm)
                            .combine((ElmExpressionRequirement)left)
                            .combine((ElmExpressionRequirement)right);
                }
                else if (left instanceof ElmExpressionRequirement && right == null) {
                    return left;
                }
                else if (right instanceof ElmExpressionRequirement && left == null) {
                    return right;
                }

                return aggregateResult(left, right);
            }

            case "Or": {
                ElmRequirement left = visitElement(elm.getOperand().get(0), context);
                ElmRequirement right = visitElement(elm.getOperand().get(1), context);

                if (left instanceof ElmExpressionRequirement && right instanceof ElmExpressionRequirement) {
                    return new ElmDisjunctiveRequirement(context.getCurrentLibraryIdentifier(), elm)
                            .combine((ElmExpressionRequirement)left)
                            .combine((ElmExpressionRequirement)right);
                }
                else if (left instanceof ElmExpressionRequirement && right == null) {
                    return left;
                }
                else if (right instanceof ElmExpressionRequirement && left == null) {
                    return right;
                }

                return aggregateResult(left, right);
            }

            // TODO: Rewrite
            case "Xor":
            case "Implies":
            //case "Not":
            //case "NotEqual":
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
                super.visitChildren(elm, context);
                return new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm);
            }
        }
    }

    @Override
    public ElmRequirement visitBinaryExpression(BinaryExpression elm, ElmRequirementsContext context) {
        return super.visitBinaryExpression(elm, context);
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
    public ElmRequirement visitOperandDef(OperandDef elm, ElmRequirementsContext context) {
        return super.visitOperandDef(elm, context);
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
    public ElmRequirement visitTupleElement(TupleElement elm, ElmRequirementsContext context) {
        return super.visitTupleElement(elm, context);
    }

    @Override
    public ElmRequirement visitTuple(Tuple elm, ElmRequirementsContext context) {
        return super.visitTuple(elm, context);
    }

    @Override
    public ElmRequirement visitInstanceElement(InstanceElement elm, ElmRequirementsContext context) {
        return super.visitInstanceElement(elm, context);
    }

    @Override
    public ElmRequirement visitInstance(Instance elm, ElmRequirementsContext context) {
        return super.visitInstance(elm, context);
    }

    @Override
    public ElmRequirement visitInterval(Interval elm, ElmRequirementsContext context) {
        ElmRequirement result = super.visitInterval(elm, context);
        ElmOperatorRequirement finalResult = new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm);
        finalResult.combine(result);
        return finalResult;
    }

    @Override
    public ElmRequirement visitList(List elm, ElmRequirementsContext context) {
        return super.visitList(elm, context);
    }

    @Override
    public ElmRequirement visitAnd(And elm, ElmRequirementsContext context) {
        return super.visitAnd(elm, context);
    }

    @Override
    public ElmRequirement visitOr(Or elm, ElmRequirementsContext context) {
        return super.visitOr(elm, context);
    }

    @Override
    public ElmRequirement visitXor(Xor elm, ElmRequirementsContext context) {
        return super.visitXor(elm, context);
    }

    @Override
    public ElmRequirement visitNot(Not elm, ElmRequirementsContext context) {
        return super.visitNot(elm, context);
    }

    @Override
    public ElmRequirement visitIf(If elm, ElmRequirementsContext context) {
        // TODO: Rewrite the if as equivalent logic
        return new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitCaseItem(CaseItem elm, ElmRequirementsContext context) {
        return super.visitCaseItem(elm, context);
    }

    @Override
    public ElmRequirement visitCase(Case elm, ElmRequirementsContext context) {
        // TODO: Rewrite the case as equivalent logic
        ElmOperatorRequirement result = new ElmOperatorRequirement(context.getCurrentLibraryIdentifier(), elm);
        ElmRequirement childResult = null;
        if (elm.getComparand() != null) {
            childResult = this.visitElement(elm.getComparand(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
            }
        }

        for (CaseItem ci : elm.getCaseItem()) {
            childResult = this.visitElement(ci, context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
            }
        }

        if (elm.getElse() != null) {
            childResult = this.visitElement(elm.getElse(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
            }
        }

        return result;
    }

    @Override
    public ElmRequirement visitNull(Null elm, ElmRequirementsContext context) {
        return new ElmExpressionRequirement(context.getCurrentLibraryIdentifier(), elm);
    }

    @Override
    public ElmRequirement visitIsNull(IsNull elm, ElmRequirementsContext context) {
        return super.visitIsNull(elm, context);
    }

    @Override
    public ElmRequirement visitIsTrue(IsTrue elm, ElmRequirementsContext context) {
        return super.visitIsTrue(elm, context);
    }

    @Override
    public ElmRequirement visitIsFalse(IsFalse elm, ElmRequirementsContext context) {
        return super.visitIsFalse(elm, context);
    }

    @Override
    public ElmRequirement visitCoalesce(Coalesce elm, ElmRequirementsContext context) {
        return super.visitCoalesce(elm, context);
    }

    @Override
    public ElmRequirement visitIs(Is elm, ElmRequirementsContext context) {
        return super.visitIs(elm, context);
    }

    @Override
    public ElmRequirement visitAs(As elm, ElmRequirementsContext context) {
        return super.visitAs(elm, context);
    }

    @Override
    public ElmRequirement visitConvert(Convert elm, ElmRequirementsContext context) {
        return super.visitConvert(elm, context);
    }

    @Override
    public ElmRequirement visitToBoolean(ToBoolean elm, ElmRequirementsContext context) {
        return super.visitToBoolean(elm, context);
    }

    @Override
    public ElmRequirement visitToConcept(ToConcept elm, ElmRequirementsContext context) {
        return super.visitToConcept(elm, context);
    }

    @Override
    public ElmRequirement visitToDateTime(ToDateTime elm, ElmRequirementsContext context) {
        return super.visitToDateTime(elm, context);
    }

    @Override
    public ElmRequirement visitToDecimal(ToDecimal elm, ElmRequirementsContext context) {
        return super.visitToDecimal(elm, context);
    }

    @Override
    public ElmRequirement visitToInteger(ToInteger elm, ElmRequirementsContext context) {
        return super.visitToInteger(elm, context);
    }

    @Override
    public ElmRequirement visitToQuantity(ToQuantity elm, ElmRequirementsContext context) {
        return super.visitToQuantity(elm, context);
    }

    @Override
    public ElmRequirement visitToString(ToString elm, ElmRequirementsContext context) {
        return super.visitToString(elm, context);
    }

    @Override
    public ElmRequirement visitToTime(ToTime elm, ElmRequirementsContext context) {
        return super.visitToTime(elm, context);
    }

    @Override
    public ElmRequirement visitEqual(Equal elm, ElmRequirementsContext context) {
        return super.visitEqual(elm, context);
    }

    @Override
    public ElmRequirement visitEquivalent(Equivalent elm, ElmRequirementsContext context) {
        return super.visitEquivalent(elm, context);
    }

    @Override
    public ElmRequirement visitNotEqual(NotEqual elm, ElmRequirementsContext context) {
        return super.visitNotEqual(elm, context);
    }

    @Override
    public ElmRequirement visitLess(Less elm, ElmRequirementsContext context) {
        return super.visitLess(elm, context);
    }

    @Override
    public ElmRequirement visitGreater(Greater elm, ElmRequirementsContext context) {
        return super.visitGreater(elm, context);
    }

    @Override
    public ElmRequirement visitLessOrEqual(LessOrEqual elm, ElmRequirementsContext context) {
        return super.visitLessOrEqual(elm, context);
    }

    @Override
    public ElmRequirement visitGreaterOrEqual(GreaterOrEqual elm, ElmRequirementsContext context) {
        return super.visitGreaterOrEqual(elm, context);
    }

    @Override
    public ElmRequirement visitAdd(Add elm, ElmRequirementsContext context) {
        return super.visitAdd(elm, context);
    }

    @Override
    public ElmRequirement visitSubtract(Subtract elm, ElmRequirementsContext context) {
        return super.visitSubtract(elm, context);
    }

    @Override
    public ElmRequirement visitMultiply(Multiply elm, ElmRequirementsContext context) {
        return super.visitMultiply(elm, context);
    }

    @Override
    public ElmRequirement visitDivide(Divide elm, ElmRequirementsContext context) {
        return super.visitDivide(elm, context);
    }

    @Override
    public ElmRequirement visitTruncatedDivide(TruncatedDivide elm, ElmRequirementsContext context) {
        return super.visitTruncatedDivide(elm, context);
    }

    @Override
    public ElmRequirement visitModulo(Modulo elm, ElmRequirementsContext context) {
        return super.visitModulo(elm, context);
    }

    @Override
    public ElmRequirement visitCeiling(Ceiling elm, ElmRequirementsContext context) {
        return super.visitCeiling(elm, context);
    }

    @Override
    public ElmRequirement visitFloor(Floor elm, ElmRequirementsContext context) {
        return super.visitFloor(elm, context);
    }

    @Override
    public ElmRequirement visitTruncate(Truncate elm, ElmRequirementsContext context) {
        return super.visitTruncate(elm, context);
    }

    @Override
    public ElmRequirement visitAbs(Abs elm, ElmRequirementsContext context) {
        return super.visitAbs(elm, context);
    }

    @Override
    public ElmRequirement visitNegate(Negate elm, ElmRequirementsContext context) {
        return super.visitNegate(elm, context);
    }

    @Override
    public ElmRequirement visitRound(Round elm, ElmRequirementsContext context) {
        return super.visitRound(elm, context);
    }

    @Override
    public ElmRequirement visitLn(Ln elm, ElmRequirementsContext context) {
        return super.visitLn(elm, context);
    }

    @Override
    public ElmRequirement visitExp(Exp elm, ElmRequirementsContext context) {
        return super.visitExp(elm, context);
    }

    @Override
    public ElmRequirement visitLog(Log elm, ElmRequirementsContext context) {
        return super.visitLog(elm, context);
    }

    @Override
    public ElmRequirement visitPower(Power elm, ElmRequirementsContext context) {
        return super.visitPower(elm, context);
    }

    @Override
    public ElmRequirement visitSuccessor(Successor elm, ElmRequirementsContext context) {
        return super.visitSuccessor(elm, context);
    }

    @Override
    public ElmRequirement visitPredecessor(Predecessor elm, ElmRequirementsContext context) {
        return super.visitPredecessor(elm, context);
    }

    @Override
    public ElmRequirement visitMinValue(MinValue elm, ElmRequirementsContext context) {
        return super.visitMinValue(elm, context);
    }

    @Override
    public ElmRequirement visitMaxValue(MaxValue elm, ElmRequirementsContext context) {
        return super.visitMaxValue(elm, context);
    }

    @Override
    public ElmRequirement visitConcatenate(Concatenate elm, ElmRequirementsContext context) {
        return super.visitConcatenate(elm, context);
    }

    @Override
    public ElmRequirement visitCombine(Combine elm, ElmRequirementsContext context) {
        return super.visitCombine(elm, context);
    }

    @Override
    public ElmRequirement visitSplit(Split elm, ElmRequirementsContext context) {
        // If the separator is a literal, infer based only on the string to split argument
        if (elm.getSeparator() instanceof Literal) {
            return visitElement(elm.getStringToSplit(), context);
        }
        return super.visitSplit(elm, context);
    }

    @Override
    public ElmRequirement visitLength(Length elm, ElmRequirementsContext context) {
        return super.visitLength(elm, context);
    }

    @Override
    public ElmRequirement visitUpper(Upper elm, ElmRequirementsContext context) {
        return super.visitUpper(elm, context);
    }

    @Override
    public ElmRequirement visitLower(Lower elm, ElmRequirementsContext context) {
        return super.visitLower(elm, context);
    }

    @Override
    public ElmRequirement visitIndexer(Indexer elm, ElmRequirementsContext context) {
        return super.visitIndexer(elm, context);
    }

    @Override
    public ElmRequirement visitPositionOf(PositionOf elm, ElmRequirementsContext context) {
        return super.visitPositionOf(elm, context);
    }

    @Override
    public ElmRequirement visitSubstring(Substring elm, ElmRequirementsContext context) {
        return super.visitSubstring(elm, context);
    }

    @Override
    public ElmRequirement visitDurationBetween(DurationBetween elm, ElmRequirementsContext context) {
        return super.visitDurationBetween(elm, context);
    }

    @Override
    public ElmRequirement visitDifferenceBetween(DifferenceBetween elm, ElmRequirementsContext context) {
        return super.visitDifferenceBetween(elm, context);
    }

    @Override
    public ElmRequirement visitDateFrom(DateFrom elm, ElmRequirementsContext context) {
        return super.visitDateFrom(elm, context);
    }

    @Override
    public ElmRequirement visitTimeFrom(TimeFrom elm, ElmRequirementsContext context) {
        return super.visitTimeFrom(elm, context);
    }

    @Override
    public ElmRequirement visitTimezoneOffsetFrom(TimezoneOffsetFrom elm, ElmRequirementsContext context) {
        return super.visitTimezoneOffsetFrom(elm, context);
    }

    @Override
    public ElmRequirement visitDateTimeComponentFrom(DateTimeComponentFrom elm, ElmRequirementsContext context) {
        return super.visitDateTimeComponentFrom(elm, context);
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
                result.combine((ElmExpressionRequirement)childResult);
            }
        }
        if (elm.getMonth() != null) {
            ElmRequirement childResult = visitExpression(elm.getMonth(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
            }
        }
        if (elm.getDay() != null) {
            ElmRequirement childResult = visitExpression(elm.getDay(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
            }
        }
        if (elm.getHour() != null) {
            ElmRequirement childResult = visitExpression(elm.getHour(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
            }
        }
        if (elm.getMinute() != null) {
            ElmRequirement childResult = visitExpression(elm.getMinute(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
            }
        }
        if (elm.getSecond() != null) {
            ElmRequirement childResult = visitExpression(elm.getSecond(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
            }
        }
        if (elm.getMillisecond() != null) {
            ElmRequirement childResult = visitExpression(elm.getMillisecond(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
            }
        }
        if (elm.getTimezoneOffset() != null) {
            ElmRequirement childResult = visitExpression(elm.getTimezoneOffset(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
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
                result.combine((ElmExpressionRequirement)childResult);
            }
        }
        if (elm.getMonth() != null) {
            ElmRequirement childResult = visitExpression(elm.getMonth(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
            }
        }
        if (elm.getDay() != null) {
            ElmRequirement childResult = visitExpression(elm.getDay(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
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
                result.combine((ElmExpressionRequirement)childResult);
            }
        }
        if (elm.getMinute() != null) {
            ElmRequirement childResult = visitExpression(elm.getMinute(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
            }
        }
        if (elm.getSecond() != null) {
            ElmRequirement childResult = visitExpression(elm.getSecond(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
            }
        }
        if (elm.getMillisecond() != null) {
            ElmRequirement childResult = visitExpression(elm.getMillisecond(), context);
            if (childResult instanceof ElmExpressionRequirement) {
                result.combine((ElmExpressionRequirement)childResult);
            }
        }
        return result;
    }

    @Override
    public ElmRequirement visitSameAs(SameAs elm, ElmRequirementsContext context) {
        return super.visitSameAs(elm, context);
    }

    @Override
    public ElmRequirement visitSameOrBefore(SameOrBefore elm, ElmRequirementsContext context) {
        return super.visitSameOrBefore(elm, context);
    }

    @Override
    public ElmRequirement visitSameOrAfter(SameOrAfter elm, ElmRequirementsContext context) {
        return super.visitSameOrAfter(elm, context);
    }

    @Override
    public ElmRequirement visitWidth(Width elm, ElmRequirementsContext context) {
        return super.visitWidth(elm, context);
    }

    @Override
    public ElmRequirement visitStart(Start elm, ElmRequirementsContext context) {
        return super.visitStart(elm, context);
    }

    @Override
    public ElmRequirement visitEnd(End elm, ElmRequirementsContext context) {
        return super.visitEnd(elm, context);
    }

    @Override
    public ElmRequirement visitContains(Contains elm, ElmRequirementsContext context) {
        return super.visitContains(elm, context);
    }

    @Override
    public ElmRequirement visitProperContains(ProperContains elm, ElmRequirementsContext context) {
        return super.visitProperContains(elm, context);
    }

    @Override
    public ElmRequirement visitIn(In elm, ElmRequirementsContext context) {
        return super.visitIn(elm, context);
    }

    @Override
    public ElmRequirement visitProperIn(ProperIn elm, ElmRequirementsContext context) {
        return super.visitProperIn(elm, context);
    }

    @Override
    public ElmRequirement visitIncludes(Includes elm, ElmRequirementsContext context) {
        return super.visitIncludes(elm, context);
    }

    @Override
    public ElmRequirement visitIncludedIn(IncludedIn elm, ElmRequirementsContext context) {
        return super.visitIncludedIn(elm, context);
    }

    @Override
    public ElmRequirement visitProperIncludes(ProperIncludes elm, ElmRequirementsContext context) {
        return super.visitProperIncludes(elm, context);
    }

    @Override
    public ElmRequirement visitProperIncludedIn(ProperIncludedIn elm, ElmRequirementsContext context) {
        return super.visitProperIncludedIn(elm, context);
    }

    @Override
    public ElmRequirement visitBefore(Before elm, ElmRequirementsContext context) {
        return super.visitBefore(elm, context);
    }

    @Override
    public ElmRequirement visitAfter(After elm, ElmRequirementsContext context) {
        return super.visitAfter(elm, context);
    }

    @Override
    public ElmRequirement visitMeets(Meets elm, ElmRequirementsContext context) {
        return super.visitMeets(elm, context);
    }

    @Override
    public ElmRequirement visitMeetsBefore(MeetsBefore elm, ElmRequirementsContext context) {
        return super.visitMeetsBefore(elm, context);
    }

    @Override
    public ElmRequirement visitMeetsAfter(MeetsAfter elm, ElmRequirementsContext context) {
        return super.visitMeetsAfter(elm, context);
    }

    @Override
    public ElmRequirement visitOverlaps(Overlaps elm, ElmRequirementsContext context) {
        return super.visitOverlaps(elm, context);
    }

    @Override
    public ElmRequirement visitOverlapsBefore(OverlapsBefore elm, ElmRequirementsContext context) {
        return super.visitOverlapsBefore(elm, context);
    }

    @Override
    public ElmRequirement visitOverlapsAfter(OverlapsAfter elm, ElmRequirementsContext context) {
        return super.visitOverlapsAfter(elm, context);
    }

    @Override
    public ElmRequirement visitStarts(Starts elm, ElmRequirementsContext context) {
        return super.visitStarts(elm, context);
    }

    @Override
    public ElmRequirement visitEnds(Ends elm, ElmRequirementsContext context) {
        return super.visitEnds(elm, context);
    }

    @Override
    public ElmRequirement visitCollapse(Collapse elm, ElmRequirementsContext context) {
        return super.visitCollapse(elm, context);
    }

    @Override
    public ElmRequirement visitUnion(Union elm, ElmRequirementsContext context) {
        return super.visitUnion(elm, context);
    }

    @Override
    public ElmRequirement visitIntersect(Intersect elm, ElmRequirementsContext context) {
        return super.visitIntersect(elm, context);
    }

    @Override
    public ElmRequirement visitExcept(Except elm, ElmRequirementsContext context) {
        return super.visitExcept(elm, context);
    }

    @Override
    public ElmRequirement visitExists(Exists elm, ElmRequirementsContext context) {
        return super.visitExists(elm, context);
    }

    @Override
    public ElmRequirement visitTimes(Times elm, ElmRequirementsContext context) {
        return super.visitTimes(elm, context);
    }

    @Override
    public ElmRequirement visitFilter(Filter elm, ElmRequirementsContext context) {
        return super.visitFilter(elm, context);
    }

    @Override
    public ElmRequirement visitFirst(First elm, ElmRequirementsContext context) {
        return super.visitFirst(elm, context);
    }

    @Override
    public ElmRequirement visitLast(Last elm, ElmRequirementsContext context) {
        return super.visitLast(elm, context);
    }

    @Override
    public ElmRequirement visitIndexOf(IndexOf elm, ElmRequirementsContext context) {
        return super.visitIndexOf(elm, context);
    }

    @Override
    public ElmRequirement visitFlatten(Flatten elm, ElmRequirementsContext context) {
        return super.visitFlatten(elm, context);
    }

    @Override
    public ElmRequirement visitSort(Sort elm, ElmRequirementsContext context) {
        return super.visitSort(elm, context);
    }

    @Override
    public ElmRequirement visitForEach(ForEach elm, ElmRequirementsContext context) {
        return super.visitForEach(elm, context);
    }

    @Override
    public ElmRequirement visitDistinct(Distinct elm, ElmRequirementsContext context) {
        return super.visitDistinct(elm, context);
    }

    @Override
    public ElmRequirement visitCurrent(Current elm, ElmRequirementsContext context) {
        return super.visitCurrent(elm, context);
    }

    @Override
    public ElmRequirement visitSingletonFrom(SingletonFrom elm, ElmRequirementsContext context) {
        return super.visitSingletonFrom(elm, context);
    }

    @Override
    public ElmRequirement visitAggregateExpression(AggregateExpression elm, ElmRequirementsContext context) {
        return super.visitAggregateExpression(elm, context);
    }

    @Override
    public ElmRequirement visitCount(Count elm, ElmRequirementsContext context) {
        return super.visitCount(elm, context);
    }

    @Override
    public ElmRequirement visitSum(Sum elm, ElmRequirementsContext context) {
        return super.visitSum(elm, context);
    }

    @Override
    public ElmRequirement visitMin(Min elm, ElmRequirementsContext context) {
        return super.visitMin(elm, context);
    }

    @Override
    public ElmRequirement visitMax(Max elm, ElmRequirementsContext context) {
        return super.visitMax(elm, context);
    }

    @Override
    public ElmRequirement visitAvg(Avg elm, ElmRequirementsContext context) {
        return super.visitAvg(elm, context);
    }

    @Override
    public ElmRequirement visitMedian(Median elm, ElmRequirementsContext context) {
        return super.visitMedian(elm, context);
    }

    @Override
    public ElmRequirement visitMode(Mode elm, ElmRequirementsContext context) {
        return super.visitMode(elm, context);
    }

    @Override
    public ElmRequirement visitVariance(Variance elm, ElmRequirementsContext context) {
        return super.visitVariance(elm, context);
    }

    @Override
    public ElmRequirement visitPopulationVariance(PopulationVariance elm, ElmRequirementsContext context) {
        return super.visitPopulationVariance(elm, context);
    }

    @Override
    public ElmRequirement visitStdDev(StdDev elm, ElmRequirementsContext context) {
        return super.visitStdDev(elm, context);
    }

    @Override
    public ElmRequirement visitPopulationStdDev(PopulationStdDev elm, ElmRequirementsContext context) {
        return super.visitPopulationStdDev(elm, context);
    }

    @Override
    public ElmRequirement visitAllTrue(AllTrue elm, ElmRequirementsContext context) {
        return super.visitAllTrue(elm, context);
    }

    @Override
    public ElmRequirement visitAnyTrue(AnyTrue elm, ElmRequirementsContext context) {
        return super.visitAnyTrue(elm, context);
    }

    @Override
    public ElmRequirement visitProperty(Property elm, ElmRequirementsContext context) {
        ElmRequirement visitResult = super.visitProperty(elm, context);

        // If the visit returns a property requirement, report as a qualified property
        if (visitResult instanceof ElmPropertyRequirement) {
            // The child is a property reference
            // Construct a new qualified property reference to report
            ElmPropertyRequirement visitPropertyRequirement = (ElmPropertyRequirement)visitResult;
            Property qualifiedProperty = new Property();
            Property sourceProperty = visitPropertyRequirement.getProperty();
            qualifiedProperty.setSource(sourceProperty.getSource());
            qualifiedProperty.setScope(sourceProperty.getScope());
            qualifiedProperty.setResultType(elm.getResultType());
            qualifiedProperty.setResultTypeName(elm.getResultTypeName());
            qualifiedProperty.setResultTypeSpecifier(elm.getResultTypeSpecifier());
            qualifiedProperty.setLocalId(sourceProperty.getLocalId());
            qualifiedProperty.setPath(sourceProperty.getPath() + "." + elm.getPath());
            return context.reportProperty(qualifiedProperty);
        }

        ElmPropertyRequirement propertyRequirement = context.reportProperty(elm);
        ElmRequirement result = aggregateResult(propertyRequirement, visitResult);
        return result;
    }

    @Override
    public ElmRequirement visitChildren(AliasedQuerySource elm, ElmRequirementsContext context) {
        // Override visit behavior because we need to exit the definition context prior to traversing the such that condition
        // Such that traversal happens in the visitChildren relationship
        ElmRequirement result = defaultResult(elm, context);
        ElmQueryAliasContext aliasContext = null;
        context.getCurrentQueryContext().enterAliasDefinitionContext(elm);
        try {
            if (elm.getExpression() != null) {
                ElmRequirement childResult = visitElement(elm.getExpression(), context);
                result = aggregateResult(result, childResult);
            }
        }
        finally {
            aliasContext = context.getCurrentQueryContext().exitAliasDefinitionContext(result);
        }
        // If this is an operator requirement, report it directly to the context, otherwise the context it contains will not be reported
        // since query requirements are abstracted to an ElmDataRequirement
        if (result instanceof ElmOperatorRequirement) {
            context.reportRequirements(result, null);
        }
        return aliasContext.getRequirements();
    }

    @Override
    public ElmRequirement visitAliasedQuerySource(AliasedQuerySource elm, ElmRequirementsContext context) {
        return super.visitAliasedQuerySource(elm, context);
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
        }
        finally {
            letContext = context.getCurrentQueryContext().exitLetDefinitionContext(result);
        }
        return letContext.getRequirements();
    }

    @Override
    public ElmRequirement visitChildren(RelationshipClause elm, ElmRequirementsContext context) {
        ElmRequirement result = visitChildren((AliasedQuerySource)elm, context);

        if (elm.getSuchThat() != null) {
            ElmRequirement childResult = visitSuchThatClause(elm.getSuchThat(), elm instanceof With, context);
            result = aggregateResult(result, childResult);
        }

        context.getCurrentQueryContext().descopeAlias(elm);

        return result;
    }

    @Override
    public ElmRequirement visitRelationshipClause(RelationshipClause elm, ElmRequirementsContext context) {
        return super.visitRelationshipClause(elm, context);
    }

    @Override
    public ElmRequirement visitWith(With elm, ElmRequirementsContext context) {
        return super.visitWith(elm, context);
    }

    @Override
    public ElmRequirement visitWithout(Without elm, ElmRequirementsContext context) {
        return super.visitWithout(elm, context);
    }

    @Override
    public ElmRequirement visitSortByItem(SortByItem elm, ElmRequirementsContext context) {
        return super.visitSortByItem(elm, context);
    }

    @Override
    public ElmRequirement visitByDirection(ByDirection elm, ElmRequirementsContext context) {
        return super.visitByDirection(elm, context);
    }

    @Override
    public ElmRequirement visitByColumn(ByColumn elm, ElmRequirementsContext context) {
        return super.visitByColumn(elm, context);
    }

    @Override
    public ElmRequirement visitByExpression(ByExpression elm, ElmRequirementsContext context) {
        return super.visitByExpression(elm, context);
    }

    @Override
    public ElmRequirement visitSortClause(SortClause elm, ElmRequirementsContext context) {
        return super.visitSortClause(elm, context);
    }

    @Override
    public ElmRequirement visitReturnClause(ReturnClause elm, ElmRequirementsContext context) {
        return super.visitReturnClause(elm, context);
    }

    @Override
    public ElmRequirement visitWhereClause(Expression elm, ElmRequirementsContext context) {
        ElmRequirement childResult = super.visitWhereClause(elm, context);
        context.getCurrentQueryContext().reportQueryRequirements(childResult);
        return childResult;
    }

    @Override
    public ElmRequirement visitSuchThatClause(Expression elm, boolean isWith, ElmRequirementsContext context) {
        ElmRequirement childResult = super.visitSuchThatClause(elm, isWith, context);
        if (isWith) {
            context.getCurrentQueryContext().reportQueryRequirements(childResult);
        }
        // TODO: Determine how to incorporate requirements from a without clause
        return childResult;
    }

    @Override
    public ElmRequirement visitQuery(Query elm, ElmRequirementsContext context) {
        ElmRequirement childResult = null;
        ElmQueryContext queryContext = null;
        context.enterQueryContext(elm);
        try {
            childResult = super.visitQuery(elm, context);
        }
        finally {
            queryContext = context.exitQueryContext();
        }
        ElmQueryRequirement result = queryContext.getQueryRequirement(childResult, context);
        result.analyzeDataRequirements(context);
        context.reportRequirements(result, null);
        return result;
    }

    @Override
    public ElmRequirement visitAliasRef(AliasRef elm, ElmRequirementsContext context) {
        return super.visitAliasRef(elm, context);
    }

    @Override
    public ElmRequirement visitQueryLetRef(QueryLetRef elm, ElmRequirementsContext context) {
        return super.visitQueryLetRef(elm, context);
    }

    @Override
    public ElmRequirement visitCode(Code elm, ElmRequirementsContext context) {
        return super.visitCode(elm, context);
    }

    @Override
    public ElmRequirement visitConcept(Concept elm, ElmRequirementsContext context) {
        return super.visitConcept(elm, context);
    }

    @Override
    public ElmRequirement visitInCodeSystem(InCodeSystem elm, ElmRequirementsContext context) {
        if (elm.getCode() != null && (elm.getCodesystem() != null || elm.getCodesystemExpression() != null)) {
            ElmRequirement left = visitElement(elm.getCode(), context);
            ElmRequirement right =
                    elm.getCodesystem() != null
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
            ElmRequirement right =
                    elm.getCodesystem() != null
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
            ElmRequirement right =
                    elm.getValueset() != null
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
            ElmRequirement right =
                    elm.getValueset() != null
                        ? visitElement(elm.getValueset(), context)
                        : visitElement(elm.getValuesetExpression(), context);

            return inferConditionRequirement(elm, context, left, right);
        }
        return super.visitAnyInValueSet(elm, context);
    }

    @Override
    public ElmRequirement visitQuantity(Quantity elm, ElmRequirementsContext context) {
        return super.visitQuantity(elm, context);
    }

    @Override
    public ElmRequirement visitCalculateAge(CalculateAge elm, ElmRequirementsContext context) {
        return super.visitCalculateAge(elm, context);
    }

    @Override
    public ElmRequirement visitCalculateAgeAt(CalculateAgeAt elm, ElmRequirementsContext context) {
        return super.visitCalculateAgeAt(elm, context);
    }

    @Override
    public ElmRequirement visitElement(Element elm, ElmRequirementsContext context) {
        return super.visitElement(elm, context);
    }

    @Override
    public ElmRequirement visitTypeSpecifier(TypeSpecifier elm, ElmRequirementsContext context) {
        return super.visitTypeSpecifier(elm, context);
    }

    @Override
    public ElmRequirement visitNamedTypeSpecifier(NamedTypeSpecifier elm, ElmRequirementsContext context) {
        return super.visitNamedTypeSpecifier(elm, context);
    }

    @Override
    public ElmRequirement visitIntervalTypeSpecifier(IntervalTypeSpecifier elm, ElmRequirementsContext context) {
        return super.visitIntervalTypeSpecifier(elm, context);
    }

    @Override
    public ElmRequirement visitListTypeSpecifier(ListTypeSpecifier elm, ElmRequirementsContext context) {
        return super.visitListTypeSpecifier(elm, context);
    }

    @Override
    public ElmRequirement visitTupleElementDefinition(TupleElementDefinition elm, ElmRequirementsContext context) {
        return super.visitTupleElementDefinition(elm, context);
    }

    @Override
    public ElmRequirement visitTupleTypeSpecifier(TupleTypeSpecifier elm, ElmRequirementsContext context) {
        return super.visitTupleTypeSpecifier(elm, context);
    }

    @Override
    public ElmRequirement visitUsingDef(UsingDef elm, ElmRequirementsContext context) {
        context.reportUsingDef(elm);
        return super.visitUsingDef(elm, context);
    }
}
