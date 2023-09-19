package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.*;
import org.cqframework.cql.cql2elm.preprocessor.FunctionDefinitionInfo;
import org.cqframework.cql.elm.tracking.Trackable;
import org.hl7.cql.model.DataType;
import org.hl7.elm.r1.FunctionDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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

    public static boolean areFunctionHeadersEquivalent(CallContext callContextFromCaller, FunctionDefinitionInfo candidateFunctionDefinition, ConversionMap conversionMap, OperatorMap operatorMap) {
        // sanity check
        final FunctionDef functionDefFromCandidate = candidateFunctionDefinition.getPreCompileOutput().getFunctionDef();

        if (! callContextFromCaller.getOperatorName().equals(functionDefFromCandidate.getName())) {
            return false;
        }

        final List<DataType> paramTypesFromCaller = StreamSupport.stream(callContextFromCaller.getSignature().getOperandTypes().spliterator(), false)
                .collect(Collectors.toList());;

        final List<DataType> paramTypesFromCandidate = functionDefFromCandidate.getOperand()
                .stream()
                .map(Trackable::getResultType)
                .collect(Collectors.toList());

        if (paramTypesFromCaller.size() != paramTypesFromCandidate.size()) {
            return false;
        }

        for (int index = 0; index < paramTypesFromCaller.size(); index++) {
            final DataType dataTypeFromCaller = paramTypesFromCaller.get(0);
            final DataType dataTypeFromCandidate = paramTypesFromCandidate.get(0);

            if (! compareEachMethodParam(dataTypeFromCaller, dataTypeFromCandidate, conversionMap, operatorMap)) {
                return false;
            }
        }

        return true;
    }

    private static boolean compareEachMethodParam(DataType dataTypeFromCaller, DataType dataTypeFromCandidate, ConversionMap conversionMap, OperatorMap operatorMap) {
        if (dataTypeFromCaller.isCompatibleWith(dataTypeFromCandidate)) {
            return true;
        }

        final Conversion foundConversion = conversionMap.findConversion(dataTypeFromCaller, dataTypeFromCandidate, true, true, operatorMap);

        final List<Conversion> conversionsFromCaller = conversionMap.getConversions(dataTypeFromCaller);

        if (conversionsFromCaller.size() == 0) {
            return false;
        }

        if (conversionsFromCaller.size() > 1) {
            logger.info("MORE THAN ONE CONVERSION TYPE!!!!!!!!!!!!!!!");
            // LUKETODO:  not sure what to do here
            return false;
        }

        final Conversion conversion = conversionsFromCaller.get(0);

        // LUKETODO:  how to replicate this with foundConversion
        if (conversion.getOperator().getFunctionDef() != null) {
            return false;
        }

        final boolean newResult;

        if (foundConversion != null) {
            if (foundConversion.getOperator().getFunctionDef() != null) {
                newResult = false;
            } else {
                newResult = foundConversion.getToType().equals(dataTypeFromCandidate);
            }
        } else {
            newResult = false;
        }

        return newResult;

//            final boolean newResult =  Optional.ofNullable(foundConversion)
//                    .filter(conversion -> conversion.getOperator().getFunctionDef() == null)
//                    .map(Conversion::getToType)
//                    .map(toType -> toType.equals(dataTypeFromCandidate))
//                    .orElse(false);
    }
}
