package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.*;
import org.cqframework.cql.cql2elm.preprocessor.FunctionDefinitionInfo;
import org.cqframework.cql.elm.tracking.Trackable;
import org.hl7.cql.model.ChoiceType;
import org.hl7.cql.model.DataType;
import org.hl7.elm.r1.ChoiceTypeSpecifier;
import org.hl7.elm.r1.OperandDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
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
        final PreCompileOutput preCompileOutputFromCandidate = candidateFunctionDefinition.getPreCompileOutput();

        if (! callContextFromCaller.getOperatorName().equals(preCompileOutputFromCandidate.getFunctionDef().getName())) {
            return false;
        }

        // LUKETODO:  can we compare return types here?  do we need to?

        final Signature callerSignature = callContextFromCaller.getSignature();
        final List<DataType> paramTypesFromCaller =
                StreamSupport.stream(callerSignature.getOperandTypes().spliterator(), false)
                        .collect(Collectors.toList());

        final List<OperandDef> operandFromCandidate = preCompileOutputFromCandidate.getFunctionDef().getOperand();

        final List<DataType> paramTypesFromCandidate = operandFromCandidate.stream()
                .map(Trackable::getResultType)
                .collect(Collectors.toList());

        // LUKETODO:  it's not from the caller, it's from the callee
        // LUKETODO:  how to handle multiple params, some of which may not be choices?
        final boolean isCandidateHasChoiceType = candidateFunctionDefinition.getPreCompileOutput()
                .getFunctionDef()
                .getOperand()
                .stream()
                .map(OperandDef::getOperandTypeSpecifier)
                .anyMatch(ChoiceTypeSpecifier.class::isInstance);

        final List<OperandDef> operand = candidateFunctionDefinition.getPreCompileOutput()
                .getFunctionDef()
                .getOperand();

        final OperandDef operandDef = operand.get(0);

        final QName operandType = operandDef.getOperandType();

        final Class<? extends OperandDef> aClass = operandDef.getClass();

//        if (callContextFromCaller.getSignature().containsChoices()) {
        if (isCandidateHasChoiceType) {
            return handleChoiceTypes(callContextFromCaller, candidateFunctionDefinition, conversionMap, operatorMap);
        }

        if (!paramTypesFromCaller.equals(paramTypesFromCandidate)) {
            return handleConversionMap(callContextFromCaller, preCompileOutputFromCandidate, conversionMap);
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


        return true;
    }

    /*
    LUKETODO:

    String | Integer variable = ...

    if (is String variable) {
        // do String stuff
    } else (is Integer variable) {
        // do Integer stuff
    }

    A|B
    should be able to call
    A|B|C

    caller choice(A,B)
    should match with
    called choice(A,B,C)

    CALLER DOES NOT HAVE TO BE A CHOICE TYPE
    A > Choice (A,B,C)


    ORDER DOESN'T MATTER


    DAteType.doStuff(String)

    fluent function doStuff(DateType, String)

    so:
     1) Check that BOTH SIDES ARE A ****CHOICE****
     2) Do a check so that the CALLER is a ****SUBSET*** of the CALLEE
     3) Whether or not it's a fluent function is irrelevant
     4) Write forward declaration tests with choices that are NOT FLUENT
     */

    // LUKETODO: figure out how we do



    //        // LUKETODO:  refactor this pattern once it works
//        if (Optional.ofNullable(preCompileOutputFromCandidate.getFunctionDef().isFluent())
//                .orElse(false)) {
//            // LUKETODO:  we need to special case this since the param list is different for the caller and callee for fluent functions
//            logger.info("fluent function");
//
//            if (!paramTypesFromCaller.equals(paramTypesFromCandidate)) {
//                handleChoiceTypes(callContextFromCaller, candidateFunctionDefinition, conversionMap, operatorMap);
//
//                final List<DataType> dataTypesFromCallerExpandChoices = signaturesFromCallerExpandChoices.stream()
//                        .map(Signature::getOperandTypes)
//                        .map(iterable -> StreamSupport.stream(iterable.spliterator(), false))
//                        .map(stream -> stream.collect(Collectors.toList()))
//                        .flatMap(Collection::stream)
//                        .collect(Collectors.toList());
//
//                final List<DataType> dataTypesFromCandidate = paramTypesFromCandidate.stream()
//                        .filter(ChoiceType.class::isInstance)
//                        .map(ChoiceType.class::cast)
//                        .map(ChoiceType::getTypes)
//                        .map(iterable -> StreamSupport.stream(iterable.spliterator(), false))
//                        .map(stream -> stream.collect(Collectors.toList()))
//                        .flatMap(Collection::stream)
//                        .collect(Collectors.toList());
//
//
//                //            final boolean containsFirst = paramTypesFromCandidate.contains(innerCollection);
//                //            final boolean containsSecond = paramTypesFromCandidate.contains(paramTypesFromCaller.get(1));
//                //            final boolean containsThird = paramTypesFromCandidate.contains(paramTypesFromCaller.get(2));
//
//                //            if (! new HashSet<>(paramTypesFromCandidate).containsAll(dataTypesFromCallerExpandChoices)) {
//                //            if (Collections.indexOfSubList(dataTypesFromCandidate, dataTypesFromCallerExpandChoices) == -1) {
//                //            if (! dataTypesFromCandidate.containsAll(dataTypesFromCallerExpandChoices)) {
//                if (!isSubset(dataTypesFromCandidate, dataTypesFromCallerExpandChoices)) {
//                    return handleConversionMap(callContextFromCaller, preCompileOutputFromCandidate, conversionMap);
//                }
//            }
//        } else {
    private static boolean handleChoiceTypes(CallContext callContextFromCaller, FunctionDefinitionInfo candidateFunctionDefinition, ConversionMap conversionMap, OperatorMap operatorMap) {
        final Signature callerSignature = callContextFromCaller.getSignature();
        final List<DataType> paramTypesFromCaller =
                StreamSupport.stream(callerSignature.getOperandTypes().spliterator(), false)
                        .filter(ChoiceType.class::isInstance)
                        .map(ChoiceType.class::cast)
                        .map(ChoiceType::getTypes)
                        .map(iterable -> StreamSupport.stream(iterable.spliterator(), false))
                        .map(stream -> stream.collect(Collectors.toList()))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

        final List<OperandDef> operandFromCandidate = candidateFunctionDefinition.getPreCompileOutput().getFunctionDef().getOperand();

        final List<DataType> paramTypesFromCandidate = operandFromCandidate.stream()
                .map(Trackable::getResultType)
                .collect(Collectors.toList());

        final List<DataType> dataTypesFromCandidate = paramTypesFromCandidate.stream()
                .filter(ChoiceType.class::isInstance)
                .map(ChoiceType.class::cast)
                .map(ChoiceType::getTypes)
                .map(iterable -> StreamSupport.stream(iterable.spliterator(), false))
                .map(stream -> stream.collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

//        final OperatorEntry operatorEntryFromCaller = operatorMap.getOperatorEntryFromOperators(callContextFromCaller.getOperatorName());
//        final List<Signature> signaturesFromCallerExpandChoices = operatorEntryFromCaller.expandChoices(callContextFromCaller.getSignature());
//        final OperatorResolution operatorResolutionFromCaller = operatorMap.resolveOperator(callContextFromCaller, conversionMap);


        return isSubset(dataTypesFromCandidate, paramTypesFromCaller);
    }

    // LUKETODO:  this probably isn't right
    private static <T> boolean isSubset(List<T> lefts, List<T> rights) {
        if (lefts.size() < rights.size()) {
            return false;
        }

        for (T right : rights) {
            if (! lefts.contains(right)) {
                return false;
            }
        }

        return true;
    }

    // LUKETODO:  need to handle the fluent function case of differing param lists here as well
    private static boolean handleConversionMap(CallContext callContextFromCaller, PreCompileOutput candidateFunctionDefinition, ConversionMap conversionMap) {
        final List<DataType> dataTypes = StreamSupport.stream(callContextFromCaller.getSignature().getOperandTypes().spliterator(), false)
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
