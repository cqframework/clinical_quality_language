package org.cqframework.cql.elm.evaluation;

import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.elm.visiting.ElmBaseLibraryVisitor;
import org.hl7.elm.r1.*;

public class ElmEvaluationVisitor extends ElmBaseLibraryVisitor<Object, ElmEvaluationContext> {

    public ElmEvaluationVisitor() {
        super();
    }
    @Override
    public Object visitExpression(Expression elm, ElmEvaluationContext context) {
        return super.visitExpression(elm, context);
    }

    @Override
    public Object visitOperatorExpression(OperatorExpression elm, ElmEvaluationContext context) {
        return super.visitOperatorExpression(elm, context);
    }

    @Override
    public Object visitUnaryExpression(UnaryExpression elm, ElmEvaluationContext context) {
        return super.visitUnaryExpression(elm, context);
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression elm, ElmEvaluationContext context) {
        return super.visitBinaryExpression(elm, context);
    }

    @Override
    public Object visitCodeFilterElement(CodeFilterElement elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitDateFilterElement(DateFilterElement elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitOtherFilterElement(OtherFilterElement elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIncludeElement(IncludeElement elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitRetrieve(Retrieve elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitProperty(Property elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSearch(Search elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCodeSystemDef(CodeSystemDef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitValueSetDef(ValueSetDef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCodeDef(CodeDef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConceptDef(ConceptDef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCodeSystemRef(CodeSystemRef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitValueSetRef(ValueSetRef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCodeRef(CodeRef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConceptRef(ConceptRef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCode(Code elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConcept(Concept elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitInCodeSystem(InCodeSystem elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitAnyInCodeSystem(AnyInCodeSystem elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitInValueSet(InValueSet elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitAnyInValueSet(AnyInValueSet elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSubsumes(Subsumes elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSubsumedBy(SubsumedBy elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitQuantity(Quantity elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitRatio(Ratio elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCalculateAge(CalculateAge elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCalculateAgeAt(CalculateAgeAt elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitElement(Element elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitLibrary(Library elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitUsingDef(UsingDef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIncludeDef(IncludeDef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitContextDef(ContextDef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    protected Object defaultResult(Trackable elm, ElmEvaluationContext context) {
        return super.defaultResult(elm, context);
    }

    @Override
    protected Object aggregateResult(Object aggregate, Object nextResult) {
        return super.aggregateResult(aggregate, nextResult);
    }

    @Override
    public Object visitTypeSpecifier(TypeSpecifier elm, ElmEvaluationContext context) {
        return super.visitTypeSpecifier(elm, context);
    }

    @Override
    public Object visitNamedTypeSpecifier(NamedTypeSpecifier elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIntervalTypeSpecifier(IntervalTypeSpecifier elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitListTypeSpecifier(ListTypeSpecifier elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitTupleElementDefinition(TupleElementDefinition elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitTupleTypeSpecifier(TupleTypeSpecifier elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitChoiceTypeSpecifier(ChoiceTypeSpecifier elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitChildren(UnaryExpression elm, ElmEvaluationContext context) {
        return super.visitChildren(elm, context);
    }

    @Override
    public Object visitChildren(BinaryExpression elm, ElmEvaluationContext context) {
        return super.visitChildren(elm, context);
    }

    @Override
    public Object visitChildren(TernaryExpression elm, ElmEvaluationContext context) {
        return super.visitChildren(elm, context);
    }

    @Override
    public Object visitTernaryExpression(TernaryExpression elm, ElmEvaluationContext context) {
        return super.visitTernaryExpression(elm, context);
    }

    @Override
    public Object visitChildren(NaryExpression elm, ElmEvaluationContext context) {
        return super.visitChildren(elm, context);
    }

    @Override
    public Object visitNaryExpression(NaryExpression elm, ElmEvaluationContext context) {
        return super.visitNaryExpression(elm, context);
    }

    @Override
    public Object visitChildren(ExpressionDef elm, ElmEvaluationContext context) {
        return super.visitChildren(elm, context);
    }

    @Override
    public Object visitExpressionDef(ExpressionDef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitFunctionDef(FunctionDef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitAccessModifier(AccessModifier elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitExpressionRef(ExpressionRef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitFunctionRef(FunctionRef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitParameterDef(ParameterDef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitParameterRef(ParameterRef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitOperandDef(OperandDef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitOperandRef(OperandRef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIdentifierRef(IdentifierRef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitLiteral(Literal elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitTupleElement(TupleElement elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitTuple(Tuple elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitInstanceElement(InstanceElement elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitInstance(Instance elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitInterval(Interval elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitList(List elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitAnd(And elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitOr(Or elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitXor(Xor elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitImplies(Implies elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitNot(Not elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIf(If elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCaseItem(CaseItem elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCase(Case elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitNull(Null elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIsNull(IsNull elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIsTrue(IsTrue elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIsFalse(IsFalse elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCoalesce(Coalesce elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIs(Is elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitAs(As elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConvert(Convert elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCanConvert(CanConvert elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConvertsToBoolean(ConvertsToBoolean elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitToBoolean(ToBoolean elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitToChars(ToChars elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitToConcept(ToConcept elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConvertsToDate(ConvertsToDate elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitToDate(ToDate elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConvertsToDateTime(ConvertsToDateTime elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitToDateTime(ToDateTime elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConvertsToLong(ConvertsToLong elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitToLong(ToLong elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConvertsToDecimal(ConvertsToDecimal elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitToDecimal(ToDecimal elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConvertsToInteger(ConvertsToInteger elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitToInteger(ToInteger elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitToList(ToList elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConvertQuantity(ConvertQuantity elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCanConvertQuantity(CanConvertQuantity elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConvertsToQuantity(ConvertsToQuantity elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitToQuantity(ToQuantity elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConvertsToRatio(ConvertsToRatio elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitToRatio(ToRatio elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConvertsToString(ConvertsToString elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitToString(ToString elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConvertsToTime(ConvertsToTime elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitToTime(ToTime elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitEqual(Equal elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitEquivalent(Equivalent elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitNotEqual(NotEqual elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitLess(Less elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitGreater(Greater elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitLessOrEqual(LessOrEqual elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitGreaterOrEqual(GreaterOrEqual elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitAdd(Add elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSubtract(Subtract elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitMultiply(Multiply elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitDivide(Divide elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitTruncatedDivide(TruncatedDivide elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitModulo(Modulo elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCeiling(Ceiling elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitFloor(Floor elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitTruncate(Truncate elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitAbs(Abs elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitNegate(Negate elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitRound(Round elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitLn(Ln elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitExp(Exp elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitLog(Log elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitPower(Power elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSuccessor(Successor elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitPredecessor(Predecessor elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitMinValue(MinValue elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitMaxValue(MaxValue elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitPrecision(Precision elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitLowBoundary(LowBoundary elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitHighBoundary(HighBoundary elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitConcatenate(Concatenate elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCombine(Combine elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSplit(Split elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSplitOnMatches(SplitOnMatches elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitLength(Length elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitUpper(Upper elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitLower(Lower elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIndexer(Indexer elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitPositionOf(PositionOf elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitLastPositionOf(LastPositionOf elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSubstring(Substring elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitStartsWith(StartsWith elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitEndsWith(EndsWith elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitMatches(Matches elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitReplaceMatches(ReplaceMatches elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitDurationBetween(DurationBetween elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitDifferenceBetween(DifferenceBetween elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitDateFrom(DateFrom elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitTimeFrom(TimeFrom elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitTimezoneFrom(TimezoneFrom elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitTimezoneOffsetFrom(TimezoneOffsetFrom elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitDateTimeComponentFrom(DateTimeComponentFrom elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitTimeOfDay(TimeOfDay elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitToday(Today elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitNow(Now elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitDateTime(DateTime elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitDate(Date elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitTime(Time elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSameAs(SameAs elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSameOrBefore(SameOrBefore elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSameOrAfter(SameOrAfter elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitWidth(Width elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSize(Size elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitPointFrom(PointFrom elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitStart(Start elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitEnd(End elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitContains(Contains elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitProperContains(ProperContains elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIn(In elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitProperIn(ProperIn elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIncludes(Includes elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIncludedIn(IncludedIn elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitProperIncludes(ProperIncludes elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitProperIncludedIn(ProperIncludedIn elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitBefore(Before elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitAfter(After elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitMeets(Meets elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitMeetsBefore(MeetsBefore elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitMeetsAfter(MeetsAfter elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitOverlaps(Overlaps elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitOverlapsBefore(OverlapsBefore elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitOverlapsAfter(OverlapsAfter elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitStarts(Starts elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitEnds(Ends elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCollapse(Collapse elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitExpand(Expand elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitUnion(Union elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIntersect(Intersect elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitExcept(Except elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitExists(Exists elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitTimes(Times elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitFilter(Filter elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitFirst(First elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitLast(Last elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSlice(Slice elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitChildren(Children elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitDescendents(Descendents elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitMessage(Message elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIndexOf(IndexOf elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitFlatten(Flatten elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSort(Sort elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitForEach(ForEach elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitRepeat(Repeat elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitDistinct(Distinct elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCurrent(Current elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitIteration(Iteration elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitTotal(Total elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSingletonFrom(SingletonFrom elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitChildren(AggregateExpression elm, ElmEvaluationContext context) {
        return super.visitChildren(elm, context);
    }

    @Override
    public Object visitAggregateExpression(AggregateExpression elm, ElmEvaluationContext context) {
        return super.visitAggregateExpression(elm, context);
    }

    @Override
    public Object visitAggregate(Aggregate elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitCount(Count elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSum(Sum elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitProduct(Product elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitGeometricMean(GeometricMean elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitMin(Min elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitMax(Max elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitAvg(Avg elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitMedian(Median elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitMode(Mode elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitVariance(Variance elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitPopulationVariance(PopulationVariance elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitStdDev(StdDev elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitPopulationStdDev(PopulationStdDev elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitAllTrue(AllTrue elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitAnyTrue(AnyTrue elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitChildren(Property elm, ElmEvaluationContext context) {
        return super.visitChildren(elm, context);
    }

    @Override
    public Object visitChildren(AliasedQuerySource elm, ElmEvaluationContext context) {
        return super.visitChildren(elm, context);
    }

    @Override
    public Object visitAliasedQuerySource(AliasedQuerySource elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitLetClause(LetClause elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSuchThatClause(Expression elm, boolean isWith, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitChildren(RelationshipClause elm, ElmEvaluationContext context) {
        return super.visitChildren(elm, context);
    }

    @Override
    public Object visitRelationshipClause(RelationshipClause elm, ElmEvaluationContext context) {
        return super.visitRelationshipClause(elm, context);
    }

    @Override
    public Object visitWith(With elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitWithout(Without elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSortByItem(SortByItem elm, ElmEvaluationContext context) {
        return super.visitSortByItem(elm, context);
    }

    @Override
    public Object visitByDirection(ByDirection elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitByColumn(ByColumn elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitByExpression(ByExpression elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitSortClause(SortClause elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitAggregateClause(AggregateClause elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitReturnClause(ReturnClause elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitWhereClause(Expression elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitQuery(Query elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitAliasRef(AliasRef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }

    @Override
    public Object visitQueryLetRef(QueryLetRef elm, ElmEvaluationContext context) {
        throw new RuntimeException(String.format("Evaluation support not implemented for %s", elm.getClass().getSimpleName()));
    }
}
