package org.cqframework.cql.cql2elm;

import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.cql2elm.model.CallContext;
import org.cqframework.cql.cql2elm.model.Signature;
import org.cqframework.cql.cql2elm.preprocessor.FunctionDefinitionInfo;
import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.gen.cqlParser;
import org.hl7.cql.model.DataType;
import org.hl7.elm.r1.OperandDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Compares the function for which we want to resolve a forward reference with one of the candidates.
 * <p/>
 * There are two distinct steps:
 * <p/>
 * <ol>
 *     <li>First pass: Superficial comparison as a relatively cheap way to validate both functions are the same by checking the String semantics.</li>
 *     <li>Second pass: If the first pass works, trigger a pre compile on the candidate function and compare that to the calling function.  This is more expensive but more accurate.</li>
 * </ol>
 */
public class ForwardInvocationValidator {
    static final Logger logger = LoggerFactory.getLogger(ForwardInvocationValidator.class);
    private static final Pattern REGEX_GENERIC_TYPE_NAMESPACE = Pattern.compile("<[a-zA-z]*.");

    // LUKETODO: We definitely need better names for the parameters
    public static boolean areFunctionsEquivalent(CallContext callContextFromCaller, FunctionDefinitionInfo foundFunctionToBeEvaluated, Function<cqlParser.FunctionDefinitionContext, PreCompileOutput> preCompileFunction) {
        if (areFunctionsSuperficiallyEquivalent(callContextFromCaller, foundFunctionToBeEvaluated)) {
            return areFunctionsPreCompileEquivalent(callContextFromCaller, foundFunctionToBeEvaluated.getDefinition(), preCompileFunction);
        }

        return false;
    }

    private static boolean areFunctionsSuperficiallyEquivalent(CallContext callContextFromCaller, FunctionDefinitionInfo foundFunctionToBeEvaluated) {
        if (! callContextFromCaller.getOperatorName().equals(foundFunctionToBeEvaluated.getName())) {
            return false;
        }

        // LUKETODO:  do I need to compare return types as well?
        // LUKETODO:  Is there any way to get the left param cqlParser.FunctionDefinitionContext here?  otherwise, in the preCompile step we are comparing a CallContext to a PreCompile result, which is not ideal
        final Signature callerSignature = callContextFromCaller.getSignature();
        final List<DataType> paramTypesFromCaller =
                StreamSupport.stream(callerSignature.getOperandTypes().spliterator(), false)
                        .collect(Collectors.toList());
        final List<String> expectedCallParamStrings = paramTypesFromCaller.stream()
                .map(Object::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        final cqlParser.FunctionDefinitionContext definition = foundFunctionToBeEvaluated.getDefinition();
        final List<ParseTree> functionToBeEvaluatedChildren = definition.children;

        final List<ParseTree> operandDefinitionContexts =
                functionToBeEvaluatedChildren.stream()
                        .filter(cqlParser.OperandDefinitionContext.class::isInstance)
                        .collect(Collectors.toList());

        final List<String> paramStringsFromFunctionToBeEvaluated = operandDefinitionContexts.stream()
                .filter(context -> context.getChildCount() >= 2)
                .map(context -> context.getChild(1))
                .filter(child -> child.getChildCount() >= 1)
                .map(child -> child.getChild(0))
                .map(ParseTree::getText)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return areBothTypeListsSemanticallyEquivalent(expectedCallParamStrings, paramStringsFromFunctionToBeEvaluated);
    }

    private static boolean areFunctionsPreCompileEquivalent(CallContext callContextFromCaller, cqlParser.FunctionDefinitionContext definition, Function<cqlParser.FunctionDefinitionContext, PreCompileOutput> preCompileFunction) {
        final PreCompileOutput evaluatedFunctionPreCompileOutput = preCompileFunction.apply(definition);

        // another sanity check
        if (! callContextFromCaller.getOperatorName().equals(evaluatedFunctionPreCompileOutput.getFunctionDef().getName())) {
            return false;
        }

        // LUKETODO:  can we compare return types here?  do we need to?

        final Signature callerSignature = callContextFromCaller.getSignature();
        final List<DataType> paramTypesFromCaller =
                StreamSupport.stream(callerSignature.getOperandTypes().spliterator(), false)
                        .collect(Collectors.toList());

        final List<OperandDef> operandFromFound = evaluatedFunctionPreCompileOutput.getFunctionDef().getOperand();

        final List<DataType> paramTypesFromFound = operandFromFound.stream()
                .map(Trackable::getResultType)
                .collect(Collectors.toList());

        return paramTypesFromCaller.equals(paramTypesFromFound);
    }

    private static boolean areBothTypeListsSemanticallyEquivalent(List<String> expectedCallParamStrings, List<String> paramStringsFromFunctionToBeEvaluated) {
        if (expectedCallParamStrings.size() != paramStringsFromFunctionToBeEvaluated.size()) {
            return false;
        }

        for (int index = 0; index < expectedCallParamStrings.size(); index++) {
            final String expectedCallParamString = expectedCallParamStrings.get(index);
            final String paramStringFromFunctionToBeEvaluated = paramStringsFromFunctionToBeEvaluated.get(index);

            if (! expectedCallParamString.equals(paramStringFromFunctionToBeEvaluated )) {
                final String expectedCallParamStringToUse = removeQualifierFromTypeOrGenericType(expectedCallParamString);
                final String paramStringFromFunctionToBeEvaluatedToUse = removeQualifierFromTypeOrGenericType(paramStringFromFunctionToBeEvaluated);

                if (! expectedCallParamStringToUse.equals(paramStringFromFunctionToBeEvaluatedToUse)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static String removeQualifierFromTypeOrGenericType(final String typeOrGenericType) {
        if (typeOrGenericType.contains(".")) {
            if (typeOrGenericType.contains("<")) {
                return REGEX_GENERIC_TYPE_NAMESPACE.matcher(typeOrGenericType)
                        .replaceAll("<");
            } else {
                return typeOrGenericType.split("\\.")[1];
            }
        }

        return typeOrGenericType;
    }
}
