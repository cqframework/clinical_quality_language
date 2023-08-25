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
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// LUKETODO: better name
public class ForwardInvocationChecker {
    static final Logger logger = LoggerFactory.getLogger(ForwardInvocationChecker.class);
    private static final Pattern REGEX_GENERIC_TYPE_NAMESPACE = Pattern.compile("<[a-zA-z]*.");

    public static boolean areFunctionsEquivalent(CallContext callContextFromCaller, FunctionDefinitionInfo foundFunctionToBeEvaluated, Function<cqlParser.FunctionDefinitionContext, PreCompileOutput> preCompileFunction) {
//        if (1 == 1) {
//            // TODO:  passing false here breaks the fluent tests
//            return true;
//        }

        if (areFunctionsSuperficiallyEquivalent(callContextFromCaller, foundFunctionToBeEvaluated)) {
            return areFunctionsPreCompileEquivalent(callContextFromCaller, foundFunctionToBeEvaluated.getDefinition(), preCompileFunction);
        }

        return false;
    }

    private static boolean areFunctionsSuperficiallyEquivalent(CallContext callContextFromCaller, FunctionDefinitionInfo foundFunctionToBeEvaluated) {
        if (! callContextFromCaller.getOperatorName().equals(foundFunctionToBeEvaluated.getName())) {
            return false;
        }

        // LUKETODO:  this breaks the fluent functions tests
        // LUKETODO:  do I need to compare return types as well?

        // LUKETODO:  this code is good as first
        // LUKETODO:  match on number of argumetns, then argument types, then partial compilation
        final Signature callerSignature = callContextFromCaller.getSignature();
        final List<DataType> paramTypesFromCaller =
                StreamSupport.stream(callerSignature.getOperandTypes().spliterator(), false)
                        .collect(Collectors.toUnmodifiableList());
        final List<String> expectedCallParamStrings = paramTypesFromCaller.stream()
                .map(Object::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toUnmodifiableList());
        final String expectedCallParams = callerSignature.getOperandTypes().iterator().next().toString();

        // Right-side context
        final cqlParser.FunctionDefinitionContext definition = foundFunctionToBeEvaluated.getDefinition();
        final List<ParseTree> functionToBeEvaluatedChildren = definition.children;

        // LUKETODO:  data types that are namespaces System.Integer vs. Integer
//        >>>> may need to do partial compilation of both function signatures
        // LUKETODO:  what about methods with multiple parameters?
        // LUKETODO:  ensure we inspect the types, not the names of the arguments
        final Optional<ParseTree> optOperandDefinitionContext =
                functionToBeEvaluatedChildren.stream()
                        .filter(cqlParser.OperandDefinitionContext.class::isInstance)
                        .findFirst();

        final List<ParseTree> operandDefinitionContexts =
                functionToBeEvaluatedChildren.stream()
                        .filter(cqlParser.OperandDefinitionContext.class::isInstance)
                        .collect(Collectors.toUnmodifiableList());

        final List<String> paramStringsFromFunctionToBeEvaluated = operandDefinitionContexts.stream()
                .filter(context -> context.getChildCount() >= 2)
                .map(context -> context.getChild(1))
                .filter(child -> child.getChildCount() >= 1)
                .map(child -> child.getChild(0))
                .map(ParseTree::getText)
                .map(String::toLowerCase)
                .collect(Collectors.toUnmodifiableList());

        // LUKETODO:  list<System.Code> vs list<Code>
//        final boolean isSemanticallyEqual = expectedCallParamStrings.equals(paramStringsFromFunctionToBeEvaluated);
        // LUKETODO:  inline
        final boolean isSemanticallyEqual = areBothTypeListsSemanticallyEquivalent(expectedCallParamStrings, paramStringsFromFunctionToBeEvaluated);

        return isSemanticallyEqual;
    }

    // LUKETODO:  how to get the
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
                        .collect(Collectors.toUnmodifiableList());

        final List<OperandDef> operandFromFound = evaluatedFunctionPreCompileOutput.getFunctionDef().getOperand();

        final List<DataType> paramTypesFromFound = operandFromFound.stream()
                .map(Trackable::getResultType)
                .collect(Collectors.toUnmodifiableList());

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
                // LUKETODO:  generics
//                final String expectedCallParamStringToUse = expectedCallParamString.contains(".") ? expectedCallParamString.split("\\.")[1] : expectedCallParamString;
//                final String paramStringFromFunctionToBeEvaluatedToUse = paramStringFromFunctionToBeEvaluated.contains(".") ? paramStringFromFunctionToBeEvaluated.split("\\.")[1] : expectedCallParamString;
                final String expectedCallParamStringToUse = removeQualifierFromTypeOrGenericType(expectedCallParamString);
                final String paramStringFromFunctionToBeEvaluatedToUse = removeQualifierFromTypeOrGenericType(paramStringFromFunctionToBeEvaluated);

                if (! expectedCallParamStringToUse.equals(paramStringFromFunctionToBeEvaluatedToUse)) {
                    return false;
                }
            }
        }

        return true;
//        return expectedCallParamStrings.equals(paramStringsFromFunctionToBeEvaluated);
    }

    private static String removeQualifierFromTypeOrGenericType(final String typeOrGenericType) {
        if (typeOrGenericType.contains(".")) {
            if (typeOrGenericType.contains("<")) {
                final String replaced = REGEX_GENERIC_TYPE_NAMESPACE.matcher(typeOrGenericType)
                        .replaceAll("<");
                return replaced;
            } else {
                return typeOrGenericType.split("\\.")[1];
            }
        }

        return typeOrGenericType;
    }
}
