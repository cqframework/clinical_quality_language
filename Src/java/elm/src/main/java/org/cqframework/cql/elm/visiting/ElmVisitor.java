package org.cqframework.cql.elm.visiting;

import org.hl7.elm.r1.*;

/**
 * This interface defines a complete generic visitor for an Elm tree
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * @param <C> The type of context passed to each visit method
 * operations with no return type.
 */
public interface ElmVisitor<T, C> {
    /**
     * Visit an Element in an ELM tree. This method will be called for
     * every node in the tree that is a descendant of the Element type.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitElement(Element elm, C context);

    /**
     * Visit a TypeSpecifier. This method will be called for every
     * node in the tree that is a descendant of the TypeSpecifier type.
     * 
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitTypeSpecifier(TypeSpecifier elm, C context);

    /**
     * Visit a NamedTypeSpecifier. This method will be called for
     * every node in the tree that is a NamedTypeSpecifier.
     * 
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitNamedTypeSpecifier(NamedTypeSpecifier elm, C context);

    /**
     * Visit a IntervalTypeSpecifier. This method will be called for
     * every node in the tree that is a IntervalTypeSpecifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitIntervalTypeSpecifier(IntervalTypeSpecifier elm, C context);

    /**
     * Visit a ListTypeSpecifier. This method will be called for
     * every node in the tree that is a ListTypeSpecifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitListTypeSpecifier(ListTypeSpecifier elm, C context);

    /**
     * Visit a TupleElementDefinition. This method will be called for
     * every node in the tree that is a TupleElementDefinition.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitTupleElementDefinition(TupleElementDefinition elm, C context);

    /**
     * Visit a TupleTypeSpecifier. This method will be called for
     * every node in the tree that is a TupleTypeSpecifier.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitTupleTypeSpecifier(TupleTypeSpecifier elm, C context);

    /**
     * Visit an Expression. This method will be called for
     * every node in the tree that is an Expression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitExpression(Expression elm, C context);

    /**
     * Visit a UnaryExpression. This method will be called for
     * every node in the tree that is a UnaryExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitUnaryExpression(UnaryExpression elm, C context);

    /**
     * Visit a BinaryExpression. This method will be called for
     * every node in the tree that is a BinaryExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitBinaryExpression(BinaryExpression elm, C context);

    /**
     * Visit a TernaryExpression. This method will be called for
     * every node in the tree that is a TernaryExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitTernaryExpression(TernaryExpression elm, C context);

    /**
     * Visit a NaryExpression. This method will be called for
     * every node in the tree that is a NaryExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitNaryExpression(NaryExpression elm, C context);

    /**
     * Visit a ExpressionDef. This method will be called for
     * every node in the tree that is a ExpressionDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitExpressionDef(ExpressionDef elm, C context);

    /**
     * Visit a FunctionDef. This method will be called for
     * every node in the tree that is a FunctionDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitFunctionDef(FunctionDef elm, C context);

    /**
     * Visit a ExpressionRef. This method will be called for
     * every node in the tree that is a ExpressionRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitExpressionRef(ExpressionRef elm, C context);

    /**
     * Visit a FunctionRef. This method will be called for
     * every node in the tree that is a FunctionRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitFunctionRef(FunctionRef elm, C context);

    /**
     * Visit a ParameterDef. This method will be called for
     * every node in the tree that is a ParameterDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitParameterDef(ParameterDef elm, C context);

    /**
     * Visit a ParameterRef. This method will be called for
     * every node in the tree that is a ParameterRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitParameterRef(ParameterRef elm, C context);

    /**
     * Visit a OperandDef. This method will be called for
     * every node in the tree that is a OperandDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitOperandDef(OperandDef elm, C context);

    /**
     * Visit a OperandRef. This method will be called for
     * every node in the tree that is a OperandRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitOperandRef(OperandRef elm, C context);

    /**
     * Visit a IdentifierRef. This method will be called for
     * every node in the tree that is a IdentifierRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitIdentifierRef(IdentifierRef elm, C context);

    /**
     * Visit a Literal. This method will be called for
     * every node in the tree that is a Literal.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitLiteral(Literal elm, C context);

    /**
     * Visit a TupleElement. This method will be called for
     * every node in the tree that is a TupleElement.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitTupleElement(TupleElement elm, C context);

    /**
     * Visit a Tuple. This method will be called for
     * every node in the tree that is a Tuple.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitTuple(Tuple elm, C context);

    /**
     * Visit a InstanceElement. This method will be called for
     * every node in the tree that is a InstanceElement.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitInstanceElement(InstanceElement elm, C context);

    /**
     * Visit a Instance. This method will be called for
     * every node in the tree that is a Instance.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitInstance(Instance elm, C context);

    /**
     * Visit a Interval. This method will be called for
     * every node in the tree that is a Interval.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitInterval(Interval elm, C context);

    /**
     * Visit a List. This method will be called for
     * every node in the tree that is a List.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitList(List elm, C context);

    /**
     * Visit a And. This method will be called for
     * every node in the tree that is a And.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitAnd(And elm, C context);

    /**
     * Visit a Or. This method will be called for
     * every node in the tree that is a Or.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitOr(Or elm, C context);

    /**
     * Visit a Xor. This method will be called for
     * every node in the tree that is a Xor.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitXor(Xor elm, C context);

    /**
     * Visit a Implies. This method will be called for
     * every node in the tree that is a Implies.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitImplies(Implies elm, C context);

    /**
     * Visit a Not. This method will be called for
     * every node in the tree that is a Not.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitNot(Not elm, C context);

    /**
     * Visit a If. This method will be called for
     * every node in the tree that is a If.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitIf(If elm, C context);

    /**
     * Visit a CaseItem. This method will be called for
     * every node in the tree that is a CaseItem.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitCaseItem(CaseItem elm, C context);

    /**
     * Visit a Case. This method will be called for
     * every node in the tree that is a Case.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitCase(Case elm, C context);

    /**
     * Visit a Null. This method will be called for
     * every node in the tree that is a Null.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitNull(Null elm, C context);

    /**
     * Visit a IsNull. This method will be called for
     * every node in the tree that is a IsNull.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitIsNull(IsNull elm, C context);

    /**
     * Visit a IsTrue. This method will be called for
     * every node in the tree that is a IsTrue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitIsTrue(IsTrue elm, C context);

    /**
     * Visit a IsFalse. This method will be called for
     * every node in the tree that is a IsFalse.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitIsFalse(IsFalse elm, C context);

    /**
     * Visit a Coalesce. This method will be called for
     * every node in the tree that is a Coalesce.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitCoalesce(Coalesce elm, C context);

    /**
     * Visit a Is. This method will be called for
     * every node in the tree that is a Is.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitIs(Is elm, C context);

    /**
     * Visit a As. This method will be called for
     * every node in the tree that is a As.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitAs(As elm, C context);

    /**
     * Visit a Convert. This method will be called for
     * every node in the tree that is a Convert.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitConvert(Convert elm, C context);

    /**
     * Visit a ToBoolean. This method will be called for
     * every node in the tree that is a ToBoolean.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitToBoolean(ToBoolean elm, C context);

    /**
     * Visit a ToConcept. This method will be called for
     * every node in the tree that is a ToConcept.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitToConcept(ToConcept elm, C context);

    /**
     * Visit a ToDateTime. This method will be called for
     * every node in the tree that is a ToDateTime.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitToDateTime(ToDateTime elm, C context);

    /**
     * Visit a ToDecimal. This method will be called for
     * every node in the tree that is a ToDecimal.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitToDecimal(ToDecimal elm, C context);

    /**
     * Visit a ToInteger. This method will be called for
     * every node in the tree that is a ToInteger.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitToInteger(ToInteger elm, C context);

    /**
     * Visit a ToQuantity. This method will be called for
     * every node in the tree that is a ToQuantity.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitToQuantity(ToQuantity elm, C context);

    /**
     * Visit a ToString. This method will be called for
     * every node in the tree that is a ToString.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitToString(ToString elm, C context);

    /**
     * Visit a ToTime. This method will be called for
     * every node in the tree that is a ToTime.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitToTime(ToTime elm, C context);

    /**
     * Visit a Equal. This method will be called for
     * every node in the tree that is a Equal.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitEqual(Equal elm, C context);

    /**
     * Visit a Equivalent. This method will be called for
     * every node in the tree that is a Equivalent.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitEquivalent(Equivalent elm, C context);

    /**
     * Visit a NotEqual. This method will be called for
     * every node in the tree that is a NotEqual.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitNotEqual(NotEqual elm, C context);

    /**
     * Visit a Less. This method will be called for
     * every node in the tree that is a Less.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitLess(Less elm, C context);

    /**
     * Visit a Greater. This method will be called for
     * every node in the tree that is a Greater.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitGreater(Greater elm, C context);

    /**
     * Visit a LessOrEqual. This method will be called for
     * every node in the tree that is a LessOrEqual.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitLessOrEqual(LessOrEqual elm, C context);

    /**
     * Visit a GreaterOrEqual. This method will be called for
     * every node in the tree that is a GreaterOrEqual.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitGreaterOrEqual(GreaterOrEqual elm, C context);

    /**
     * Visit a Add. This method will be called for
     * every node in the tree that is a Add.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitAdd(Add elm, C context);

    /**
     * Visit a Subtract. This method will be called for
     * every node in the tree that is a Subtract.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitSubtract(Subtract elm, C context);

    /**
     * Visit a Multiply. This method will be called for
     * every node in the tree that is a Multiply.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitMultiply(Multiply elm, C context);

    /**
     * Visit a Divide. This method will be called for
     * every node in the tree that is a Divide.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitDivide(Divide elm, C context);

    /**
     * Visit a TruncatedDivide. This method will be called for
     * every node in the tree that is a TruncatedDivide.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitTruncatedDivide(TruncatedDivide elm, C context);

    /**
     * Visit a Modulo. This method will be called for
     * every node in the tree that is a Modulo.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitModulo(Modulo elm, C context);

    /**
     * Visit a Ceiling. This method will be called for
     * every node in the tree that is a Ceiling.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitCeiling(Ceiling elm, C context);

    /**
     * Visit a Floor. This method will be called for
     * every node in the tree that is a Floor.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitFloor(Floor elm, C context);

    /**
     * Visit a Truncate. This method will be called for
     * every node in the tree that is a Truncate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitTruncate(Truncate elm, C context);

    /**
     * Visit a Abs. This method will be called for
     * every node in the tree that is a Abs.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitAbs(Abs elm, C context);

    /**
     * Visit a Negate. This method will be called for
     * every node in the tree that is a Negate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitNegate(Negate elm, C context);

    /**
     * Visit a Round. This method will be called for
     * every node in the tree that is a Round.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitRound(Round elm, C context);

    /**
     * Visit a Ln. This method will be called for
     * every node in the tree that is a Ln.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitLn(Ln elm, C context);

    /**
     * Visit a Exp. This method will be called for
     * every node in the tree that is a Exp.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitExp(Exp elm, C context);

    /**
     * Visit a Log. This method will be called for
     * every node in the tree that is a Log.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitLog(Log elm, C context);

    /**
     * Visit a Power. This method will be called for
     * every node in the tree that is a Power.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitPower(Power elm, C context);

    /**
     * Visit a Successor. This method will be called for
     * every node in the tree that is a Successor.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitSuccessor(Successor elm, C context);

    /**
     * Visit a Predecessor. This method will be called for
     * every node in the tree that is a Predecessor.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitPredecessor(Predecessor elm, C context);

    /**
     * Visit a MinValue. This method will be called for
     * every node in the tree that is a MinValue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitMinValue(MinValue elm, C context);

    /**
     * Visit a MaxValue. This method will be called for
     * every node in the tree that is a MaxValue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitMaxValue(MaxValue elm, C context);

    /**
     * Visit a Concatenate. This method will be called for
     * every node in the tree that is a Concatenate.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitConcatenate(Concatenate elm, C context);

    /**
     * Visit a Combine. This method will be called for
     * every node in the tree that is a Combine.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitCombine(Combine elm, C context);

    /**
     * Visit a Split. This method will be called for
     * every node in the tree that is a Split.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitSplit(Split elm, C context);

    /**
     * Visit a Length. This method will be called for
     * every node in the tree that is a Length.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitLength(Length elm, C context);

    /**
     * Visit a Upper. This method will be called for
     * every node in the tree that is a Upper.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitUpper(Upper elm, C context);

    /**
     * Visit a Lower. This method will be called for
     * every node in the tree that is a Lower.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitLower(Lower elm, C context);

    /**
     * Visit a Indexer. This method will be called for
     * every node in the tree that is a Indexer.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitIndexer(Indexer elm, C context);

    /**
     * Visit a PositionOf. This method will be called for
     * every node in the tree that is a PositionOf.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitPositionOf(PositionOf elm, C context);

    /**
     * Visit a Substring. This method will be called for
     * every node in the tree that is a Substring.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitSubstring(Substring elm, C context);

    /**
     * Visit a DurationBetween. This method will be called for
     * every node in the tree that is a DurationBetween.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitDurationBetween(DurationBetween elm, C context);

    /**
     * Visit a DifferenceBetween. This method will be called for
     * every node in the tree that is a DifferenceBetween.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitDifferenceBetween(DifferenceBetween elm, C context);

    /**
     * Visit a DateFrom. This method will be called for
     * every node in the tree that is a DateFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitDateFrom(DateFrom elm, C context);

    /**
     * Visit a TimeFrom. This method will be called for
     * every node in the tree that is a TimeFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitTimeFrom(TimeFrom elm, C context);

    /**
     * Visit a TimezoneFrom. This method will be called for
     * every node in the tree that is a TimezoneFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitTimezoneFrom(TimezoneFrom elm, C context);

    /**
     * Visit a DateTimeComponentFrom. This method will be called for
     * every node in the tree that is a DateTimeComponentFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitDateTimeComponentFrom(DateTimeComponentFrom elm, C context);

    /**
     * Visit a TimeOfDay. This method will be called for
     * every node in the tree that is a TimeOfDay.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitTimeOfDay(TimeOfDay elm, C context);

    /**
     * Visit a Today. This method will be called for
     * every node in the tree that is a Today.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitToday(Today elm, C context);

    /**
     * Visit a Now. This method will be called for
     * every node in the tree that is a Now.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitNow(Now elm, C context);

    /**
     * Visit a DateTime. This method will be called for
     * every node in the tree that is a DateTime.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitDateTime(DateTime elm, C context);

    /**
     * Visit a Time. This method will be called for
     * every node in the tree that is a Time.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitTime(Time elm, C context);

    /**
     * Visit a SameAs. This method will be called for
     * every node in the tree that is a SameAs.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitSameAs(SameAs elm, C context);

    /**
     * Visit a SameOrBefore. This method will be called for
     * every node in the tree that is a SameOrBefore.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitSameOrBefore(SameOrBefore elm, C context);

    /**
     * Visit a SameOrAfter. This method will be called for
     * every node in the tree that is a SameOrAfter.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitSameOrAfter(SameOrAfter elm, C context);

    /**
     * Visit a Width. This method will be called for
     * every node in the tree that is a Width.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitWidth(Width elm, C context);

    /**
     * Visit a Start. This method will be called for
     * every node in the tree that is a Start.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitStart(Start elm, C context);

    /**
     * Visit a End. This method will be called for
     * every node in the tree that is a End.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitEnd(End elm, C context);

    /**
     * Visit a Contains. This method will be called for
     * every node in the tree that is a Contains.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitContains(Contains elm, C context);

    /**
     * Visit a ProperContains. This method will be called for
     * every node in the tree that is a ProperContains.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitProperContains(ProperContains elm, C context);

    /**
     * Visit a In. This method will be called for
     * every node in the tree that is a In.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitIn(In elm, C context);

    /**
     * Visit a ProperIn. This method will be called for
     * every node in the tree that is a ProperIn.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitProperIn(ProperIn elm, C context);

    /**
     * Visit a Includes. This method will be called for
     * every node in the tree that is a Includes.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitIncludes(Includes elm, C context);

    /**
     * Visit a IncludedIn. This method will be called for
     * every node in the tree that is a IncludedIn.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitIncludedIn(IncludedIn elm, C context);

    /**
     * Visit a ProperIncludes. This method will be called for
     * every node in the tree that is a ProperIncludes.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitProperIncludes(ProperIncludes elm, C context);

    /**
     * Visit a ProperIncludedIn. This method will be called for
     * every node in the tree that is a ProperIncludedIn.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitProperIncludedIn(ProperIncludedIn elm, C context);

    /**
     * Visit a Before. This method will be called for
     * every node in the tree that is a Before.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitBefore(Before elm, C context);

    /**
     * Visit a After. This method will be called for
     * every node in the tree that is a After.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitAfter(After elm, C context);

    /**
     * Visit a Meets. This method will be called for
     * every node in the tree that is a Meets.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitMeets(Meets elm, C context);

    /**
     * Visit a MeetsBefore. This method will be called for
     * every node in the tree that is a MeetsBefore.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitMeetsBefore(MeetsBefore elm, C context);

    /**
     * Visit a MeetsAfter. This method will be called for
     * every node in the tree that is a MeetsAfter.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitMeetsAfter(MeetsAfter elm, C context);

    /**
     * Visit a Overlaps. This method will be called for
     * every node in the tree that is a Overlaps.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitOverlaps(Overlaps elm, C context);

    /**
     * Visit a OverlapsBefore. This method will be called for
     * every node in the tree that is a OverlapsBefore.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitOverlapsBefore(OverlapsBefore elm, C context);

    /**
     * Visit a OverlapsAfter. This method will be called for
     * every node in the tree that is a OverlapsAfter.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitOverlapsAfter(OverlapsAfter elm, C context);

    /**
     * Visit a Starts. This method will be called for
     * every node in the tree that is a Starts.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitStarts(Starts elm, C context);

    /**
     * Visit a Ends. This method will be called for
     * every node in the tree that is a Ends.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitEnds(Ends elm, C context);

    /**
     * Visit a Collapse. This method will be called for
     * every node in the tree that is a Collapse.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitCollapse(Collapse elm, C context);

    /**
     * Visit a Union. This method will be called for
     * every node in the tree that is a Union.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitUnion(Union elm, C context);

    /**
     * Visit a Intersect. This method will be called for
     * every node in the tree that is a Intersect.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitIntersect(Intersect elm, C context);

    /**
     * Visit a Except. This method will be called for
     * every node in the tree that is a Except.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitExcept(Except elm, C context);

    /**
     * Visit a Literal. This method will be called for
     * every node in the tree that is a Literal.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitExists(Exists elm, C context);

    /**
     * Visit a Times. This method will be called for
     * every node in the tree that is a Times.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitTimes(Times elm, C context);

    /**
     * Visit a Filter. This method will be called for
     * every node in the tree that is a Filter.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitFilter(Filter elm, C context);

    /**
     * Visit a First. This method will be called for
     * every node in the tree that is a First.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitFirst(First elm, C context);

    /**
     * Visit a Last. This method will be called for
     * every node in the tree that is a Last.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitLast(Last elm, C context);

    /**
     * Visit a IndexOf. This method will be called for
     * every node in the tree that is a IndexOf.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitIndexOf(IndexOf elm, C context);

    /**
     * Visit a Flatten. This method will be called for
     * every node in the tree that is a Flatten.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitFlatten(Flatten elm, C context);

    /**
     * Visit a Sort. This method will be called for
     * every node in the tree that is a Sort.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitSort(Sort elm, C context);

    /**
     * Visit a ForEach. This method will be called for
     * every node in the tree that is a ForEach.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitForEach(ForEach elm, C context);

    /**
     * Visit a Distinct. This method will be called for
     * every node in the tree that is a Distinct.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitDistinct(Distinct elm, C context);

    /**
     * Visit a Current. This method will be called for
     * every node in the tree that is a Current.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitCurrent(Current elm, C context);

    /**
     * Visit a SingletonFrom. This method will be called for
     * every node in the tree that is a SingletonFrom.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitSingletonFrom(SingletonFrom elm, C context);

    /**
     * Visit a AggregateExpression. This method will be called for
     * every node in the tree that is a AggregateExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitAggregateExpression(AggregateExpression elm, C context);

    /**
     * Visit a Count. This method will be called for
     * every node in the tree that is a Count.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitCount(Count elm, C context);

    /**
     * Visit a Sum. This method will be called for
     * every node in the tree that is a Sum.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitSum(Sum elm, C context);

    /**
     * Visit a Min. This method will be called for
     * every node in the tree that is a Min.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitMin(Min elm, C context);

    /**
     * Visit a Max. This method will be called for
     * every node in the tree that is a Max.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitMax(Max elm, C context);

    /**
     * Visit a Avg. This method will be called for
     * every node in the tree that is a Avg.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitAvg(Avg elm, C context);

    /**
     * Visit a Median. This method will be called for
     * every node in the tree that is a Median.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitMedian(Median elm, C context);

    /**
     * Visit a Mode. This method will be called for
     * every node in the tree that is a Mode.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitMode(Mode elm, C context);

    /**
     * Visit a Variance. This method will be called for
     * every node in the tree that is a Variance.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitVariance(Variance elm, C context);

    /**
     * Visit a PopulationVariance. This method will be called for
     * every node in the tree that is a PopulationVariance.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitPopulationVariance(PopulationVariance elm, C context);

    /**
     * Visit a StdDev. This method will be called for
     * every node in the tree that is a StdDev.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitStdDev(StdDev elm, C context);

    /**
     * Visit a PopulationStdDev. This method will be called for
     * every node in the tree that is a PopulationStdDev.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitPopulationStdDev(PopulationStdDev elm, C context);

    /**
     * Visit a AllTrue. This method will be called for
     * every node in the tree that is a AllTrue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitAllTrue(AllTrue elm, C context);

    /**
     * Visit a AnyTrue. This method will be called for
     * every node in the tree that is a AnyTrue.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitAnyTrue(AnyTrue elm, C context);

    /**
     * Visit a Property. This method will be called for
     * every node in the tree that is a Property.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitProperty(Property elm, C context);

    /**
     * Visit a AliasedQuerySource. This method will be called for
     * every node in the tree that is a AliasedQuerySource.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitAliasedQuerySource(AliasedQuerySource elm, C context);

    /**
     * Visit a LetClause. This method will be called for
     * every node in the tree that is a LetClause.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitLetClause(LetClause elm, C context);

    /**
     * Visit a RelationshipClause. This method will be called for
     * every node in the tree that is a RelationshipClause.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitRelationshipClause(RelationshipClause elm, C context);

    /**
     * Visit a With. This method will be called for
     * every node in the tree that is a With.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitWith(With elm, C context);

    /**
     * Visit a Without. This method will be called for
     * every node in the tree that is a Without.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitWithout(Without elm, C context);

    /**
     * Visit a SortByItem. This method will be called for
     * every node in the tree that is a SortByItem.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitSortByItem(SortByItem elm, C context);

    /**
     * Visit a ByDirection. This method will be called for
     * every node in the tree that is a ByDirection.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitByDirection(ByDirection elm, C context);

    /**
     * Visit a ByColumn. This method will be called for
     * every node in the tree that is a ByColumn.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitByColumn(ByColumn elm, C context);

    /**
     * Visit a ByExpression. This method will be called for
     * every node in the tree that is a ByExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitByExpression(ByExpression elm, C context);

    /**
     * Visit a SortClause. This method will be called for
     * every node in the tree that is a SortClause.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitSortClause(SortClause elm, C context);

    /**
     * Visit a ReturnClause. This method will be called for
     * every node in the tree that is a ReturnClause.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitReturnClause(ReturnClause elm, C context);

    /**
     * Visit a Query. This method will be called for
     * every node in the tree that is a Query.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitQuery(Query elm, C context);

    /**
     * Visit a AliasRef. This method will be called for
     * every node in the tree that is a AliasRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitAliasRef(AliasRef elm, C context);

    /**
     * Visit a QueryLetRef. This method will be called for
     * every node in the tree that is a QueryLetRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitQueryLetRef(QueryLetRef elm, C context);
}
