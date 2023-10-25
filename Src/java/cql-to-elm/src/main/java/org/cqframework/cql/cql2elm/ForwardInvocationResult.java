package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.preprocessor.FunctionDefinitionInfo;

/**
 * Result of each candidate function for forward invocation, including the implicit conversion scores, if applicable.
 */
class ForwardInvocationResult {
    private final int[] scores;
    private final FunctionDefinitionInfo functionDefinitionInfo;

    public static ForwardInvocationResult noMatch(FunctionDefinitionInfo functionDefinitionInfo) {
        return new ForwardInvocationResult(functionDefinitionInfo, Integer.MAX_VALUE);
    }

    public ForwardInvocationResult(FunctionDefinitionInfo functionDefinitionInfo, int... scores) {
        this.functionDefinitionInfo = functionDefinitionInfo;
        this.scores = scores;
    }

    public int[] getScores() {
        return scores;
    }

    public boolean isNoMatch() {
        return scores.length == 1 && Integer.MAX_VALUE == scores[0];
    }

    public FunctionDefinitionInfo getFunctionDefinitionInfo() {
        return functionDefinitionInfo;
    }
}
