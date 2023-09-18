package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.*;
import org.cqframework.cql.cql2elm.preprocessor.FunctionDefinitionInfo;
import org.cqframework.cql.elm.tracking.Trackable;
import org.hl7.cql.model.DataType;
import org.hl7.elm.r1.OperandDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
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

    public static boolean areFunctionHeadersEquivalent(CallContext callContextFromCallingFunction, FunctionDefinitionInfo candidateFunctionDefinition, ConversionMap conversionMap) {
        // sanity check
        final PreCompileOutput preCompileOutput = candidateFunctionDefinition.getPreCompileOutput();
        if (! callContextFromCallingFunction.getOperatorName().equals(preCompileOutput.getFunctionDef().getName())) {
            return false;
        }

        // LUKETODO:  can we compare return types here?  do we need to?

        final Signature callerSignature = callContextFromCallingFunction.getSignature();
        final List<DataType> paramTypesFromCaller =
                StreamSupport.stream(callerSignature.getOperandTypes().spliterator(), false)
                        .collect(Collectors.toList());

        final List<OperandDef> operandFromFound = preCompileOutput.getFunctionDef().getOperand();

        final List<DataType> paramTypesFromFound = operandFromFound.stream()
                .map(Trackable::getResultType)
                .collect(Collectors.toList());

        if (Optional.ofNullable(preCompileOutput.getFunctionDef().isFluent())
                .orElse(false)) {
            // LUKETODO:  we need to special case this since the param list is different for the caller and callee for fluent functions
            logger.info("fluent function");
        }

        // LUKETODO:  this fails because the callContext and preCompile arg lists don't match up:
        // Also, the comes from the passed in List<Expression>, which has 3 params, not 4.
        // The function def has a Choice<DateTime, Quantity, Interval<DateTime>, Interval<Quantity>>, so there's a bug somewhere
        /*
0 = {ClassType@3459} "System.Quantity"
1 = {IntervalType@4699} "interval<System.Quantity>"
2 = {IntervalType@4700} "interval<System.DateTime>"

vs.

0 = {SimpleType@3429} "System.DateTime"
1 = {ClassType@3459} "System.Quantity"
2 = {IntervalType@4730} "interval<System.DateTime>"
3 = {IntervalType@4731} "interval<System.Quantity>"
         */

        if (!paramTypesFromCaller.equals(paramTypesFromFound)) {
            return handleConversionMap(callContextFromCallingFunction, preCompileOutput, conversionMap);

        }

        return true;
    }

    private static boolean handleConversionMap(CallContext callContextFromCallingFunction, PreCompileOutput candidateFunctionDefinition, ConversionMap conversionMap) {
        final List<DataType> dataTypes = StreamSupport.stream(callContextFromCallingFunction.getSignature().getOperandTypes().spliterator(), false)
                .collect(Collectors.toList());

        final List<OperandDef> operandFromFound = candidateFunctionDefinition.getFunctionDef().getOperand();

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
}
