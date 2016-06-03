package org.cqframework.cql.elm.visiting;

import org.hl7.elm.r1.*;

/**
 * Provides the base implementation for an ElmVisitor.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * @param <C> The type of context passed to each visit method
 * operations with no return type.
 */
public class ElmBaseVisitor<T, C> implements ElmVisitor<T, C> {
    /**
     * Visit an Element in an ELM tree. This method will be called for
     * every node in the tree that is a descendant of the Element type.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitElement(Element elm, C context) {
        if (elm instanceof AliasedQuerySource) return visitAliasedQuerySource((AliasedQuerySource)elm, context);
        else if (elm instanceof CaseItem) return visitCaseItem((CaseItem)elm, context);
        else if (elm instanceof Expression) return visitExpression((Expression)elm, context);
        else if (elm instanceof LetClause) return visitLetClause((LetClause)elm, context);
        else if (elm instanceof OperandDef) return visitOperandDef((OperandDef)elm, context);
        else if (elm instanceof ParameterDef) return visitParameterDef((ParameterDef)elm, context);
        else if (elm instanceof ReturnClause) return visitReturnClause((ReturnClause)elm, context);
        else if (elm instanceof SortByItem) return visitSortByItem((SortByItem)elm, context);
        else if (elm instanceof TupleElementDefinition) return visitTupleElementDefinition((TupleElementDefinition)elm, context);
        else if (elm instanceof TypeSpecifier) return visitTypeSpecifier((TypeSpecifier)elm, context);
        else return null;
    }

    /**
     * Visit a TypeSpecifier. This method will be called for every
     * node in the tree that is a descendant of the TypeSpecifier type.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitTypeSpecifier(TypeSpecifier elm, C context) {
        if (elm instanceof NamedTypeSpecifier) return visitNamedTypeSpecifier((NamedTypeSpecifier)elm, context);
        else if (elm instanceof IntervalTypeSpecifier) return visitIntervalTypeSpecifier((IntervalTypeSpecifier)elm, context);
        else if (elm instanceof ListTypeSpecifier) return visitListTypeSpecifier((ListTypeSpecifier)elm, context);
        else if (elm instanceof TupleTypeSpecifier) return visitTupleTypeSpecifier((TupleTypeSpecifier)elm, context);
        else return null;
    }

    /**
     * Visit a NamedTypeSpecifier. This method will be called for
     * every node in the tree that is a NamedTypeSpecifier.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitNamedTypeSpecifier(NamedTypeSpecifier elm, C context) {
        return null;
    }

    /**
     * Visit a IntervalTypeSpecifier. This method will be called for
     * every node in the tree that is a IntervalTypeSpecifier.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIntervalTypeSpecifier(IntervalTypeSpecifier elm, C context) {
        visitElement(elm.getPointType(), context);
        return null;
    }

    /**
     * Visit a ListTypeSpecifier. This method will be called for
     * every node in the tree that is a ListTypeSpecifier.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitListTypeSpecifier(ListTypeSpecifier elm, C context) {
        visitElement(elm.getElementType(), context);
        return null;
    }

    /**
     * Visit a TupleElementDefinition. This method will be called for
     * every node in the tree that is a TupleElementDefinition.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitTupleElementDefinition(TupleElementDefinition elm, C context) {
        visitElement(elm.getType(), context);
        return null;
    }

    /**
     * Visit a TupleTypeSpecifier. This method will be called for
     * every node in the tree that is a TupleTypeSpecifier.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitTupleTypeSpecifier(TupleTypeSpecifier elm, C context) {
        for (TupleElementDefinition element : elm.getElement()) {
            visitElement(element, context);
        }
        return null;
    }

    /**
     * Visit an Expression. This method will be called for
     * every node in the tree that is an Expression.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitExpression(Expression elm, C context) {
        if (elm instanceof AggregateExpression) return visitAggregateExpression((AggregateExpression)elm, context);
        else if (elm instanceof AliasRef) return visitAliasRef((AliasRef)elm, context);
        else if (elm instanceof BinaryExpression) return visitBinaryExpression((BinaryExpression)elm, context);
        else if (elm instanceof Case) return visitCase((Case)elm, context);
        else if (elm instanceof Combine) return visitCombine((Combine)elm, context);
        else if (elm instanceof Current) return visitCurrent((Current)elm, context);
        else if (elm instanceof DateTime) return visitDateTime((DateTime)elm, context);
        else if (elm instanceof ExpressionRef) return visitExpressionRef((ExpressionRef)elm, context);
        else if (elm instanceof Filter) return visitFilter((Filter)elm, context);
        else if (elm instanceof First) return visitFirst((First)elm, context);
        else if (elm instanceof ForEach) return visitForEach((ForEach)elm, context);
        else if (elm instanceof FunctionRef) return visitFunctionRef((FunctionRef)elm, context);
        else if (elm instanceof IdentifierRef) return visitIdentifierRef((IdentifierRef)elm, context);
        else if (elm instanceof If) return visitIf((If)elm, context);
        else if (elm instanceof IndexOf) return visitIndexOf((IndexOf)elm, context);
        else if (elm instanceof Instance) return visitInstance((Instance)elm, context);
        else if (elm instanceof Interval) return visitInterval((Interval)elm, context);
        else if (elm instanceof Last) return visitLast((Last)elm, context);
        else if (elm instanceof List) return visitList((List)elm, context);
        else if (elm instanceof Literal) return visitLiteral((Literal)elm, context);
        else if (elm instanceof MaxValue) return visitMaxValue((MaxValue)elm, context);
        else if (elm instanceof MinValue) return visitMinValue((MinValue)elm, context);
        else if (elm instanceof NaryExpression) return visitNaryExpression((NaryExpression)elm, context);
        else if (elm instanceof Now) return visitNow((Now)elm, context);
        else if (elm instanceof Null) return visitNull((Null)elm, context);
        else if (elm instanceof OperandRef) return visitOperandRef((OperandRef)elm, context);
        else if (elm instanceof ParameterRef) return visitParameterRef((ParameterRef)elm, context);
        else if (elm instanceof PositionOf) return visitPositionOf((PositionOf)elm, context);
        else if (elm instanceof Property) return visitProperty((Property)elm, context);
        else if (elm instanceof Query) return visitQuery((Query)elm, context);
        else if (elm instanceof QueryLetRef) return visitQueryLetRef((QueryLetRef)elm, context);
        else if (elm instanceof Round) return visitRound((Round)elm, context);
        else if (elm instanceof Sort) return visitSort((Sort)elm, context);
        else if (elm instanceof Split) return visitSplit((Split)elm, context);
        else if (elm instanceof Substring) return visitSubstring((Substring)elm, context);
        else if (elm instanceof TernaryExpression) return visitTernaryExpression((TernaryExpression)elm, context);
        else if (elm instanceof Time) return visitTime((Time)elm, context);
        else if (elm instanceof TimeOfDay) return visitTimeOfDay((TimeOfDay)elm, context);
        else if (elm instanceof Today) return visitToday((Today)elm, context);
        else if (elm instanceof Tuple) return visitTuple((Tuple)elm, context);
        else if (elm instanceof UnaryExpression) return visitUnaryExpression((UnaryExpression)elm, context);
        else return null;
    }

    /**
     * Visit a UnaryExpression. This method will be called for
     * every node in the tree that is a UnaryExpression.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitUnaryExpression(UnaryExpression elm, C context) {
        if (elm instanceof Abs) return visitAbs((Abs)elm, context);
        else if (elm instanceof As) return visitAs((As)elm, context);
        else if (elm instanceof Ceiling) return visitCeiling((Ceiling)elm, context);
        else if (elm instanceof Collapse) return visitCollapse((Collapse)elm, context);
        else if (elm instanceof Convert) return visitConvert((Convert)elm, context);
        else if (elm instanceof DateFrom) return visitDateFrom((DateFrom)elm, context);
        else if (elm instanceof DateTimeComponentFrom) return visitDateTimeComponentFrom((DateTimeComponentFrom)elm, context);
        else if (elm instanceof Distinct) return visitDistinct((Distinct)elm, context);
        else if (elm instanceof End) return visitEnd((End)elm, context);
        else if (elm instanceof Exists) return visitExists((Exists)elm, context);
        else if (elm instanceof Exp) return visitExp((Exp)elm, context);
        else if (elm instanceof Flatten) return visitFlatten((Flatten)elm, context);
        else if (elm instanceof Floor) return visitFloor((Floor)elm, context);
        else if (elm instanceof Is) return visitIs((Is)elm, context);
        else if (elm instanceof IsFalse) return visitIsFalse((IsFalse)elm, context);
        else if (elm instanceof IsNull) return visitIsNull((IsNull)elm, context);
        else if (elm instanceof IsTrue) return visitIsTrue((IsTrue)elm, context);
        else if (elm instanceof Length) return visitLength((Length)elm, context);
        else if (elm instanceof Ln) return visitLn((Ln)elm, context);
        else if (elm instanceof Lower) return visitLower((Lower)elm, context);
        else if (elm instanceof Negate) return visitNegate((Negate)elm, context);
        else if (elm instanceof Not) return visitNot((Not)elm, context);
        else if (elm instanceof Predecessor) return visitPredecessor((Predecessor)elm, context);
        else if (elm instanceof SingletonFrom) return visitSingletonFrom((SingletonFrom)elm, context);
        else if (elm instanceof Start) return visitStart((Start)elm, context);
        else if (elm instanceof Successor) return visitSuccessor((Successor)elm, context);
        else if (elm instanceof TimeFrom) return visitTimeFrom((TimeFrom)elm, context);
        else if (elm instanceof TimezoneFrom) return visitTimezoneFrom((TimezoneFrom)elm, context);
        else if (elm instanceof ToBoolean) return visitToBoolean((ToBoolean)elm, context);
        else if (elm instanceof ToConcept) return visitToConcept((ToConcept)elm, context);
        else if (elm instanceof ToDateTime) return visitToDateTime((ToDateTime)elm, context);
        else if (elm instanceof ToDecimal) return visitToDecimal((ToDecimal)elm, context);
        else if (elm instanceof ToInteger) return visitToInteger((ToInteger)elm, context);
        else if (elm instanceof ToQuantity) return visitToQuantity((ToQuantity)elm, context);
        else if (elm instanceof ToString) return visitToString((ToString)elm, context);
        else if (elm instanceof ToTime) return visitToTime((ToTime)elm, context);
        else if (elm instanceof Truncate) return visitTruncate((Truncate)elm, context);
        else if (elm instanceof Upper) return visitUpper((Upper)elm, context);
        else if (elm instanceof Width) return visitWidth((Width)elm, context);
        else return null;
    }

    /**
     * Visit a BinaryExpression. This method will be called for
     * every node in the tree that is a BinaryExpression.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitBinaryExpression(BinaryExpression elm, C context) {
        if (elm instanceof Add) return visitAdd((Add)elm, context);
        else if (elm instanceof After) return visitAfter((After)elm, context);
        else if (elm instanceof And) return visitAnd((And)elm, context);
        else if (elm instanceof Before) return visitBefore((Before)elm, context);
        else if (elm instanceof Contains) return visitContains((Contains)elm, context);
        else if (elm instanceof DifferenceBetween) return visitDifferenceBetween((DifferenceBetween)elm, context);
        else if (elm instanceof Divide) return visitDivide((Divide)elm, context);
        else if (elm instanceof DurationBetween) return visitDurationBetween((DurationBetween)elm, context);
        else if (elm instanceof Ends) return visitEnds((Ends)elm, context);
        else if (elm instanceof Equal) return visitEqual((Equal)elm, context);
        else if (elm instanceof Equivalent) return visitEquivalent((Equivalent)elm, context);
        else if (elm instanceof Except) return visitExcept((Except)elm, context);
        else if (elm instanceof Greater) return visitGreater((Greater)elm, context);
        else if (elm instanceof GreaterOrEqual) return visitGreaterOrEqual((GreaterOrEqual)elm, context);
        else if (elm instanceof In) return visitIn((In)elm, context);
        else if (elm instanceof IncludedIn) return visitIncludedIn((IncludedIn)elm, context);
        else if (elm instanceof Includes) return visitIncludes((Includes)elm, context);
        else if (elm instanceof Indexer) return visitIndexer((Indexer)elm, context);
        else if (elm instanceof Intersect) return visitIntersect((Intersect)elm, context);
        else if (elm instanceof Less) return visitLess((Less)elm, context);
        else if (elm instanceof LessOrEqual) return visitLessOrEqual((LessOrEqual)elm, context);
        else if (elm instanceof Log) return visitLog((Log)elm, context);
        else if (elm instanceof Meets) return visitMeets((Meets)elm, context);
        else if (elm instanceof MeetsAfter) return visitMeetsAfter((MeetsAfter)elm, context);
        else if (elm instanceof MeetsBefore) return visitMeetsBefore((MeetsBefore)elm, context);
        else if (elm instanceof Modulo) return visitModulo((Modulo)elm, context);
        else if (elm instanceof Multiply) return visitMultiply((Multiply)elm, context);
        else if (elm instanceof NotEqual) return visitNotEqual((NotEqual)elm, context);
        else if (elm instanceof Or) return visitOr((Or)elm, context);
        else if (elm instanceof Overlaps) return visitOverlaps((Overlaps)elm, context);
        else if (elm instanceof OverlapsAfter) return visitOverlapsAfter((OverlapsAfter)elm, context);
        else if (elm instanceof OverlapsBefore) return visitOverlapsBefore((OverlapsBefore)elm, context);
        else if (elm instanceof Power) return visitPower((Power)elm, context);
        else if (elm instanceof ProperContains) return visitProperContains((ProperContains)elm, context);
        else if (elm instanceof ProperIn) return visitProperIn((ProperIn)elm, context);
        else if (elm instanceof ProperIncludedIn) return visitProperIncludedIn((ProperIncludedIn)elm, context);
        else if (elm instanceof ProperIncludes) return visitProperIncludes((ProperIncludes)elm, context);
        else if (elm instanceof SameAs) return visitSameAs((SameAs)elm, context);
        else if (elm instanceof SameOrAfter) return visitSameOrAfter((SameOrAfter)elm, context);
        else if (elm instanceof SameOrBefore) return visitSameOrBefore((SameOrBefore)elm, context);
        else if (elm instanceof Starts) return visitStarts((Starts)elm, context);
        else if (elm instanceof Subtract) return visitSubtract((Subtract)elm, context);
        else if (elm instanceof Times) return visitTimes((Times)elm, context);
        else if (elm instanceof TruncatedDivide) return visitTruncatedDivide((TruncatedDivide)elm, context);
        else if (elm instanceof Union) return visitUnion((Union)elm, context);
        else if (elm instanceof Xor) return visitXor((Xor)elm, context);
        else return null;
    }

    /**
     * Visit a TernaryExpression. This method will be called for
     * every node in the tree that is a TernaryExpression.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitTernaryExpression(TernaryExpression elm, C context) {
        for (Expression element : elm.getOperand()) {
            visitElement(element, context);
        }
        return null;
    }

    /**
     * Visit a NaryExpression. This method will be called for
     * every node in the tree that is a NaryExpression.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitNaryExpression(NaryExpression elm, C context) {
        if (elm instanceof Coalesce) return visitCoalesce((Coalesce)elm, context);
        else if (elm instanceof Concatenate) return visitConcatenate((Concatenate)elm, context);
        else return null;
    }

    /**
     * Visit a ExpressionDef. This method will be called for
     * every node in the tree that is a ExpressionDef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitExpressionDef(ExpressionDef elm, C context) {
        visitElement(elm.getExpression(), context);
        return null;
    }

    /**
     * Visit a FunctionDef. This method will be called for
     * every node in the tree that is a FunctionDef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitFunctionDef(FunctionDef elm, C context) {
        for (OperandDef element : elm.getOperand()) {
            visitElement(element, context);
        }
        visitElement(elm.getExpression(), context);
        return null;
    }

    /**
     * Visit a ExpressionRef. This method will be called for
     * every node in the tree that is a ExpressionRef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitExpressionRef(ExpressionRef elm, C context) {
        return null;
    }

    /**
     * Visit a FunctionRef. This method will be called for
     * every node in the tree that is a FunctionRef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitFunctionRef(FunctionRef elm, C context) {
        for (Expression element : elm.getOperand()) {
            visitElement(element, context);
        }
        return null;
    }

    /**
     * Visit a ParameterDef. This method will be called for
     * every node in the tree that is a ParameterDef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitParameterDef(ParameterDef elm, C context) {
        if (elm.getParameterTypeSpecifier() != null) {
            visitElement(elm.getParameterTypeSpecifier(), context);
        }

        if (elm.getDefault() != null) {
            visitElement(elm.getDefault(), context);
        }

        return null;
    }

    /**
     * Visit a ParameterRef. This method will be called for
     * every node in the tree that is a ParameterRef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitParameterRef(ParameterRef elm, C context) {
        return null;
    }

    /**
     * Visit a OperandDef. This method will be called for
     * every node in the tree that is a OperandDef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitOperandDef(OperandDef elm, C context) {
        if (elm.getOperandTypeSpecifier() != null) {
            visitElement(elm.getOperandTypeSpecifier(), context);
        }
        return null;
    }

    /**
     * Visit a OperandRef. This method will be called for
     * every node in the tree that is a OperandRef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitOperandRef(OperandRef elm, C context) {
        return null;
    }

    /**
     * Visit a IdentifierRef. This method will be called for
     * every node in the tree that is a IdentifierRef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIdentifierRef(IdentifierRef elm, C context) {
        return null;
    }

    /**
     * Visit a Literal. This method will be called for
     * every node in the tree that is a Literal.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitLiteral(Literal elm, C context) {
        return null;
    }

    /**
     * Visit a TupleElement. This method will be called for
     * every node in the tree that is a TupleElement.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitTupleElement(TupleElement elm, C context) {
        visitElement(elm.getValue(), context);
        return null;
    }

    /**
     * Visit a Tuple. This method will be called for
     * every node in the tree that is a Tuple.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitTuple(Tuple elm, C context) {
        for (TupleElement element : elm.getElement()) {
            visitTupleElement(element, context);
        }
        return null;
    }

    /**
     * Visit a InstanceElement. This method will be called for
     * every node in the tree that is a InstanceElement.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitInstanceElement(InstanceElement elm, C context) {
        visitElement(elm.getValue(), context);
        return null;
    }

    /**
     * Visit a Instance. This method will be called for
     * every node in the tree that is a Instance.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitInstance(Instance elm, C context) {
        for (InstanceElement element : elm.getElement()) {
            visitInstanceElement(element, context);
        }
        return null;
    }

    /**
     * Visit a Interval. This method will be called for
     * every node in the tree that is a Interval.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitInterval(Interval elm, C context) {
        if (elm.getLow() != null) {
            visitElement(elm.getLow(), context);
        }
        if (elm.getLowClosedExpression() != null) {
            visitElement(elm.getLowClosedExpression(), context);
        }
        if (elm.getHigh() != null) {
            visitElement(elm.getHigh(), context);
        }
        if (elm.getHighClosedExpression() != null) {
            visitElement(elm.getHighClosedExpression(), context);
        }
        return null;
    }

    /**
     * Visit a List. This method will be called for
     * every node in the tree that is a List.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitList(List elm, C context) {
        if (elm.getTypeSpecifier() != null) {
            visitElement(elm.getTypeSpecifier(), context);
        }
        for (Expression element : elm.getElement()) {
            visitElement(element, context);
        }
        return null;
    }

    /**
     * Visit a And. This method will be called for
     * every node in the tree that is a And.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitAnd(And elm, C context) {
        return null;
    }

    /**
     * Visit a Or. This method will be called for
     * every node in the tree that is a Or.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitOr(Or elm, C context) {
        return null;
    }

    /**
     * Visit a Xor. This method will be called for
     * every node in the tree that is a Xor.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitXor(Xor elm, C context) {
        return null;
    }

    /**
     * Visit a Not. This method will be called for
     * every node in the tree that is a Not.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitNot(Not elm, C context) {
        return null;
    }

    /**
     * Visit a If. This method will be called for
     * every node in the tree that is a If.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIf(If elm, C context) {
        return null;
    }

    /**
     * Visit a CaseItem. This method will be called for
     * every node in the tree that is a CaseItem.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCaseItem(CaseItem elm, C context) {
        return null;
    }

    /**
     * Visit a Case. This method will be called for
     * every node in the tree that is a Case.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCase(Case elm, C context) {
        return null;
    }

    /**
     * Visit a Null. This method will be called for
     * every node in the tree that is a Null.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitNull(Null elm, C context) {
        return null;
    }

    /**
     * Visit a IsNull. This method will be called for
     * every node in the tree that is a IsNull.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIsNull(IsNull elm, C context) {
        return null;
    }

    /**
     * Visit a IsTrue. This method will be called for
     * every node in the tree that is a IsTrue.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIsTrue(IsTrue elm, C context) {
        return null;
    }

    /**
     * Visit a IsFalse. This method will be called for
     * every node in the tree that is a IsFalse.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIsFalse(IsFalse elm, C context) {
        return null;
    }

    /**
     * Visit a Coalesce. This method will be called for
     * every node in the tree that is a Coalesce.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCoalesce(Coalesce elm, C context) {
        return null;
    }

    /**
     * Visit a Is. This method will be called for
     * every node in the tree that is a Is.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIs(Is elm, C context) {
        return null;
    }

    /**
     * Visit a As. This method will be called for
     * every node in the tree that is a As.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitAs(As elm, C context) {
        return null;
    }

    /**
     * Visit a Convert. This method will be called for
     * every node in the tree that is a Convert.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitConvert(Convert elm, C context) {
        return null;
    }

    /**
     * Visit a ToBoolean. This method will be called for
     * every node in the tree that is a ToBoolean.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitToBoolean(ToBoolean elm, C context) {
        return null;
    }

    /**
     * Visit a ToConcept. This method will be called for
     * every node in the tree that is a ToConcept.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitToConcept(ToConcept elm, C context) {
        return null;
    }

    /**
     * Visit a ToDateTime. This method will be called for
     * every node in the tree that is a ToDateTime.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitToDateTime(ToDateTime elm, C context) {
        return null;
    }

    /**
     * Visit a ToDecimal. This method will be called for
     * every node in the tree that is a ToDecimal.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitToDecimal(ToDecimal elm, C context) {
        return null;
    }

    /**
     * Visit a ToInteger. This method will be called for
     * every node in the tree that is a ToInteger.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitToInteger(ToInteger elm, C context) {
        return null;
    }

    /**
     * Visit a ToQuantity. This method will be called for
     * every node in the tree that is a ToQuantity.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitToQuantity(ToQuantity elm, C context) {
        return null;
    }

    /**
     * Visit a ToString. This method will be called for
     * every node in the tree that is a ToString.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitToString(ToString elm, C context) {
        return null;
    }

    /**
     * Visit a ToTime. This method will be called for
     * every node in the tree that is a ToTime.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitToTime(ToTime elm, C context) {
        return null;
    }

    /**
     * Visit a Equal. This method will be called for
     * every node in the tree that is a Equal.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitEqual(Equal elm, C context) {
        return null;
    }

    /**
     * Visit a Equivalent. This method will be called for
     * every node in the tree that is a Equivalent.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitEquivalent(Equivalent elm, C context) {
        return null;
    }

    /**
     * Visit a NotEqual. This method will be called for
     * every node in the tree that is a NotEqual.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitNotEqual(NotEqual elm, C context) {
        return null;
    }

    /**
     * Visit a Less. This method will be called for
     * every node in the tree that is a Less.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitLess(Less elm, C context) {
        return null;
    }

    /**
     * Visit a Greater. This method will be called for
     * every node in the tree that is a Greater.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitGreater(Greater elm, C context) {
        return null;
    }

    /**
     * Visit a LessOrEqual. This method will be called for
     * every node in the tree that is a LessOrEqual.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitLessOrEqual(LessOrEqual elm, C context) {
        return null;
    }

    /**
     * Visit a GreaterOrEqual. This method will be called for
     * every node in the tree that is a GreaterOrEqual.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitGreaterOrEqual(GreaterOrEqual elm, C context) {
        return null;
    }

    /**
     * Visit a Add. This method will be called for
     * every node in the tree that is a Add.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitAdd(Add elm, C context) {
        return null;
    }

    /**
     * Visit a Subtract. This method will be called for
     * every node in the tree that is a Subtract.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSubtract(Subtract elm, C context) {
        return null;
    }

    /**
     * Visit a Multiply. This method will be called for
     * every node in the tree that is a Multiply.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitMultiply(Multiply elm, C context) {
        return null;
    }

    /**
     * Visit a Divide. This method will be called for
     * every node in the tree that is a Divide.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitDivide(Divide elm, C context) {
        return null;
    }

    /**
     * Visit a TruncatedDivide. This method will be called for
     * every node in the tree that is a TruncatedDivide.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitTruncatedDivide(TruncatedDivide elm, C context) {
        return null;
    }

    /**
     * Visit a Modulo. This method will be called for
     * every node in the tree that is a Modulo.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitModulo(Modulo elm, C context) {
        return null;
    }

    /**
     * Visit a Ceiling. This method will be called for
     * every node in the tree that is a Ceiling.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCeiling(Ceiling elm, C context) {
        return null;
    }

    /**
     * Visit a Floor. This method will be called for
     * every node in the tree that is a Floor.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitFloor(Floor elm, C context) {
        return null;
    }

    /**
     * Visit a Truncate. This method will be called for
     * every node in the tree that is a Truncate.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitTruncate(Truncate elm, C context) {
        return null;
    }

    /**
     * Visit a Abs. This method will be called for
     * every node in the tree that is a Abs.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitAbs(Abs elm, C context) {
        return null;
    }

    /**
     * Visit a Negate. This method will be called for
     * every node in the tree that is a Negate.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitNegate(Negate elm, C context) {
        return null;
    }

    /**
     * Visit a Round. This method will be called for
     * every node in the tree that is a Round.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitRound(Round elm, C context) {
        return null;
    }

    /**
     * Visit a Ln. This method will be called for
     * every node in the tree that is a Ln.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitLn(Ln elm, C context) {
        return null;
    }

    /**
     * Visit a Exp. This method will be called for
     * every node in the tree that is a Exp.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitExp(Exp elm, C context) {
        return null;
    }

    /**
     * Visit a Log. This method will be called for
     * every node in the tree that is a Log.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitLog(Log elm, C context) {
        return null;
    }

    /**
     * Visit a Power. This method will be called for
     * every node in the tree that is a Power.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitPower(Power elm, C context) {
        return null;
    }

    /**
     * Visit a Successor. This method will be called for
     * every node in the tree that is a Successor.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSuccessor(Successor elm, C context) {
        return null;
    }

    /**
     * Visit a Predecessor. This method will be called for
     * every node in the tree that is a Predecessor.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitPredecessor(Predecessor elm, C context) {
        return null;
    }

    /**
     * Visit a MinValue. This method will be called for
     * every node in the tree that is a MinValue.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitMinValue(MinValue elm, C context) {
        return null;
    }

    /**
     * Visit a MaxValue. This method will be called for
     * every node in the tree that is a MaxValue.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitMaxValue(MaxValue elm, C context) {
        return null;
    }

    /**
     * Visit a Concatenate. This method will be called for
     * every node in the tree that is a Concatenate.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitConcatenate(Concatenate elm, C context) {
        return null;
    }

    /**
     * Visit a Combine. This method will be called for
     * every node in the tree that is a Combine.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCombine(Combine elm, C context) {
        return null;
    }

    /**
     * Visit a Split. This method will be called for
     * every node in the tree that is a Split.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSplit(Split elm, C context) {
        return null;
    }

    /**
     * Visit a Length. This method will be called for
     * every node in the tree that is a Length.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitLength(Length elm, C context) {
        return null;
    }

    /**
     * Visit a Upper. This method will be called for
     * every node in the tree that is a Upper.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitUpper(Upper elm, C context) {
        return null;
    }

    /**
     * Visit a Lower. This method will be called for
     * every node in the tree that is a Lower.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitLower(Lower elm, C context) {
        return null;
    }

    /**
     * Visit a Indexer. This method will be called for
     * every node in the tree that is a Indexer.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIndexer(Indexer elm, C context) {
        return null;
    }

    /**
     * Visit a PositionOf. This method will be called for
     * every node in the tree that is a PositionOf.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitPositionOf(PositionOf elm, C context) {
        return null;
    }

    /**
     * Visit a Substring. This method will be called for
     * every node in the tree that is a Substring.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSubstring(Substring elm, C context) {
        return null;
    }

    /**
     * Visit a DurationBetween. This method will be called for
     * every node in the tree that is a DurationBetween.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitDurationBetween(DurationBetween elm, C context) {
        return null;
    }

    /**
     * Visit a DifferenceBetween. This method will be called for
     * every node in the tree that is a DifferenceBetween.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitDifferenceBetween(DifferenceBetween elm, C context) {
        return null;
    }

    /**
     * Visit a DateFrom. This method will be called for
     * every node in the tree that is a DateFrom.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitDateFrom(DateFrom elm, C context) {
        return null;
    }

    /**
     * Visit a TimeFrom. This method will be called for
     * every node in the tree that is a TimeFrom.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitTimeFrom(TimeFrom elm, C context) {
        return null;
    }

    /**
     * Visit a TimezoneFrom. This method will be called for
     * every node in the tree that is a TimezoneFrom.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitTimezoneFrom(TimezoneFrom elm, C context) {
        return null;
    }

    /**
     * Visit a DateTimeComponentFrom. This method will be called for
     * every node in the tree that is a DateTimeComponentFrom.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitDateTimeComponentFrom(DateTimeComponentFrom elm, C context) {
        return null;
    }

    /**
     * Visit a TimeOfDay. This method will be called for
     * every node in the tree that is a TimeOfDay.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitTimeOfDay(TimeOfDay elm, C context) {
        return null;
    }

    /**
     * Visit a Today. This method will be called for
     * every node in the tree that is a Today.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitToday(Today elm, C context) {
        return null;
    }

    /**
     * Visit a Now. This method will be called for
     * every node in the tree that is a Now.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitNow(Now elm, C context) {
        return null;
    }

    /**
     * Visit a DateTime. This method will be called for
     * every node in the tree that is a DateTime.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitDateTime(DateTime elm, C context) {
        return null;
    }

    /**
     * Visit a Time. This method will be called for
     * every node in the tree that is a Time.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitTime(Time elm, C context) {
        return null;
    }

    /**
     * Visit a SameAs. This method will be called for
     * every node in the tree that is a SameAs.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSameAs(SameAs elm, C context) {
        return null;
    }

    /**
     * Visit a SameOrBefore. This method will be called for
     * every node in the tree that is a SameOrBefore.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSameOrBefore(SameOrBefore elm, C context) {
        return null;
    }

    /**
     * Visit a SameOrAfter. This method will be called for
     * every node in the tree that is a SameOrAfter.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSameOrAfter(SameOrAfter elm, C context) {
        return null;
    }

    /**
     * Visit a Width. This method will be called for
     * every node in the tree that is a Width.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitWidth(Width elm, C context) {
        return null;
    }

    /**
     * Visit a Start. This method will be called for
     * every node in the tree that is a Start.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitStart(Start elm, C context) {
        return null;
    }

    /**
     * Visit a End. This method will be called for
     * every node in the tree that is a End.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitEnd(End elm, C context) {
        return null;
    }

    /**
     * Visit a Contains. This method will be called for
     * every node in the tree that is a Contains.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitContains(Contains elm, C context) {
        return null;
    }

    /**
     * Visit a ProperContains. This method will be called for
     * every node in the tree that is a ProperContains.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitProperContains(ProperContains elm, C context) {
        return null;
    }

    /**
     * Visit a In. This method will be called for
     * every node in the tree that is a In.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIn(In elm, C context) {
        return null;
    }

    /**
     * Visit a ProperIn. This method will be called for
     * every node in the tree that is a ProperIn.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitProperIn(ProperIn elm, C context) {
        return null;
    }

    /**
     * Visit a Includes. This method will be called for
     * every node in the tree that is a Includes.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIncludes(Includes elm, C context) {
        return null;
    }

    /**
     * Visit a IncludedIn. This method will be called for
     * every node in the tree that is a IncludedIn.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIncludedIn(IncludedIn elm, C context) {
        return null;
    }

    /**
     * Visit a ProperIncludes. This method will be called for
     * every node in the tree that is a ProperIncludes.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitProperIncludes(ProperIncludes elm, C context) {
        return null;
    }

    /**
     * Visit a ProperIncludedIn. This method will be called for
     * every node in the tree that is a ProperIncludedIn.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitProperIncludedIn(ProperIncludedIn elm, C context) {
        return null;
    }

    /**
     * Visit a Before. This method will be called for
     * every node in the tree that is a Before.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitBefore(Before elm, C context) {
        return null;
    }

    /**
     * Visit a After. This method will be called for
     * every node in the tree that is a After.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitAfter(After elm, C context) {
        return null;
    }

    /**
     * Visit a Meets. This method will be called for
     * every node in the tree that is a Meets.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitMeets(Meets elm, C context) {
        return null;
    }

    /**
     * Visit a MeetsBefore. This method will be called for
     * every node in the tree that is a MeetsBefore.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitMeetsBefore(MeetsBefore elm, C context) {
        return null;
    }

    /**
     * Visit a MeetsAfter. This method will be called for
     * every node in the tree that is a MeetsAfter.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitMeetsAfter(MeetsAfter elm, C context) {
        return null;
    }

    /**
     * Visit a Overlaps. This method will be called for
     * every node in the tree that is a Overlaps.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitOverlaps(Overlaps elm, C context) {
        return null;
    }

    /**
     * Visit a OverlapsBefore. This method will be called for
     * every node in the tree that is a OverlapsBefore.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitOverlapsBefore(OverlapsBefore elm, C context) {
        return null;
    }

    /**
     * Visit a OverlapsAfter. This method will be called for
     * every node in the tree that is a OverlapsAfter.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitOverlapsAfter(OverlapsAfter elm, C context) {
        return null;
    }

    /**
     * Visit a Starts. This method will be called for
     * every node in the tree that is a Starts.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitStarts(Starts elm, C context) {
        return null;
    }

    /**
     * Visit a Ends. This method will be called for
     * every node in the tree that is a Ends.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitEnds(Ends elm, C context) {
        return null;
    }

    /**
     * Visit a Collapse. This method will be called for
     * every node in the tree that is a Collapse.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCollapse(Collapse elm, C context) {
        return null;
    }

    /**
     * Visit a Union. This method will be called for
     * every node in the tree that is a Union.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitUnion(Union elm, C context) {
        return null;
    }

    /**
     * Visit a Intersect. This method will be called for
     * every node in the tree that is a Intersect.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIntersect(Intersect elm, C context) {
        return null;
    }

    /**
     * Visit a Except. This method will be called for
     * every node in the tree that is a Except.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitExcept(Except elm, C context) {
        return null;
    }

    /**
     * Visit a Literal. This method will be called for
     * every node in the tree that is a Literal.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitExists(Exists elm, C context) {
        return null;
    }

    /**
     * Visit a Times. This method will be called for
     * every node in the tree that is a Times.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitTimes(Times elm, C context) {
        return null;
    }

    /**
     * Visit a Filter. This method will be called for
     * every node in the tree that is a Filter.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitFilter(Filter elm, C context) {
        return null;
    }

    /**
     * Visit a First. This method will be called for
     * every node in the tree that is a First.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitFirst(First elm, C context) {
        return null;
    }

    /**
     * Visit a Last. This method will be called for
     * every node in the tree that is a Last.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitLast(Last elm, C context) {
        return null;
    }

    /**
     * Visit a IndexOf. This method will be called for
     * every node in the tree that is a IndexOf.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIndexOf(IndexOf elm, C context) {
        return null;
    }

    /**
     * Visit a Flatten. This method will be called for
     * every node in the tree that is a Flatten.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitFlatten(Flatten elm, C context) {
        return null;
    }

    /**
     * Visit a Sort. This method will be called for
     * every node in the tree that is a Sort.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSort(Sort elm, C context) {
        return null;
    }

    /**
     * Visit a ForEach. This method will be called for
     * every node in the tree that is a ForEach.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitForEach(ForEach elm, C context) {
        return null;
    }

    /**
     * Visit a Distinct. This method will be called for
     * every node in the tree that is a Distinct.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitDistinct(Distinct elm, C context) {
        return null;
    }

    /**
     * Visit a Current. This method will be called for
     * every node in the tree that is a Current.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCurrent(Current elm, C context) {
        return null;
    }

    /**
     * Visit a SingletonFrom. This method will be called for
     * every node in the tree that is a SingletonFrom.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSingletonFrom(SingletonFrom elm, C context) {
        return null;
    }

    /**
     * Visit a AggregateExpression. This method will be called for
     * every node in the tree that is a AggregateExpression.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitAggregateExpression(AggregateExpression elm, C context) {
        return null;
    }

    /**
     * Visit a Count. This method will be called for
     * every node in the tree that is a Count.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCount(Count elm, C context) {
        return null;
    }

    /**
     * Visit a Sum. This method will be called for
     * every node in the tree that is a Sum.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSum(Sum elm, C context) {
        return null;
    }

    /**
     * Visit a Min. This method will be called for
     * every node in the tree that is a Min.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitMin(Min elm, C context) {
        return null;
    }

    /**
     * Visit a Max. This method will be called for
     * every node in the tree that is a Max.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitMax(Max elm, C context) {
        return null;
    }

    /**
     * Visit a Avg. This method will be called for
     * every node in the tree that is a Avg.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitAvg(Avg elm, C context) {
        return null;
    }

    /**
     * Visit a Median. This method will be called for
     * every node in the tree that is a Median.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitMedian(Median elm, C context) {
        return null;
    }

    /**
     * Visit a Mode. This method will be called for
     * every node in the tree that is a Mode.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitMode(Mode elm, C context) {
        return null;
    }

    /**
     * Visit a Variance. This method will be called for
     * every node in the tree that is a Variance.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitVariance(Variance elm, C context) {
        return null;
    }

    /**
     * Visit a PopulationVariance. This method will be called for
     * every node in the tree that is a PopulationVariance.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitPopulationVariance(PopulationVariance elm, C context) {
        return null;
    }

    /**
     * Visit a StdDev. This method will be called for
     * every node in the tree that is a StdDev.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitStdDev(StdDev elm, C context) {
        return null;
    }

    /**
     * Visit a PopulationStdDev. This method will be called for
     * every node in the tree that is a PopulationStdDev.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitPopulationStdDev(PopulationStdDev elm, C context) {
        return null;
    }

    /**
     * Visit a AllTrue. This method will be called for
     * every node in the tree that is a AllTrue.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitAllTrue(AllTrue elm, C context) {
        return null;
    }

    /**
     * Visit a AnyTrue. This method will be called for
     * every node in the tree that is a AnyTrue.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitAnyTrue(AnyTrue elm, C context) {
        return null;
    }

    /**
     * Visit a Property. This method will be called for
     * every node in the tree that is a Property.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitProperty(Property elm, C context) {
        return null;
    }

    /**
     * Visit a AliasedQuerySource. This method will be called for
     * every node in the tree that is a AliasedQuerySource.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitAliasedQuerySource(AliasedQuerySource elm, C context) {
        return null;
    }

    /**
     * Visit a LetClause. This method will be called for
     * every node in the tree that is a LetClause.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitLetClause(LetClause elm, C context) {
        return null;
    }

    /**
     * Visit a RelationshipClause. This method will be called for
     * every node in the tree that is a RelationshipClause.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitRelationshipClause(RelationshipClause elm, C context) {
        return null;
    }

    /**
     * Visit a With. This method will be called for
     * every node in the tree that is a With.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitWith(With elm, C context) {
        return null;
    }

    /**
     * Visit a Without. This method will be called for
     * every node in the tree that is a Without.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitWithout(Without elm, C context) {
        return null;
    }

    /**
     * Visit a SortByItem. This method will be called for
     * every node in the tree that is a SortByItem.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSortByItem(SortByItem elm, C context) {
        return null;
    }

    /**
     * Visit a ByDirection. This method will be called for
     * every node in the tree that is a ByDirection.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitByDirection(ByDirection elm, C context) {
        return null;
    }

    /**
     * Visit a ByColumn. This method will be called for
     * every node in the tree that is a ByColumn.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitByColumn(ByColumn elm, C context) {
        return null;
    }

    /**
     * Visit a ByExpression. This method will be called for
     * every node in the tree that is a ByExpression.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitByExpression(ByExpression elm, C context) {
        return null;
    }

    /**
     * Visit a SortClause. This method will be called for
     * every node in the tree that is a SortClause.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSortClause(SortClause elm, C context) {
        return null;
    }

    /**
     * Visit a ReturnClause. This method will be called for
     * every node in the tree that is a ReturnClause.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitReturnClause(ReturnClause elm, C context) {
        return null;
    }

    /**
     * Visit a Query. This method will be called for
     * every node in the tree that is a Query.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitQuery(Query elm, C context) {
        return null;
    }

    /**
     * Visit a AliasRef. This method will be called for
     * every node in the tree that is a AliasRef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitAliasRef(AliasRef elm, C context) {
        return null;
    }

    /**
     * Visit a QueryLetRef. This method will be called for
     * every node in the tree that is a QueryLetRef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitQueryLetRef(QueryLetRef elm, C context) {
        return null;
    }
}
