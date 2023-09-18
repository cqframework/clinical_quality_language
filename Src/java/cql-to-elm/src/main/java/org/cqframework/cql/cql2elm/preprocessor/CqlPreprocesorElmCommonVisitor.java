package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.cql2elm.StringEscapeUtils;

public class CqlPreprocesorElmCommonVisitor {
    // LUKETODO:  add common methods used by CqlPreprocessorVisitor and Cql2ElmVisitor
    public static boolean isStartingWithDigit(String header, int index) {
        return (index < header.length()) && Character.isDigit(header.charAt(index));
    }
}
