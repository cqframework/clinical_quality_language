package org.cqframework.cql.elm.evaluating;

import java.math.BigDecimal;
import javax.xml.namespace.QName;
import org.hl7.elm.r1.*;

/*
A simple ELM engine that is capable of limited evaluation of ELM
required for static analysis and other optimization use cases (such as
constant folding, data requirements analysis, limited constraint validation,
etc.)
 */
public class SimpleElmEngine {
    public SimpleElmEngine() {}

    private boolean literalsEqual(Literal left, Literal right) {
        return (left == null && right == null)
                || (left != null
                        && left.getValueType() != null
                        && left.getValueType().equals(right.getValueType())
                        && stringsEqual(left.getValue(), right.getValue()));
    }

    public boolean booleansEqual(Expression left, Expression right) {
        return expressionsEqual(left, right);
    }

    public boolean integersEqual(Expression left, Expression right) {
        return expressionsEqual(left, right);
    }

    public boolean decimalsEqual(Expression left, Expression right) {
        return expressionsEqual(left, right);
    }

    public boolean decimalsEqual(BigDecimal left, BigDecimal right) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        return left.equals(right);
    }

    public boolean quantitiesEqual(Quantity left, Quantity right) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null || right == null) {
            return true;
        }

        return decimalsEqual(left.getValue(), right.getValue()) && stringsEqual(left.getUnit(), right.getUnit());
    }

    public boolean stringsEqual(Expression left, Expression right) {
        return expressionsEqual(left, right);
    }

    public boolean dateTimesEqual(Expression left, Expression right) {
        return expressionsEqual(left, right);
    }

    public boolean dateRangesEqual(Expression left, Expression right) {
        return expressionsEqual(left, right);
    }

    public boolean stringsEqual(String left, String right) {
        return (left == null && right == null) || (left != null && left.equals(right));
    }

    public boolean systemsEqual(CodeSystemRef left, CodeSystemRef right) {
        // TODO: Needs to do the comparison on the URI, but I don't want to have to resolve here
        return (left == null && right == null)
                || (left != null
                        && stringsEqual(left.getLibraryName(), right.getLibraryName())
                        && stringsEqual(left.getName(), right.getName()));
    }

    public boolean valueSetsEqual(ValueSetRef left, ValueSetRef right) {
        // TODO: Needs to do the comparison on the URI, but I don't want to have to resolve here
        return (left == null && right == null)
                || (left != null
                        && stringsEqual(left.getLibraryName(), right.getLibraryName())
                        && stringsEqual(left.getName(), right.getName()));
    }

    public boolean codesEqual(Expression left, Expression right) {
        return expressionsEqual(left, right);
    }

    public boolean qnamesEqual(QName left, QName right) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        return left.equals(right);
    }

    public boolean typeSpecifiersEqual(TypeSpecifier left, TypeSpecifier right) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        // NamedTypeSpecifier
        if (left instanceof NamedTypeSpecifier) {
            if (right instanceof NamedTypeSpecifier) {
                NamedTypeSpecifier leftArg = (NamedTypeSpecifier) left;
                NamedTypeSpecifier rightArg = (NamedTypeSpecifier) right;
                return qnamesEqual(leftArg.getName(), rightArg.getName());
            }

            return false;
        }

        // IntervalTypeSpecifier
        if (left instanceof IntervalTypeSpecifier) {
            if (right instanceof IntervalTypeSpecifier) {
                IntervalTypeSpecifier leftArg = (IntervalTypeSpecifier) left;
                IntervalTypeSpecifier rightArg = (IntervalTypeSpecifier) right;
                return typeSpecifiersEqual(leftArg.getPointType(), rightArg.getPointType());
            }

            return false;
        }

        // ListTypeSpecifier
        if (left instanceof ListTypeSpecifier) {
            if (right instanceof ListTypeSpecifier) {
                ListTypeSpecifier leftArg = (ListTypeSpecifier) left;
                ListTypeSpecifier rightArg = (ListTypeSpecifier) right;
                return typeSpecifiersEqual(leftArg.getElementType(), rightArg.getElementType());
            }

            return false;
        }

        // TupleTypeSpecifier
        if (left instanceof TupleTypeSpecifier) {
            if (right instanceof TupleTypeSpecifier) {
                TupleTypeSpecifier leftArg = (TupleTypeSpecifier) left;
                TupleTypeSpecifier rightArg = (TupleTypeSpecifier) right;
                if (leftArg.getElement() != null
                        && rightArg.getElement() != null
                        && leftArg.getElement().size() == rightArg.getElement().size()) {
                    for (int i = 0; i < leftArg.getElement().size(); i++) {
                        TupleElementDefinition leftElement =
                                leftArg.getElement().get(i);
                        TupleElementDefinition rightElement =
                                rightArg.getElement().get(i);
                        if (!typeSpecifiersEqual(leftElement.getType(), rightElement.getType())
                                || !typeSpecifiersEqual(leftElement.getElementType(), rightElement.getElementType())
                                || !stringsEqual(leftElement.getName(), rightElement.getName())) {
                            return false;
                        }
                    }

                    return true;
                }

                return false;
            }

            return false;
        }

        // ChoiceTypeSpecifier
        if (left instanceof ChoiceTypeSpecifier) {
            if (right instanceof ChoiceTypeSpecifier) {
                ChoiceTypeSpecifier leftArg = (ChoiceTypeSpecifier) left;
                ChoiceTypeSpecifier rightArg = (ChoiceTypeSpecifier) right;
                if (leftArg.getType() != null
                        && rightArg.getType() != null
                        && leftArg.getType().size() == rightArg.getType().size()) {
                    for (int i = 0; i < leftArg.getType().size(); i++) {
                        TypeSpecifier leftType = leftArg.getType().get(i);
                        TypeSpecifier rightType = rightArg.getType().get(i);
                        if (!typeSpecifiersEqual(leftType, rightType)) {
                            return false;
                        }
                    }
                }

                if (leftArg.getChoice() != null
                        && rightArg.getChoice() != null
                        && leftArg.getChoice().size() == rightArg.getChoice().size()) {
                    for (int i = 0; i < leftArg.getChoice().size(); i++) {
                        TypeSpecifier leftType = leftArg.getChoice().get(i);
                        TypeSpecifier rightType = rightArg.getChoice().get(i);
                        if (!typeSpecifiersEqual(leftType, rightType)) {
                            return false;
                        }
                    }

                    return true;
                }

                return false;
            }

            return false;
        }

        // False for the possibility of an unrecognized type specifier type
        return false;
    }

    public boolean expressionsEqual(Expression left, Expression right) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        if (left instanceof Literal) {
            if (right instanceof Literal) {
                return literalsEqual((Literal) left, (Literal) right);
            }

            return false;
        }

        if (left instanceof Date) {
            if (right instanceof Date) {
                Date leftDate = (Date) left;
                Date rightDate = (Date) right;

                return integersEqual(leftDate.getYear(), rightDate.getYear())
                        && integersEqual(leftDate.getMonth(), rightDate.getMonth())
                        && integersEqual(leftDate.getDay(), rightDate.getDay());
            }

            return false;
        }

        if (left instanceof Time) {
            if (right instanceof Time) {
                Time leftTime = (Time) left;
                Time rightTime = (Time) right;

                return integersEqual(leftTime.getHour(), rightTime.getHour())
                        && integersEqual(leftTime.getMinute(), rightTime.getMinute())
                        && integersEqual(leftTime.getSecond(), rightTime.getSecond())
                        && integersEqual(leftTime.getMillisecond(), rightTime.getMillisecond());
            }

            return false;
        }

        if (left instanceof DateTime) {
            if (right instanceof DateTime) {
                DateTime leftDateTime = (DateTime) left;
                DateTime rightDateTime = (DateTime) right;

                return integersEqual(leftDateTime.getYear(), rightDateTime.getYear())
                        && integersEqual(leftDateTime.getMonth(), rightDateTime.getMonth())
                        && integersEqual(leftDateTime.getDay(), rightDateTime.getDay())
                        && integersEqual(leftDateTime.getHour(), rightDateTime.getHour())
                        && integersEqual(leftDateTime.getMinute(), rightDateTime.getMinute())
                        && integersEqual(leftDateTime.getSecond(), rightDateTime.getSecond())
                        && integersEqual(leftDateTime.getMillisecond(), rightDateTime.getMillisecond())
                        && decimalsEqual(leftDateTime.getTimezoneOffset(), rightDateTime.getTimezoneOffset());
            }

            return false;
        }

        if (left instanceof Interval) {
            if (right instanceof Interval) {
                Interval leftInterval = (Interval) left;
                Interval rightInterval = (Interval) right;

                return booleansEqual(leftInterval.getLowClosedExpression(), rightInterval.getLowClosedExpression())
                        && dateTimesEqual(leftInterval.getLow(), rightInterval.getLow())
                        && leftInterval.isLowClosed() == rightInterval.isLowClosed()
                        && booleansEqual(
                                leftInterval.getHighClosedExpression(), rightInterval.getHighClosedExpression())
                        && dateTimesEqual(leftInterval.getHigh(), rightInterval.getHigh())
                        && leftInterval.isHighClosed() == rightInterval.isHighClosed();
            }

            return false;
        }

        // TODO: Strictly speaking this would need to resolve the parameter library since it's not in the ELM if it's a
        // local parameter reference
        if (left instanceof ParameterRef) {
            if (right instanceof ParameterRef) {
                ParameterRef leftParameter = (ParameterRef) left;
                ParameterRef rightParameter = (ParameterRef) right;
                return stringsEqual(leftParameter.getLibraryName(), rightParameter.getLibraryName())
                        && stringsEqual(leftParameter.getName(), rightParameter.getName());
            }

            return false;
        }

        if (left instanceof ValueSetRef) {
            if (right instanceof ValueSetRef) {
                return valueSetsEqual((ValueSetRef) left, (ValueSetRef) right);
            }

            return false;
        }

        if (left instanceof CodeSystemRef) {
            if (right instanceof CodeSystemRef) {
                return systemsEqual((CodeSystemRef) left, (CodeSystemRef) right);
            }

            return false;
        }

        if (left instanceof ConceptRef) {
            if (right instanceof ConceptRef) {
                ConceptRef leftConcept = (ConceptRef) left;
                ConceptRef rightConcept = (ConceptRef) right;
                // TODO: Needs to do the comparison on the URI, but I don't want to resolve here
                return stringsEqual(leftConcept.getLibraryName(), rightConcept.getLibraryName())
                        && stringsEqual(leftConcept.getName(), rightConcept.getName());
            }

            return false;
        }

        if (left instanceof CodeRef) {
            if (right instanceof CodeRef) {
                CodeRef leftCode = (CodeRef) left;
                CodeRef rightCode = (CodeRef) right;
                // TODO: Needs to do the comparison on the URI, but I don't want to resolve here
                return stringsEqual(leftCode.getLibraryName(), rightCode.getLibraryName())
                        && stringsEqual(leftCode.getName(), rightCode.getName());
            }

            return false;
        }

        if (left instanceof Code) {
            if (right instanceof Code) {
                Code leftCode = (Code) left;
                Code rightCode = (Code) right;
                return stringsEqual(leftCode.getCode(), rightCode.getCode())
                        && systemsEqual(leftCode.getSystem(), rightCode.getSystem());
            }

            return false;
        }

        if (left instanceof Concept) {
            if (right instanceof Concept) {
                Concept leftConcept = (Concept) left;
                Concept rightConcept = (Concept) right;
                if (leftConcept.getCode() != null && rightConcept.getCode() != null) {
                    for (Code lc : leftConcept.getCode()) {
                        for (Code rc : rightConcept.getCode()) {
                            if (codesEqual(lc, rc)) {
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }

        if (left instanceof List) {
            if (right instanceof List) {
                List leftList = (List) left;
                List rightList = (List) right;
                // TODO: Potentially use a hashSet to avoid order-dependence here
                if (leftList.getElement().size() == rightList.getElement().size()) {
                    for (int i = 0; i < leftList.getElement().size(); i++) {
                        if (!codesEqual(
                                leftList.getElement().get(i),
                                rightList.getElement().get(i))) {
                            return false;
                        }
                    }
                }
            }

            return false;
        }

        if (left instanceof ToList) {
            if (right instanceof ToList) {
                Expression leftSingleton = ((ToList) left).getOperand();
                Expression rightSingleton = ((ToList) right).getOperand();
                return codesEqual(leftSingleton, rightSingleton);
            }

            return false;
        }

        // Quantity
        if (left instanceof Quantity) {
            if (right instanceof Quantity) {
                return quantitiesEqual((Quantity) left, (Quantity) right);
            }

            return false;
        }

        // Ratio
        if (left instanceof Ratio) {
            if (right instanceof Ratio) {
                return quantitiesEqual(((Ratio) left).getDenominator(), ((Ratio) right).getDenominator())
                        && quantitiesEqual(((Ratio) left).getNumerator(), ((Ratio) right).getNumerator());
            }

            return false;
        }

        // TODO: Consider refactoring ComparableElmRequirement?
        // Retrieve

        // InCodeSystem
        if (left instanceof InCodeSystem) {
            if (right instanceof InCodeSystem) {
                InCodeSystem inCodeSystemLeft = (InCodeSystem) left;
                InCodeSystem inCodeSystemRight = (InCodeSystem) right;
                return expressionsEqual(inCodeSystemLeft.getCode(), inCodeSystemRight.getCode())
                        && systemsEqual(inCodeSystemLeft.getCodesystem(), inCodeSystemRight.getCodesystem())
                        && expressionsEqual(
                                inCodeSystemLeft.getCodesystemExpression(),
                                inCodeSystemRight.getCodesystemExpression());
            }

            return false;
        }

        // AnyInCodeSystem
        if (left instanceof AnyInCodeSystem) {
            if (right instanceof AnyInCodeSystem) {
                AnyInCodeSystem anyInCodeSystemLeft = (AnyInCodeSystem) left;
                AnyInCodeSystem anyInCodeSystemRight = (AnyInCodeSystem) right;
                return expressionsEqual(anyInCodeSystemLeft.getCodes(), anyInCodeSystemRight.getCodes())
                        && systemsEqual(anyInCodeSystemLeft.getCodesystem(), anyInCodeSystemRight.getCodesystem())
                        && expressionsEqual(
                                anyInCodeSystemLeft.getCodesystemExpression(),
                                anyInCodeSystemRight.getCodesystemExpression());
            }

            return false;
        }

        // InValueSet
        if (left instanceof InValueSet) {
            if (right instanceof InValueSet) {
                InValueSet inLeft = (InValueSet) left;
                InValueSet inRight = (InValueSet) right;
                return expressionsEqual(inLeft.getCode(), inRight.getCode())
                        && valueSetsEqual(inLeft.getValueset(), inRight.getValueset())
                        && expressionsEqual(inLeft.getValuesetExpression(), inRight.getValuesetExpression());
            }

            return false;
        }

        // AnyInValueSet
        if (left instanceof AnyInValueSet) {
            if (right instanceof AnyInValueSet) {
                AnyInValueSet inLeft = (AnyInValueSet) left;
                AnyInValueSet inRight = (AnyInValueSet) right;
                return expressionsEqual(inLeft.getCodes(), inRight.getCodes())
                        && valueSetsEqual(inLeft.getValueset(), inRight.getValueset())
                        && expressionsEqual(inLeft.getValuesetExpression(), inRight.getValuesetExpression());
            }

            return false;
        }

        // CalculateAge
        if (left instanceof CalculateAge) {
            if (right instanceof CalculateAge) {
                CalculateAge leftArg = (CalculateAge) left;
                CalculateAge rightArg = (CalculateAge) right;
                return expressionsEqual(leftArg.getOperand(), rightArg.getOperand())
                        && leftArg.getPrecision().equals(rightArg.getPrecision());
            }

            return false;
        }

        // Subsumes
        if (left instanceof Subsumes) {
            if (right instanceof Subsumes) {
                Subsumes leftArg = (Subsumes) left;
                Subsumes rightArg = (Subsumes) right;
                if (operandsEqual(leftArg, rightArg)) {
                    return true;
                }

                return false;
            }

            return false;
        }

        // SubsumedBy
        if (left instanceof SubsumedBy) {
            if (right instanceof SubsumedBy) {
                SubsumedBy leftArg = (SubsumedBy) left;
                SubsumedBy rightArg = (SubsumedBy) right;
                if (operandsEqual(leftArg, rightArg)) {
                    return true;
                }

                return false;
            }

            return false;
        }

        // AggregateExpression
        if (left instanceof AggregateExpression) {
            if (right instanceof AggregateExpression) {
                AggregateExpression leftArg = (AggregateExpression) left;
                AggregateExpression rightArg = (AggregateExpression) right;
                return aggregateExpressionsEqual(leftArg, rightArg);
            }

            return false;
        }

        // OperatorExpression
        if (left instanceof OperatorExpression) {
            if (right instanceof OperatorExpression) {
                OperatorExpression leftArg = (OperatorExpression) left;
                OperatorExpression rightArg = (OperatorExpression) right;
                return operatorExpressionsEqual(leftArg, rightArg);
            }

            return false;
        }

        if (!left.getClass().getCanonicalName().equals(right.getClass().getCanonicalName())) {
            return false;
        }

        // AliasRef
        if (left instanceof AliasRef) {
            if (right instanceof AliasRef) {
                AliasRef leftArg = (AliasRef) left;
                AliasRef rightArg = (AliasRef) right;
                return stringsEqual(leftArg.getName(), rightArg.getName());
            }

            return false;
        }

        // Case
        if (left instanceof Case) {
            if (right instanceof Case) {
                Case leftArg = (Case) left;
                Case rightArg = (Case) right;
                if (!expressionsEqual(leftArg.getComparand(), rightArg.getComparand())) {
                    return false;
                }

                if (!expressionsEqual(leftArg.getElse(), rightArg.getElse())) {
                    return false;
                }

                if (leftArg.getCaseItem() != null
                        && rightArg.getCaseItem() != null
                        && leftArg.getCaseItem().size()
                                == rightArg.getCaseItem().size()) {
                    for (int i = 0; i < leftArg.getCaseItem().size(); i++) {
                        CaseItem leftCaseItem = leftArg.getCaseItem().get(i);
                        CaseItem rightCaseItem = rightArg.getCaseItem().get(i);
                        if (!expressionsEqual(leftCaseItem.getWhen(), rightCaseItem.getWhen())
                                || !expressionsEqual(leftCaseItem.getThen(), rightCaseItem.getThen())) {
                            return false;
                        }
                    }

                    return true;
                }

                return false;
            }

            return false;
        }

        // Current
        if (left instanceof Current) {
            if (right instanceof Current) {
                Current leftArg = (Current) left;
                Current rightArg = (Current) right;
                return stringsEqual(leftArg.getScope(), rightArg.getScope());
            }

            return false;
        }

        // FunctionRef
        if (left instanceof FunctionRef) {
            if (right instanceof FunctionRef) {
                FunctionRef leftArg = (FunctionRef) left;
                FunctionRef rightArg = (FunctionRef) right;
                return stringsEqual(leftArg.getLibraryName(), rightArg.getLibraryName())
                        && stringsEqual(leftArg.getName(), rightArg.getName())
                        && operandsEqual(leftArg, rightArg);
            }
        }

        // ExpressionRef
        if (left instanceof ExpressionRef) {
            if (right instanceof ExpressionRef) {
                ExpressionRef leftArg = (ExpressionRef) left;
                ExpressionRef rightArg = (ExpressionRef) right;
                return stringsEqual(leftArg.getLibraryName(), rightArg.getLibraryName())
                        && stringsEqual(leftArg.getName(), rightArg.getName());
            }

            return false;
        }

        // Filter
        if (left instanceof Filter) {
            if (right instanceof Filter) {
                Filter leftArg = (Filter) left;
                Filter rightArg = (Filter) right;
                return expressionsEqual(leftArg.getSource(), rightArg.getSource())
                        && expressionsEqual(leftArg.getCondition(), rightArg.getCondition())
                        && stringsEqual(leftArg.getScope(), rightArg.getScope());
            }

            return false;
        }

        // ForEach
        if (left instanceof ForEach) {
            if (right instanceof ForEach) {
                ForEach leftArg = (ForEach) left;
                ForEach rightArg = (ForEach) right;
                return expressionsEqual(leftArg.getSource(), rightArg.getSource())
                        && expressionsEqual(leftArg.getElement(), rightArg.getElement())
                        && stringsEqual(leftArg.getScope(), rightArg.getScope());
            }

            return false;
        }

        // IdentifierRef
        if (left instanceof IdentifierRef) {
            if (right instanceof IdentifierRef) {
                IdentifierRef leftArg = (IdentifierRef) left;
                IdentifierRef rightArg = (IdentifierRef) right;
                return stringsEqual(leftArg.getLibraryName(), rightArg.getLibraryName())
                        && stringsEqual(leftArg.getName(), rightArg.getName());
            }

            return false;
        }

        // If
        if (left instanceof If) {
            if (right instanceof If) {
                If leftArg = (If) left;
                If rightArg = (If) right;
                return expressionsEqual(leftArg.getCondition(), rightArg.getCondition())
                        && expressionsEqual(leftArg.getThen(), rightArg.getThen())
                        && expressionsEqual(leftArg.getElse(), rightArg.getElse());
            }

            return false;
        }

        // Instance
        if (left instanceof Instance) {
            if (right instanceof Instance) {
                Instance leftArg = (Instance) left;
                Instance rightArg = (Instance) right;
                if (!qnamesEqual(leftArg.getClassType(), rightArg.getClassType())) {
                    return false;
                }

                if (leftArg.getElement() != null
                        && rightArg.getElement() != null
                        && leftArg.getElement().size() == rightArg.getElement().size()) {
                    for (int i = 0; i < leftArg.getElement().size(); i++) {
                        InstanceElement leftElement = leftArg.getElement().get(i);
                        InstanceElement rightElement = rightArg.getElement().get(i);
                        if (!stringsEqual(leftElement.getName(), rightElement.getName())
                                || !expressionsEqual(leftElement.getValue(), rightElement.getValue())) {
                            return false;
                        }
                    }

                    return true;
                }

                return false;
            }

            return false;
        }

        // Iteration
        if (left instanceof Iteration) {
            if (right instanceof Iteration) {
                Iteration leftArg = (Iteration) left;
                Iteration rightArg = (Iteration) right;
                return stringsEqual(leftArg.getScope(), rightArg.getScope());
            }

            return false;
        }

        // MaxValue
        if (left instanceof MaxValue) {
            if (right instanceof MaxValue) {
                MaxValue leftArg = (MaxValue) left;
                MaxValue rightArg = (MaxValue) right;
                return qnamesEqual(leftArg.getValueType(), rightArg.getValueType());
            }

            return false;
        }

        // MinValue
        if (left instanceof MinValue) {
            if (right instanceof MinValue) {
                MinValue leftArg = (MinValue) left;
                MinValue rightArg = (MinValue) right;
                return qnamesEqual(leftArg.getValueType(), rightArg.getValueType());
            }

            return false;
        }

        // Null
        if (left instanceof Null) {
            if (right instanceof Null) {
                return true;
            }

            return false;
        }

        // OperandRef
        if (left instanceof OperandRef) {
            if (right instanceof OperandRef) {
                OperandRef leftArg = (OperandRef) left;
                OperandRef rightArg = (OperandRef) right;
                return stringsEqual(leftArg.getName(), rightArg.getName());
            }

            return false;
        }

        // Property
        if (left instanceof Property) {
            if (right instanceof Property) {
                Property leftArg = (Property) left;
                Property rightArg = (Property) right;
                return stringsEqual(leftArg.getScope(), rightArg.getScope())
                        && stringsEqual(leftArg.getPath(), rightArg.getPath());
            }

            return false;
        }

        // Query
        if (left instanceof Query) {
            if (right instanceof Query) {
                Query leftArg = (Query) left;
                Query rightArg = (Query) right;
            }

            return false;
        }

        // QueryLetRef
        if (left instanceof QueryLetRef) {
            if (right instanceof QueryLetRef) {
                QueryLetRef leftArg = (QueryLetRef) left;
                QueryLetRef rightArg = (QueryLetRef) right;
                return stringsEqual(leftArg.getName(), rightArg.getName());
            }

            return false;
        }

        // Repeat
        if (left instanceof Repeat) {
            if (right instanceof Repeat) {
                Repeat leftArg = (Repeat) left;
                Repeat rightArg = (Repeat) right;
            }

            return false;
        }

        // Sort
        if (left instanceof Sort) {
            if (right instanceof Sort) {
                Sort leftArg = (Sort) left;
                Sort rightArg = (Sort) right;
            }

            return false;
        }

        // Total
        if (left instanceof Total) {
            if (right instanceof Total) {
                Total leftArg = (Total) left;
                Total rightArg = (Total) right;
            }

            return false;
        }

        // Tuple
        if (left instanceof Tuple) {
            if (right instanceof Tuple) {
                Tuple leftArg = (Tuple) left;
                Tuple rightArg = (Tuple) right;
            }

            return false;
        }

        return false;
    }

    public boolean operandsEqual(FunctionRef left, FunctionRef right) {
        if (left.getOperand() != null
                && left.getOperand() != null
                && left.getOperand().size() == left.getOperand().size()) {
            for (int i = 0; i < left.getOperand().size(); i++) {
                if (!expressionsEqual(
                        left.getOperand().get(i), right.getOperand().get(i))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public boolean operandsEqual(BinaryExpression left, BinaryExpression right) {
        if (left.getOperand() != null
                && left.getOperand() != null
                && left.getOperand().size() == left.getOperand().size()) {
            for (int i = 0; i < left.getOperand().size(); i++) {
                if (!expressionsEqual(
                        left.getOperand().get(i), right.getOperand().get(i))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public boolean operandsEqual(TernaryExpression left, TernaryExpression right) {
        if (left.getOperand() != null
                && left.getOperand() != null
                && left.getOperand().size() == left.getOperand().size()) {
            for (int i = 0; i < left.getOperand().size(); i++) {
                if (!expressionsEqual(
                        left.getOperand().get(i), right.getOperand().get(i))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public boolean operandsEqual(NaryExpression left, NaryExpression right) {
        if (left.getOperand() != null
                && left.getOperand() != null
                && left.getOperand().size() == left.getOperand().size()) {
            for (int i = 0; i < left.getOperand().size(); i++) {
                if (!expressionsEqual(
                        left.getOperand().get(i), right.getOperand().get(i))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public boolean operatorExpressionsEqual(OperatorExpression left, OperatorExpression right) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        // UnaryExpression
        if (left instanceof UnaryExpression) {
            if (right instanceof UnaryExpression) {
                UnaryExpression leftArg = (UnaryExpression) left;
                UnaryExpression rightArg = (UnaryExpression) right;
                return unaryExpressionsEqual(leftArg, rightArg);
            }

            return false;
        }

        // BinaryExpression
        if (left instanceof BinaryExpression) {
            if (right instanceof BinaryExpression) {
                BinaryExpression leftArg = (BinaryExpression) left;
                BinaryExpression rightArg = (BinaryExpression) right;
                return binaryExpressionsEqual(leftArg, rightArg);
            }

            return false;
        }

        // TernaryExpression
        if (left instanceof TernaryExpression) {
            if (right instanceof TernaryExpression) {
                TernaryExpression leftArg = (TernaryExpression) left;
                TernaryExpression rightArg = (TernaryExpression) right;
                return ternaryExpressionsEqual(leftArg, rightArg);
            }

            return false;
        }

        // NaryExpression
        if (left instanceof NaryExpression) {
            if (right instanceof NaryExpression) {
                NaryExpression leftArg = (NaryExpression) left;
                NaryExpression rightArg = (NaryExpression) right;
                return naryExpressionsEqual(leftArg, rightArg);
            }

            return false;
        }

        if (!left.getClass().getCanonicalName().equals(right.getClass().getCanonicalName())) {
            return false;
        }

        // Round
        if (left instanceof Round) {
            if (right instanceof Round) {
                Round leftArg = (Round) left;
                Round rightArg = (Round) right;
                return expressionsEqual(leftArg.getOperand(), rightArg.getOperand())
                        && expressionsEqual(leftArg.getPrecision(), rightArg.getPrecision());
            }

            return false;
        }

        // Combine
        if (left instanceof Combine) {
            if (right instanceof Combine) {
                Combine leftArg = (Combine) left;
                Combine rightArg = (Combine) right;
                return expressionsEqual(leftArg.getSource(), rightArg.getSource())
                        && expressionsEqual(leftArg.getSeparator(), rightArg.getSeparator());
            }

            return false;
        }

        // Split
        if (left instanceof Split) {
            if (right instanceof Split) {
                Split leftArg = (Split) left;
                Split rightArg = (Split) right;
                return expressionsEqual(leftArg.getStringToSplit(), rightArg.getStringToSplit())
                        && expressionsEqual(leftArg.getSeparator(), rightArg.getSeparator());
            }

            return false;
        }

        // SplitOnMatches
        if (left instanceof SplitOnMatches) {
            if (right instanceof SplitOnMatches) {
                SplitOnMatches leftArg = (SplitOnMatches) left;
                SplitOnMatches rightArg = (SplitOnMatches) right;
                return expressionsEqual(leftArg.getStringToSplit(), rightArg.getStringToSplit())
                        && expressionsEqual(leftArg.getSeparatorPattern(), rightArg.getSeparatorPattern());
            }

            return false;
        }

        // PositionOf
        if (left instanceof PositionOf) {
            if (right instanceof PositionOf) {
                PositionOf leftArg = (PositionOf) left;
                PositionOf rightArg = (PositionOf) right;
                return expressionsEqual(leftArg.getString(), rightArg.getString())
                        && expressionsEqual(leftArg.getPattern(), rightArg.getPattern());
            }

            return false;
        }

        // LastPositionOf
        if (left instanceof LastPositionOf) {
            if (right instanceof LastPositionOf) {
                LastPositionOf leftArg = (LastPositionOf) left;
                LastPositionOf rightArg = (LastPositionOf) right;
                return expressionsEqual(leftArg.getString(), rightArg.getString())
                        && expressionsEqual(leftArg.getPattern(), rightArg.getPattern());
            }

            return false;
        }

        // Substring
        if (left instanceof Substring) {
            if (right instanceof Substring) {
                Substring leftArg = (Substring) left;
                Substring rightArg = (Substring) right;
                return expressionsEqual(leftArg.getStringToSub(), rightArg.getStringToSub())
                        && expressionsEqual(leftArg.getStartIndex(), rightArg.getStartIndex())
                        && expressionsEqual(leftArg.getLength(), rightArg.getLength());
            }

            return false;
        }

        // TimeOfDay
        // Today
        // Now

        // Time
        if (left instanceof Time) {
            if (right instanceof Time) {
                Time leftArg = (Time) left;
                Time rightArg = (Time) right;
                return expressionsEqual(leftArg.getHour(), rightArg.getHour())
                        && expressionsEqual(leftArg.getMinute(), rightArg.getMinute())
                        && expressionsEqual(leftArg.getSecond(), rightArg.getSecond())
                        && expressionsEqual(leftArg.getMillisecond(), rightArg.getMillisecond());
            }

            return false;
        }

        // Date
        if (left instanceof Date) {
            if (right instanceof Date) {
                Date leftArg = (Date) left;
                Date rightArg = (Date) right;
                return expressionsEqual(leftArg.getYear(), rightArg.getYear())
                        && expressionsEqual(leftArg.getMonth(), rightArg.getMonth())
                        && expressionsEqual(leftArg.getDay(), rightArg.getDay());
            }

            return false;
        }

        // DateTime
        if (left instanceof DateTime) {
            if (right instanceof DateTime) {
                DateTime leftArg = (DateTime) left;
                DateTime rightArg = (DateTime) right;
                return expressionsEqual(leftArg.getYear(), rightArg.getYear())
                        && expressionsEqual(leftArg.getMonth(), rightArg.getMonth())
                        && expressionsEqual(leftArg.getDay(), rightArg.getDay())
                        && expressionsEqual(leftArg.getHour(), rightArg.getHour())
                        && expressionsEqual(leftArg.getMinute(), rightArg.getMinute())
                        && expressionsEqual(leftArg.getSecond(), rightArg.getSecond())
                        && expressionsEqual(leftArg.getMillisecond(), rightArg.getMillisecond())
                        && expressionsEqual(leftArg.getTimezoneOffset(), rightArg.getTimezoneOffset());
            }

            return false;
        }

        // First
        if (left instanceof First) {
            if (right instanceof First) {
                First leftArg = (First) left;
                First rightArg = (First) right;
                return expressionsEqual(leftArg.getSource(), rightArg.getSource())
                        && stringsEqual(leftArg.getOrderBy(), rightArg.getOrderBy());
            }

            return false;
        }

        // Last
        if (left instanceof Last) {
            if (right instanceof Last) {
                Last leftArg = (Last) left;
                Last rightArg = (Last) right;
                return expressionsEqual(leftArg.getSource(), rightArg.getSource())
                        && stringsEqual(leftArg.getOrderBy(), rightArg.getOrderBy());
            }

            return false;
        }

        // IndexOf
        if (left instanceof IndexOf) {
            if (right instanceof IndexOf) {
                IndexOf leftArg = (IndexOf) left;
                IndexOf rightArg = (IndexOf) right;
                return expressionsEqual(leftArg.getSource(), rightArg.getSource())
                        && expressionsEqual(leftArg.getElement(), rightArg.getElement());
            }

            return false;
        }

        // Slice
        if (left instanceof Slice) {
            if (right instanceof Slice) {
                Slice leftArg = (Slice) left;
                Slice rightArg = (Slice) right;
                return expressionsEqual(leftArg.getSource(), rightArg.getSource())
                        && expressionsEqual(leftArg.getStartIndex(), rightArg.getStartIndex())
                        && expressionsEqual(leftArg.getEndIndex(), rightArg.getEndIndex());
            }

            return false;
        }

        // Children
        if (left instanceof Children) {
            if (right instanceof Children) {
                Children leftArg = (Children) left;
                Children rightArg = (Children) right;
                return expressionsEqual(leftArg.getSource(), rightArg.getSource());
            }

            return false;
        }

        // Descendents
        if (left instanceof Descendents) {
            if (right instanceof Descendents) {
                Descendents leftArg = (Descendents) left;
                Descendents rightArg = (Descendents) right;
                return expressionsEqual(leftArg.getSource(), rightArg.getSource());
            }

            return false;
        }

        // Message
        if (left instanceof Message) {
            if (right instanceof Message) {
                Message leftArg = (Message) left;
                Message rightArg = (Message) right;
                return expressionsEqual(leftArg.getSource(), rightArg.getSource())
                        && expressionsEqual(leftArg.getCode(), rightArg.getCode())
                        && expressionsEqual(leftArg.getCondition(), rightArg.getCondition())
                        && expressionsEqual(leftArg.getMessage(), rightArg.getMessage())
                        && expressionsEqual(leftArg.getSeverity(), rightArg.getSeverity());
            }

            return false;
        }

        // Generally speaking we would return false here, but because we've covered all the cases, we return true
        return true;
    }

    public boolean operandsEqual(UnaryExpression left, UnaryExpression right) {
        return expressionsEqual(left.getOperand(), right.getOperand());
    }

    public boolean unaryExpressionsEqual(UnaryExpression left, UnaryExpression right) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        if (!left.getClass().getCanonicalName().equals(right.getClass().getCanonicalName())) {
            return false;
        }

        if (!operandsEqual(left, right)) {
            return false;
        }

        // Abs
        // As
        if (left instanceof As) {
            if (right instanceof As) {
                As leftArg = (As) left;
                As rightArg = (As) right;
                return qnamesEqual(leftArg.getAsType(), rightArg.getAsType())
                        && typeSpecifiersEqual(leftArg.getAsTypeSpecifier(), rightArg.getAsTypeSpecifier())
                        && leftArg.isStrict() == rightArg.isStrict();
            }
        }
        // Ceiling
        // CanConvert
        if (left instanceof CanConvert) {
            if (right instanceof CanConvert) {
                CanConvert leftArg = (CanConvert) left;
                CanConvert rightArg = (CanConvert) right;
                return qnamesEqual(leftArg.getToType(), rightArg.getToType())
                        && typeSpecifiersEqual(leftArg.getToTypeSpecifier(), rightArg.getToTypeSpecifier());
            }

            return false;
        }
        // Convert
        if (left instanceof Convert) {
            if (right instanceof Convert) {
                Convert leftArg = (Convert) left;
                Convert rightArg = (Convert) right;
                return qnamesEqual(leftArg.getToType(), rightArg.getToType())
                        && typeSpecifiersEqual(leftArg.getToTypeSpecifier(), rightArg.getToTypeSpecifier());
            }

            return false;
        }
        // ConvertsToBoolean
        // ConvertsToDate
        // ConvertsToDateTime
        // ConvertsToDecimal
        // ConvertsToInteger
        // ConvertsToLong
        // ConvertsToQuantity
        // ConvertsToRatio
        // ConvertsToString
        // ConvertsToTime
        // DateFrom
        // DateTimeComponentFrom
        if (left instanceof DateTimeComponentFrom) {
            if (right instanceof DateTimeComponentFrom) {
                DateTimeComponentFrom leftArg = (DateTimeComponentFrom) left;
                DateTimeComponentFrom rightArg = (DateTimeComponentFrom) right;
                return leftArg.getPrecision() == rightArg.getPrecision();
            }
            return false;
        }
        // Distinct
        // End
        // Exists
        // Exp
        // Flatten
        // Floor
        // Is
        if (left instanceof Is) {
            if (right instanceof Is) {
                Is leftArg = (Is) left;
                Is rightArg = (Is) right;
                return qnamesEqual(leftArg.getIsType(), rightArg.getIsType())
                        && typeSpecifiersEqual(leftArg.getIsTypeSpecifier(), rightArg.getIsTypeSpecifier());
            }
            return false;
        }
        // IsFalse
        // IsNull
        // IsTrue
        // Length
        // Ln
        // Lower
        // Negate
        // Not
        // PointFrom
        // Precision
        // Predecessor
        // SingletonFrom
        // Size
        // Start
        // Successor
        // TimeFrom
        // TimezoneFrom
        // TimezoneOffsetFrom
        // ToBoolean
        // ToConcept
        // ToChars
        // ToDate
        // ToDateTime
        // ToDecimal
        // ToInteger
        // ToLong
        // ToList
        // ToQuantity
        // ToRatio
        // ToString
        // ToTime
        // Truncate
        // Upper
        // Width

        // We've covered all the special cases above, so if we make it here, the expressions are equal
        return true;
    }

    public boolean binaryExpressionsEqual(BinaryExpression left, BinaryExpression right) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        if (!left.getClass().getCanonicalName().equals(right.getClass().getCanonicalName())) {
            return false;
        }

        if (!operandsEqual(left, right)) {
            return false;
        }

        // TODO: Handle special cases for operators that have a precision modifier
        // Add
        // After
        // And
        // Before
        // CanConvertQuantity
        // Contains
        // ConvertQuantity
        // Collapse
        // DifferenceBetween
        // Divide
        // DurationBetween
        // Ends
        // EndsWith
        // Equal
        // Equivalent
        // Expand
        // Greater
        // GreaterOrEqual
        // HighBoundary
        // Implies
        // In
        // IncludedIn
        // Includes
        // Indexer
        // Less
        // LessOrEqual
        // Log
        // LowBoundary
        // Matches
        // Meets
        // MeetsAfter
        // MeetsBefore
        // Modulo
        // Multiply
        // NotEqual
        // Or
        // Overlaps
        // OverlapsAfter
        // OverlapsBefore
        // Power
        // ProperContains
        // ProperIn
        // ProperIncludedIn
        // ProperIncludes
        // SameAs
        // SameOrAfter
        // SameOrBefore
        // Starts
        // StartsWith
        // Subtract
        // Times
        // TruncatedDivide
        // Xor

        return true;
    }

    public boolean ternaryExpressionsEqual(TernaryExpression left, TernaryExpression right) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        if (!left.getClass().getCanonicalName().equals(right.getClass().getCanonicalName())) {
            return false;
        }

        if (!operandsEqual(left, right)) {
            return false;
        }

        // ReplaceMatches
        return true;
    }

    public boolean naryExpressionsEqual(NaryExpression left, NaryExpression right) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        if (!left.getClass().getCanonicalName().equals(right.getClass().getCanonicalName())) {
            return false;
        }

        if (!operandsEqual(left, right)) {
            return false;
        }

        // Coalesce
        // Concatenate
        // Except
        // Intersect
        // Union
        return false;
    }

    public boolean aggregateExpressionsEqual(AggregateExpression left, AggregateExpression right) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        if (!left.getClass().getCanonicalName().equals(right.getClass().getCanonicalName())) {
            return false;
        }

        if (!expressionsEqual(left.getSource(), right.getSource()) || !stringsEqual(left.getPath(), right.getPath())) {
            return false;
        }

        // Aggregate
        if (left instanceof Aggregate) {
            if (right instanceof Aggregate) {
                Aggregate leftArg = (Aggregate) left;
                Aggregate rightArg = (Aggregate) right;
                return expressionsEqual(leftArg.getInitialValue(), rightArg.getInitialValue())
                        && expressionsEqual(leftArg.getIteration(), rightArg.getIteration());
            }
        }
        // Count
        // Sum
        // Product
        // Min
        // Max
        // Avg
        // GeometricMean
        // Median
        // Mode
        // Variance
        // StdDev
        // PopulationVariance
        // PopulationStdDev
        // AllTrue
        // AnyTrue

        return true;
    }
}
