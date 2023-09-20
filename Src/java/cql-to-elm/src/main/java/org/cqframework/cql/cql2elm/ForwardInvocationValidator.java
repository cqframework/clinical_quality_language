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

        return handleImplicitConversion(dataTypeFromCaller, dataTypeFromCandidate, conversionMap, operatorMap);
    }

    private static boolean handleImplicitConversion(DataType theDataTypeFromCaller, DataType theDataTypeFromCandidate, ConversionMap theConversionMap, OperatorMap theOperatorMap) {
        final Conversion foundConversion = theConversionMap.findConversion(theDataTypeFromCaller, theDataTypeFromCandidate, false, true, theOperatorMap);

        return Optional.ofNullable(foundConversion)
                .map(nonNullConversion -> {
                    // Handle the case of a forward declaration of to$tring(value Concept) calling toString(value List<System.Code>) as this would otherwise
                    // result in an error due to a false positive of the forward declaration resolving itself
                    if (foundConversion.getOperator() != null) {
                        if (foundConversion.getOperator().getFunctionDef() != null) {
                            return false;
                        }
                    }

                    return foundConversion.getToType().equals(theDataTypeFromCandidate);
                })
                .orElse(false);
    }
}
