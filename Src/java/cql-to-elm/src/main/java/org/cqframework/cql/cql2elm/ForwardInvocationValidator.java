package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.*;
import org.cqframework.cql.cql2elm.preprocessor.FunctionDefinitionInfo;
import org.cqframework.cql.elm.tracking.Trackable;
import org.hl7.cql.model.DataType;
import org.hl7.elm.r1.OperandDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
    public static boolean areFunctionsEquivalent(CallContext callContextFromCaller, FunctionDefinitionInfo foundFunctionToBeEvaluated, ConversionMap conversionMap) {
        return areFunctionsPreCompileEquivalent(callContextFromCaller, foundFunctionToBeEvaluated, conversionMap);
    }


    private static boolean areFunctionsPreCompileEquivalent(CallContext callContextFromCaller, FunctionDefinitionInfo functionDefinitionInfo, ConversionMap conversionMap) {
        // another sanity check
        final PreCompileOutput preCompileOutput = functionDefinitionInfo.getPreCompileOutput();
        if (! callContextFromCaller.getOperatorName().equals(preCompileOutput.getFunctionDef().getName())) {
            return false;
        }

        // LUKETODO:  can we compare return types here?  do we need to?

        final Signature callerSignature = callContextFromCaller.getSignature();
        final List<DataType> paramTypesFromCaller =
                StreamSupport.stream(callerSignature.getOperandTypes().spliterator(), false)
                        .collect(Collectors.toList());

        final List<OperandDef> operandFromFound = preCompileOutput.getFunctionDef().getOperand();

        final List<DataType> paramTypesFromFound = operandFromFound.stream()
                .map(Trackable::getResultType)
                .collect(Collectors.toList());

        if (!paramTypesFromCaller.equals(paramTypesFromFound)) {
            return handleConversionMapForPreCompile(callContextFromCaller, preCompileOutput, conversionMap);

        }

        return true;
    }



    private static boolean handleConversionMapForPreCompile(CallContext callContextFromCaller, PreCompileOutput evaluatedFunctionPreCompileOutput, ConversionMap conversionMap) {
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

            if (conversions.size() == 0) {
                // LUKETODO:  not sure what to do here: TestCMS645
                // LUKETODO:  fluent function
                return false;
            }

            if (conversions.size() > 1) {
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
