package org.cqframework.cql.elm.evaluating;

import org.hl7.elm.r1.*;

/*
A simple ELM engine that is capable of limited evaluation of ELM
required for static analysis and other optimization use cases (such as
constant folding, data requirements analysis, limited constraint validation,
etc.)
 */
public class SimpleElmEngine {
    public SimpleElmEngine() {
    }

    private boolean literalsEqual(Literal left, Literal right) {
        return (left == null && right == null)
                || (left.getValueType() != null && left.getValueType().equals(right.getValueType()) && stringsEqual(left.getValue(), right.getValue()));
    }

    public boolean booleansEqual(Expression left, Expression right) {
        if (left == null && right == null) {
            return true;
        }

        if (left instanceof Literal) {
            if (right instanceof Literal) {
                return literalsEqual((Literal)left, (Literal)right);
            }
        }

        return false;
    }

    public boolean integersEqual(Expression left, Expression right) {
        if (left == null && right == null) {
            return true;
        }

        if (left instanceof Literal) {
            if (right instanceof Literal) {
                return literalsEqual((Literal)left, (Literal)right);
            }
        }

        return false;
    }

    public boolean decimalsEqual(Expression left, Expression right) {
        if (left == null && right == null) {
            return true;
        }

        if (left instanceof Literal) {
            if (right instanceof Literal) {
                return literalsEqual((Literal)left, (Literal)right);
            }
        }

        return false;
    }

    public boolean stringsEqual(Expression left, Expression right) {
        if (left == null && right == null) {
            return true;
        }

        if (left instanceof Literal) {
            if (right instanceof Literal) {
                return literalsEqual((Literal)left, (Literal)right);
            }
        }

        return false;
    }

    public boolean dateTimesEqual(Expression left, Expression right) {
        if (left == null && right == null) {
            return true;
        }

        if (left instanceof Literal) {
            if (right instanceof Literal) {
                return literalsEqual((Literal)left, (Literal)right);
            }
        }

        if (left instanceof Date) {
            if (right instanceof Date) {
                Date leftDate = (Date)left;
                Date rightDate = (Date)right;

                return integersEqual(leftDate.getYear(), rightDate.getYear())
                        && integersEqual(leftDate.getMonth(), rightDate.getMonth())
                        && integersEqual(leftDate.getDay(), rightDate.getDay());
            }
        }

        if (left instanceof Time) {
            if (right instanceof Time) {
                Time leftTime = (Time)left;
                Time rightTime = (Time)right;

                return integersEqual(leftTime.getHour(), rightTime.getHour())
                        && integersEqual(leftTime.getMinute(), rightTime.getMinute())
                        && integersEqual(leftTime.getSecond(), rightTime.getSecond())
                        && integersEqual(leftTime.getMillisecond(), rightTime.getMillisecond());
            }
        }

        if (left instanceof DateTime) {
            if (right instanceof DateTime) {
                DateTime leftDateTime = (DateTime)left;
                DateTime rightDateTime = (DateTime)right;

                return integersEqual(leftDateTime.getYear(), rightDateTime.getYear())
                        && integersEqual(leftDateTime.getMonth(), rightDateTime.getMonth())
                        && integersEqual(leftDateTime.getDay(), rightDateTime.getDay())
                        && integersEqual(leftDateTime.getHour(), rightDateTime.getHour())
                        && integersEqual(leftDateTime.getMinute(), rightDateTime.getMinute())
                        && integersEqual(leftDateTime.getSecond(), rightDateTime.getSecond())
                        && integersEqual(leftDateTime.getMillisecond(), rightDateTime.getMillisecond())
                        && decimalsEqual(leftDateTime.getTimezoneOffset(), rightDateTime.getTimezoneOffset());
            }
        }

        return false;
    }

    public boolean dateRangesEqual(Expression left, Expression right) {
        if (left == null && right == null) {
            return true;
        }

        if (left instanceof Interval) {
            if (right instanceof Interval) {
                Interval leftInterval = (Interval)left;
                Interval rightInterval = (Interval)right;

                return booleansEqual(leftInterval.getLowClosedExpression(), rightInterval.getLowClosedExpression())
                        && dateTimesEqual(leftInterval.getLow(), rightInterval.getLow())
                        && leftInterval.isLowClosed() == rightInterval.isLowClosed()
                        && booleansEqual(leftInterval.getHighClosedExpression(), rightInterval.getHighClosedExpression())
                        && dateTimesEqual(leftInterval.getHigh(), rightInterval.getHigh())
                        && leftInterval.isHighClosed() == rightInterval.isHighClosed();
            }
        }

        // TODO: Strictly speaking this would need to resolve the parameter library since it's not in the ELM if it's a local parameter reference
        if (left instanceof ParameterRef) {
            if (right instanceof ParameterRef) {
                ParameterRef leftParameter = (ParameterRef)left;
                ParameterRef rightParameter = (ParameterRef)right;
                return stringsEqual(leftParameter.getLibraryName(), rightParameter.getLibraryName())
                        && stringsEqual(leftParameter.getName(), rightParameter.getName());
            }
        }

        return false;
    }

    public boolean stringsEqual(String left, String right) {
        return (left == null && right == null) || (left != null && left.equals(right));
    }

    public boolean systemsEqual(CodeSystemRef left, CodeSystemRef right) {
        // TODO: Needs to do the comparison on the URI, but I don't want to have to resolve here
        return (left == null && right == null)
                || (stringsEqual(left.getLibraryName(), right.getLibraryName()) && stringsEqual(left.getName(), right.getName()));
    }

    public boolean valueSetsEqual(ValueSetRef left, ValueSetRef right) {
        // TODO: Needs to do the comparison on the URI, but I don't want to have to resolve here
        return (left == null && right == null)
                || (stringsEqual(left.getLibraryName(), right.getLibraryName()) && stringsEqual(left.getName(), right.getName()));
    }

    public boolean codesEqual(Expression left, Expression right) {
        if (left == null && right == null) {
            return true;
        }

        if (left instanceof Literal) {
            if (right instanceof Literal) {
                return literalsEqual((Literal)left, (Literal)right);
            }
        }

        if (left instanceof ValueSetRef) {
            if (right instanceof ValueSetRef) {
                return valueSetsEqual((ValueSetRef)left, (ValueSetRef)right);
            }
        }
        else if (left instanceof CodeSystemRef) {
            if (right instanceof CodeSystemRef) {
                return systemsEqual((CodeSystemRef)left, (CodeSystemRef)right);
            }
        }
        else if (left instanceof ConceptRef) {
            if (right instanceof ConceptRef) {
                ConceptRef leftConcept = (ConceptRef)left;
                ConceptRef rightConcept = (ConceptRef)right;
                // TODO: Needs to do the comparison on the URI, but I don't want to resolve here
                return stringsEqual(leftConcept.getLibraryName(), rightConcept.getLibraryName())
                        && stringsEqual(leftConcept.getName(), rightConcept.getName());
            }
        }
        else if (left instanceof CodeRef) {
            if (right instanceof CodeRef) {
                CodeRef leftCode = (CodeRef)left;
                CodeRef rightCode = (CodeRef)right;
                // TODO: Needs to do the comparison on the URI, but I don't want to resolve here
                return stringsEqual(leftCode.getLibraryName(), rightCode.getLibraryName())
                        && stringsEqual(leftCode.getName(), rightCode.getName());
            }
        }
        else if (left instanceof Code) {
            if (right instanceof Code) {
                Code leftCode = (Code)left;
                Code rightCode = (Code)right;
                return stringsEqual(leftCode.getCode(), rightCode.getCode())
                        && systemsEqual(leftCode.getSystem(), rightCode.getSystem());
            }
        }
        else if (left instanceof Concept) {
            if (right instanceof Concept) {
                Concept leftConcept = (Concept)left;
                Concept rightConcept = (Concept)right;
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
        }
        else if (left instanceof List) {
            if (right instanceof List) {
                List leftList = (List)left;
                List rightList = (List)right;
                // TOOD: Potentially use a hashSet to avoid order-dependence here
                if (leftList.getElement().size() == rightList.getElement().size()) {
                    for (int i = 0; i < leftList.getElement().size(); i++) {
                        if (!codesEqual(leftList.getElement().get(i), rightList.getElement().get(i))) {
                            return false;
                        }
                    }
                }
            }
        }

        else if (left instanceof ToList) {
            if (right instanceof ToList) {
                Expression leftSingleton = ((ToList)left).getOperand();
                Expression rightSingleton = ((ToList)right).getOperand();
                return codesEqual(leftSingleton, rightSingleton);
            }
        }

        return false;
    }
}
