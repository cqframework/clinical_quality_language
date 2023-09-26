package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.*;
import org.cqframework.cql.cql2elm.preprocessor.FunctionDefinitionInfo;
import org.cqframework.cql.elm.tracking.Trackable;
import org.hl7.cql.model.DataType;
import org.hl7.elm.r1.FunctionDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Compares the function for which we want to resolve a forward reference with one of the candidates by leveraging preCompile/function headers.
 * <p/>
 * <ol>
 *     <li>Compare the {@link CallContext} of the function from the point of view of the calling code to the candidate function definition, which includes a list of {@link FunctionDefinitionInfo}s retrieved by function name.</li>
 *     <li>Compare the data types of the parameters for the calling and called functions.</li>
 *     <li>Take into account implicit conversions (ex FHIRHelpers) when the parameter lists don't match.</li>
 * </ol>
 */
public class ForwardInvocationValidator {
    static final Logger logger = LoggerFactory.getLogger(ForwardInvocationValidator.class);

    public static FunctionDefinitionInfo resolveOnSignature(CallContext callContextFromCaller, Iterable<FunctionDefinitionInfo> candidateFunctionDefinitions, ConversionMap conversionMap) {
        final Map<DataType, List<Conversion>> allMultipleConversions = conversionMap.getAllMultipleConversions();

        allMultipleConversions.forEach((key, value) -> logger.info("from: {}, to: {}", key, value.stream().map(conv -> String.format("to: %s, implicit: %s, score: %s", conv.getToType(), conv.isImplicit(), conv.getScore())).collect(Collectors.toList())));

//from: System.Integer, to: [to: System.String, implicit: false, score: 4, to: System.Boolean, implicit: false, score: 4, to: System.Long, implicit: true, score: 4, to: System.Decimal, implicit: true, score: 4, to: System.Quantity, implicit: true, score: 5]
//from: System.Date, to: [to: System.String, implicit: false, score: 4, to: System.DateTime, implicit: true, score: 4]
//from: System.Decimal, to: [to: System.String, implicit: false, score: 4, to: System.Boolean, implicit: false, score: 4, to: System.Quantity, implicit: true, score: 5]
//from: System.Long, to: [to: System.String, implicit: false, score: 4, to: System.Boolean, implicit: false, score: 4, to: System.Integer, implicit: false, score: 4, to: System.Decimal, implicit: true, score: 4]

        if (candidateFunctionDefinitions != null) {
            final List<DataType> paramTypesFromCaller = StreamSupport.stream(callContextFromCaller.getSignature().getOperandTypes().spliterator(), false)
                    .collect(Collectors.toList());;

                    // LUKETODO:  duplicate keys (Integer and Integer)
            final Map<DataType, List<Conversion>> implicitConversionsPerParamType = paramTypesFromCaller.stream()
                    .distinct()
                    .collect(Collectors.toMap(Function.identity(), entry -> conversionMap.getConversions(entry)
                            .stream()
                            .filter(Conversion::isImplicit)
                            .collect(Collectors.toList())));

            final Optional<Integer> minScoreIfApplicable = implicitConversionsPerParamType.values().stream().flatMap(Collection::stream).map(Conversion::getScore).min(Comparator.comparing(Function.identity()));

            final Map<DataType, List<Conversion>> implicitConversionsPerParamWithMinimumScore = implicitConversionsPerParamType
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                            .filter(conv -> minScoreIfApplicable.map(minScore -> minScore == conv.getScore())
                                    .orElse(false))
                            .collect(Collectors.toList())));

            final List<FunctionDefinitionInfo> resolvedFunctionDefinitionInfos = new ArrayList<>();
            // LUKETODO:  need to consider scores for multiple conversions
            // LUKETODO:  what about functions with multiple implicit params each with different scores?
            ForwardInvocationResult previousResult = ForwardInvocationResult.NON_MATCH;

            for (FunctionDefinitionInfo candidateFunctionDefinition : candidateFunctionDefinitions) {
                /*
                1. NO MATCH
                2. Non-implicit match
                3. Score above
                4. Score below
                 */

                final ForwardInvocationResult currentResult = ForwardInvocationValidator.scoreFunctionHeaderOrNothing(callContextFromCaller, candidateFunctionDefinition, implicitConversionsPerParamType);
                if (ForwardInvocationResult.NON_MATCH == currentResult) {
                    continue;
                }

                if (ForwardInvocationResult.FULL_MATCH == currentResult) {
                    resolvedFunctionDefinitionInfos.clear();
                    resolvedFunctionDefinitionInfos.add(candidateFunctionDefinition);
                }

                if (ForwardInvocationResult.NON_MATCH == previousResult) {
                    resolvedFunctionDefinitionInfos.clear();
                    resolvedFunctionDefinitionInfos.add(candidateFunctionDefinition);
                } else {
                    if (currentResult.scores.length == previousResult.scores.length) {
                        if (currentResult.scores.length > 1) {
                            Boolean allScoresLessThan = null;
                            Boolean previousScoreMatch = null;
                            boolean hasScoreMismatch = false;

                            // LUKETODO:  [4,5] vs [5,4]
                            // >> it could be [4,5,5] vis [5,4,4] and it doesn't matter
                            for (int index = 0; index < currentResult.scores.length; index++) {
                                final int currentScore = currentResult.scores[index];
                                final int previousScore = previousResult.scores[index];

                                if (allScoresLessThan == null) {
                                    allScoresLessThan = currentScore < previousScore;
                                } else {
                                    allScoresLessThan = (currentScore < previousScore) && allScoresLessThan;
                                }

                                if (previousScoreMatch != null) {
                                    if (previousScoreMatch !=  (currentScore < previousScore)) {
                                        hasScoreMismatch = true;
                                    }
                                }

                                previousScoreMatch = currentScore < previousScore;
                            }

                            if (allScoresLessThan != null && allScoresLessThan.booleanValue()) {
                                resolvedFunctionDefinitionInfos.clear();
                                resolvedFunctionDefinitionInfos.add(candidateFunctionDefinition);
                            } else if (hasScoreMismatch) {
                                // LUKETODO:  needs to be more specific in order to capture [4,5] vs [5,4]
                                resolvedFunctionDefinitionInfos.clear();
                            }
                        } else {
                            if (currentResult.getFirstScore() < previousResult.getFirstScore()) {
                                resolvedFunctionDefinitionInfos.clear();
                                resolvedFunctionDefinitionInfos.add(candidateFunctionDefinition);
                            }
                        }
                    }
                }

                previousResult = currentResult;
            }
            if (resolvedFunctionDefinitionInfos.size() == 0) {
                throw new CqlCompilerException("forward declaration resolution found NO functions for name:" + callContextFromCaller.getOperatorName());
            }
            if (resolvedFunctionDefinitionInfos.size() > 1) {
                throw new CqlCompilerException("forward declaration resolution found more than one functions for name:" + callContextFromCaller.getOperatorName());
            }
            return resolvedFunctionDefinitionInfos.get(0);
        }

        return null;
    }

    private static boolean evaluateScores(ForwardInvocationResult previousResult, ForwardInvocationResult currentResult) {
        if (ForwardInvocationResult.NON_MATCH == currentResult) {
            return false;
        }

        if (ForwardInvocationResult.FULL_MATCH == currentResult) {
            return true;
        }

        if (currentResult.scores.length == previousResult.scores.length) {
            Boolean allScoresLessThan = null;

            for (int index = 0; index < currentResult.scores.length; index++) {
                final int currentScore = currentResult.scores[index];
                final int previousScore = previousResult.scores[index];

                if (allScoresLessThan == null) {
                    allScoresLessThan = currentScore < previousScore;
                } else {

                    allScoresLessThan = (currentScore < previousScore) && allScoresLessThan;
                }
            }

            return allScoresLessThan;
        }

        // LUKETODO: solve problem of [4,5] and [5,4] and remove all
        // LUKETODO: get rid of this since it doesn't make sense.... instead check for the previous invocation
        return currentResult.getFirstScore() < previousResult.getFirstScore();
    }

    private static class ForwardInvocationResult {
        private final static ForwardInvocationResult NON_MATCH = new ForwardInvocationResult(Integer.MAX_VALUE);
        private final static ForwardInvocationResult FULL_MATCH = new ForwardInvocationResult(Integer.MIN_VALUE);
        private final int[] scores;

        public ForwardInvocationResult(int... scores) {
            this.scores = scores;
        }

        private int getFirstScore() {
            return scores[0];
        }
    }

    public static ForwardInvocationResult scoreFunctionHeaderOrNothing(CallContext callContextFromCaller, FunctionDefinitionInfo candidateFunctionDefinition, Map<DataType, List<Conversion>> implicitConversionsPerParamType) {
        final FunctionDef functionDefFromCandidate = candidateFunctionDefinition.getPreCompileOutput().getFunctionDef();

        if (! callContextFromCaller.getOperatorName().equals(functionDefFromCandidate.getName())) {
            return ForwardInvocationResult.FULL_MATCH;
        }

        final List<DataType> paramTypesFromCaller = StreamSupport.stream(callContextFromCaller.getSignature().getOperandTypes().spliterator(), false)
                .collect(Collectors.toList());;

        final List<DataType> paramTypesFromCandidate = functionDefFromCandidate.getOperand()
                .stream()
                .map(Trackable::getResultType)
                .collect(Collectors.toList());

        if (paramTypesFromCaller.size() != paramTypesFromCandidate.size()) {
            return ForwardInvocationResult.NON_MATCH;
        }

        final int[] scores = new int[paramTypesFromCaller.size()];

        for (int index = 0; index < paramTypesFromCaller.size(); index++) {
            final DataType dataTypeFromCaller = paramTypesFromCaller.get(index);
            final DataType dataTypeFromCandidate = paramTypesFromCandidate.get(index);

            final int score = compareEachMethodParam(dataTypeFromCaller, dataTypeFromCandidate, implicitConversionsPerParamType);

            if (Integer.MAX_VALUE == score) {
                return ForwardInvocationResult.NON_MATCH;
            }

            scores[index] = score;
        }

        return new ForwardInvocationResult(scores);
    }

    private static int compareEachMethodParam(DataType dataTypeFromCaller, DataType dataTypeFromCandidate, Map<DataType, List<Conversion>> implicitConversionsPerParamType) {
        if (dataTypeFromCaller.isCompatibleWith(dataTypeFromCandidate)) {
            return Integer.MIN_VALUE;
        }

        return handleImplicitConversion(dataTypeFromCaller, dataTypeFromCandidate, implicitConversionsPerParamType);
    }

    private static int handleImplicitConversion(DataType dataTypeFromCaller, DataType dataTypeFromCandidate, Map<DataType, List<Conversion>> implicitConversionsPerParamType) {
        final List<Conversion> conversions = implicitConversionsPerParamType.get(dataTypeFromCaller);

        final List<Conversion> conversionsMatchingToType = conversions
                .stream()
                .filter(conv -> conv.getToType().equals(dataTypeFromCandidate))
                .collect(Collectors.toList());

        if (conversionsMatchingToType.size() > 1) {
            return Integer.MAX_VALUE;
        }

        if (conversionsMatchingToType.size() < 1) {
            return Integer.MAX_VALUE;
        }

        return conversionsMatchingToType.get(0).getScore();
    }
}
