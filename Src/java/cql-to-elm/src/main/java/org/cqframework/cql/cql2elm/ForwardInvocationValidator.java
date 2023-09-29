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
 *     <li>Take into account implicit conversions (ex FHIRHelpers) when the parameter lists don't match, including each conversion's score.</li>
 * </ol>
 */
public class ForwardInvocationValidator {
    private static final Logger logger = LoggerFactory.getLogger(ForwardInvocationValidator.class);

    public static FunctionDefinitionInfo resolveOnSignature(CallContext callContextFromCaller, Iterable<FunctionDefinitionInfo> candidateFunctionDefinitions, ConversionMap conversionMap) {
        if (candidateFunctionDefinitions != null) {
            final List<DataType> paramTypesFromCaller = StreamSupport.stream(callContextFromCaller.getSignature().getOperandTypes().spliterator(), false)
                    .collect(Collectors.toList());;

            final Map<DataType, List<Conversion>> implicitConversionsPerParamType = paramTypesFromCaller.stream()
                    .distinct()
                    .collect(Collectors.toMap(Function.identity(), entry -> conversionMap.getConversions(entry)
                            .stream()
                            .filter(Conversion::isImplicit)
                            .collect(Collectors.toList())));

            final List<ForwardInvocationResult> resolvedFunctionDefinitionInfos = new ArrayList<>();

            for (FunctionDefinitionInfo candidateFunctionDefinition : candidateFunctionDefinitions) {
                final ForwardInvocationResult currentResult = scoreFunctionHeaderOrNothing(callContextFromCaller, candidateFunctionDefinition, implicitConversionsPerParamType);

                evaluateCandidateParamScores(currentResult, resolvedFunctionDefinitionInfos);
            }
            if (resolvedFunctionDefinitionInfos.size() == 0) {
                throw new CqlCompilerException("forward declaration resolution found NO functions for name:" + callContextFromCaller.getOperatorName());
            }
            if (resolvedFunctionDefinitionInfos.size() > 1) {
                throw new CqlCompilerException("forward declaration resolution found more than one functions for name:" + callContextFromCaller.getOperatorName());
            }
            return resolvedFunctionDefinitionInfos.get(0).getFunctionDefinitionInfo();
        }

        return null;
    }

    private static void evaluateCandidateParamScores(ForwardInvocationResult currentResult, List<ForwardInvocationResult> previousForwardInvocationResults) {
        if (currentResult.isNoMatch()) {
            return;
        }

        if (previousForwardInvocationResults.isEmpty()) {
            previousForwardInvocationResults.add(currentResult);
            return;
        }

        if (previousForwardInvocationResults.stream()
                .map(ForwardInvocationResult::getScores)
                .anyMatch(scores -> scores.length != currentResult.getScores().length)) {
            // Sanity check: This should never happen
            return;
        }

        Boolean allScoresLessThanOrEqual = null;
        Boolean isPrevMatchScoreLessThanOrEqual = null;

        for (int index = 0; index < currentResult.getScores().length; index++) {
            final int currentScore = currentResult.getScores()[index];

            final List<int[]> previousScoresForPreviousResults = previousForwardInvocationResults.stream()
                    .map(ForwardInvocationResult::getScores)
                    .collect(Collectors.toList());

            for (int[] previousScores : previousScoresForPreviousResults) {
                final boolean isScoreLessThanOrEqual = currentScore <= previousScores[index];

                if (allScoresLessThanOrEqual == null) {
                    allScoresLessThanOrEqual = isScoreLessThanOrEqual;
                } else {
                    allScoresLessThanOrEqual = isScoreLessThanOrEqual && allScoresLessThanOrEqual;
                }

                if (isPrevMatchScoreLessThanOrEqual != null) {
                    // Previous candidate has scores of [4,5] but the current candidate has scores [5,4] so we cannot resolve
                    if (isPrevMatchScoreLessThanOrEqual != isScoreLessThanOrEqual) {
                        throw new CqlCompilerException("Cannot resolve forward declaration for function call:" + currentResult.getFunctionDefinitionInfo().getName());
                    }
                }

                isPrevMatchScoreLessThanOrEqual = isScoreLessThanOrEqual;
            }
        }

        if (allScoresLessThanOrEqual != null && allScoresLessThanOrEqual.booleanValue()) {
            previousForwardInvocationResults.clear();
            previousForwardInvocationResults.add(currentResult);
        }
    }

    public static ForwardInvocationResult scoreFunctionHeaderOrNothing(CallContext callContextFromCaller, FunctionDefinitionInfo candidateFunctionDefinition, Map<DataType, List<Conversion>> implicitConversionsPerParamType) {
        final FunctionDef functionDefFromCandidate = candidateFunctionDefinition.getPreCompileOutput().getFunctionDef();

        if (! callContextFromCaller.getOperatorName().equals(functionDefFromCandidate.getName())) {
            return ForwardInvocationResult.noMatch(candidateFunctionDefinition);
        }

        final List<DataType> paramTypesFromCaller = StreamSupport.stream(callContextFromCaller.getSignature().getOperandTypes().spliterator(), false)
                .collect(Collectors.toList());;

        final List<DataType> paramTypesFromCandidate = functionDefFromCandidate.getOperand()
                .stream()
                .map(Trackable::getResultType)
                .collect(Collectors.toList());

        if (paramTypesFromCaller.size() != paramTypesFromCandidate.size()) {
            return ForwardInvocationResult.noMatch(candidateFunctionDefinition);
        }

        final int[] scores = new int[paramTypesFromCaller.size()];

        for (int index = 0; index < paramTypesFromCaller.size(); index++) {
            final DataType dataTypeFromCaller = paramTypesFromCaller.get(index);
            final DataType dataTypeFromCandidate = paramTypesFromCandidate.get(index);

            final int score = compareEachMethodParam(dataTypeFromCaller, dataTypeFromCandidate, implicitConversionsPerParamType);

            scores[index] = score;
        }

        return new ForwardInvocationResult(candidateFunctionDefinition, scores);
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

        if (conversionsMatchingToType.size() != 1) {
            return Integer.MAX_VALUE;
        }

        return conversionsMatchingToType.get(0).getScore();
    }
}
