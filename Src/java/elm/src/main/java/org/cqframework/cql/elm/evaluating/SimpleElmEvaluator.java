package org.cqframework.cql.elm.evaluating;

import javax.xml.namespace.QName;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.TypeSpecifier;

public class SimpleElmEvaluator {

    private static final SimpleElmEngine engine = new SimpleElmEngine();

    public static SimpleElmEngine simpleElmEngine() {
        return engine;
    }

    public static boolean booleansEqual(Expression left, Expression right) {
        return engine.booleansEqual(left, right);
    }

    public static boolean integersEqual(Expression left, Expression right) {
        return engine.integersEqual(left, right);
    }

    public static boolean decimalsEqual(Expression left, Expression right) {
        return engine.decimalsEqual(left, right);
    }

    public static boolean stringsEqual(Expression left, Expression right) {
        return engine.stringsEqual(left, right);
    }

    public static boolean stringsEqual(String left, String right) {
        return engine.stringsEqual(left, right);
    }

    public static boolean dateTimesEqual(Expression left, Expression right) {
        return engine.dateTimesEqual(left, right);
    }

    public static boolean dateRangesEqual(Expression left, Expression right) {
        return engine.dateRangesEqual(left, right);
    }

    public static boolean codesEqual(Expression left, Expression right) {
        return engine.codesEqual(left, right);
    }

    public static boolean typeSpecifiersEqual(TypeSpecifier left, TypeSpecifier right) {
        return engine.typeSpecifiersEqual(left, right);
    }

    public static boolean qnamesEqual(QName left, QName right) {
        return engine.qnamesEqual(left, right);
    }
}
