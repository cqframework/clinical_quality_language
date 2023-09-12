package org.cqframework.cql.cql2elm;

import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.cql2elm.model.*;
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
    private static final Pattern REGEX_GENERIC_TYPE_NAMESPACE = Pattern.compile("[a-zA-z]*\\.");

    // LUKETODO: We definitely need better names for the parameters
    // LUKETODO: why is BaseTest.TestIntervalImplicitConversion failing when the params clearly don't match: (13,11): Could not resolve call to operator LengthInDays with signature (FHIR.Period).
    public static boolean areFunctionsEquivalent(CallContext callContextFromCaller, FunctionDefinitionInfo foundFunctionToBeEvaluated, Function<cqlParser.FunctionDefinitionContext, PreCompileOutput> preCompileFunction, ConversionMap conversionMap, OperatorMap operatorMap) {
        if (areFunctionsSuperficiallyEquivalent(callContextFromCaller, foundFunctionToBeEvaluated, conversionMap, operatorMap)) {
            return areFunctionsPreCompileEquivalent(callContextFromCaller, foundFunctionToBeEvaluated.getDefinition(), preCompileFunction, conversionMap);
        }

        return false;
//        return true;
    }

    private static boolean areFunctionsSuperficiallyEquivalent(CallContext callContextFromCaller, FunctionDefinitionInfo foundFunctionToBeEvaluated, ConversionMap conversionMap, OperatorMap operatorMap) {
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

        return areBothTypeListsSemanticallyEquivalent(callContextFromCaller, expectedCallParamStrings, paramStringsFromFunctionToBeEvaluated, conversionMap, operatorMap);
    }

    // LUKETODO:  the way this is resolved by the compiler is with CompiledLibrary#resolveOperator, which takes a ConversionMap
    // The ConversionMap comes from the LibaryBuilder
    // LibraryBuilder passes the ConversionMap

    private static boolean areFunctionsPreCompileEquivalent(CallContext callContextFromCaller, cqlParser.FunctionDefinitionContext definition, Function<cqlParser.FunctionDefinitionContext, PreCompileOutput> preCompileFunction, ConversionMap conversionMap) {
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

        if (!paramTypesFromCaller.equals(paramTypesFromFound)) {
            return handleConversionMapForPreCompile(callContextFromCaller, evaluatedFunctionPreCompileOutput, conversionMap);

        }

        return true;
    }

    private static boolean areBothTypeListsSemanticallyEquivalent(CallContext callContextFromCaller, List<String> expectedCallParamStrings, List<String> paramStringsFromFunctionToBeEvaluated, ConversionMap conversionMap, OperatorMap operatorMap) {
        if (expectedCallParamStrings.size() != paramStringsFromFunctionToBeEvaluated.size()) {
            return false;
        }

        boolean result = true;

        for (int index = 0; index < expectedCallParamStrings.size(); index++) {
            final String expectedCallParamString = expectedCallParamStrings.get(index);
            final String paramStringFromFunctionToBeEvaluated = paramStringsFromFunctionToBeEvaluated.get(index);

            if (! expectedCallParamString.equals(paramStringFromFunctionToBeEvaluated )) {
                final String expectedCallParamStringToUse = removeQualifierFromTypeOrGenericType(expectedCallParamString);
                final String paramStringFromFunctionToBeEvaluatedToUse = removeQualifierFromTypeOrGenericType(paramStringFromFunctionToBeEvaluated);

                if (! expectedCallParamStringToUse.equals(paramStringFromFunctionToBeEvaluatedToUse)) {
                    result = false;
                }
            }
        }

        if (!result) {
            return handleConversionMap(callContextFromCaller, paramStringsFromFunctionToBeEvaluated, conversionMap);
        }

        return true;
    }

    // LUKETODO: better name for method
    private static boolean handleConversionMap(CallContext callContextFromCaller, List<String> paramStringsFromFunctionToBeEvaluated, ConversionMap conversionMap) {
//        if (1 == 1) {
//            return false;
//        }
        // LUKETODO: test multiple param functions
        final List<DataType> dataTypes = StreamSupport.stream(callContextFromCaller.getSignature().getOperandTypes().spliterator(), false)
                .collect(Collectors.toList());
        // sanity check
        if (dataTypes.size() != paramStringsFromFunctionToBeEvaluated.size()) {
            return false;
        }

        for (int index = 0; index < dataTypes .size(); index++) {
            final DataType dataType = dataTypes.get(index);
            final String paramStringFromFunctionToBeEvaluated = paramStringsFromFunctionToBeEvaluated.get(index);

            final List<Conversion> conversions = conversionMap.getConversions(dataType);
            if (conversions.size() != 1) {
                // LUKETODO:  not sure what to do here
                return false;
            }

            final Conversion conversion = conversions.get(0);

            // LUKETODO:  this is a NASTY HACK to get all LibraryTests working
            if (conversion.getOperator().getFunctionDef() != null) {
                return false;
            }

            final String conversionTypeString = conversion.getToType().toString();
            final String removedQualifier = removeQualifierFromTypeOrGenericType(conversionTypeString);

            final boolean conversionTest = removedQualifier.equalsIgnoreCase(paramStringFromFunctionToBeEvaluated);

            if (!conversionTest) {
                return false;
            }
        }

        return true;
    }

    private static boolean handleConversionMapForPreCompile(CallContext callContextFromCaller, PreCompileOutput evaluatedFunctionPreCompileOutput, ConversionMap conversionMap) {
//        if (1 == 1) {
//            return false;
//        }
        final List<DataType> dataTypes = StreamSupport.stream(callContextFromCaller.getSignature().getOperandTypes().spliterator(), false)
                .collect(Collectors.toList());

        final List<OperandDef> operandFromFound = evaluatedFunctionPreCompileOutput.getFunctionDef().getOperand();

        final List<DataType> paramTypesFromFound = operandFromFound.stream()
                .map(Trackable::getResultType)
                .collect(Collectors.toList());

        if (dataTypes.size() != paramTypesFromFound.size()) {
            return false;
        }

        for (int index = 0; index < dataTypes.size(); index++) {
            final DataType dataType = dataTypes.get(index);
            final DataType dataType1 = paramTypesFromFound.get(index);

            final List<Conversion> conversions = conversionMap.getConversions(dataType);

            if (conversions.size() != 1) {
                // LUKETODO:  not sure what to do here
                return false;
            }

            final Conversion conversion = conversions.get(0);
            if (conversion.getOperator().getFunctionDef() != null) {
                return false;
            }
            final DataType conversionType = conversion.getToType();

            final boolean conversionTest = dataType1.equals(conversionType);

            if (!conversionTest) {
                return false;
            }
        }

        return true;
    }

    private static String removeQualifierFromTypeOrGenericType(final String typeOrGenericType) {
        return REGEX_GENERIC_TYPE_NAMESPACE.matcher(typeOrGenericType)
                .replaceAll("");
    }
}
