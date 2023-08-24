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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// LUKETODO: better name
public class ForwardInvocationChecker {
    static final Logger logger = LoggerFactory.getLogger(Cql2ElmVisitor.class);

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

        final boolean isSemanticallyEqual = expectedCallParamStrings.equals(paramStringsFromFunctionToBeEvaluated);

        return isSemanticallyEqual;


//        if (optOperandDefinitionContext.isPresent()) {
//            final ParseTree parseTree = optOperandDefinitionContext.get();
//
//            final int childCount = parseTree.getChildCount();
//
//            if (childCount >= 2) {
//                final ParseTree paramType = parseTree.getChild(1);
////                logger.info("paramType: {}", paramType);
//                // TODO: error handling
//                // TODO: Probably need some kind of recursion here
//                final ParseTree childLevel1 = paramType.getChild(0);
//                final String childLevel1Text = childLevel1.getText();
////                logger.info("childLevel1.getText() : {}", childLevel1Text);
//
//                // "list" vs. "List"
//                // LUKETODO: we have a bug:   System.String (expectedCallParam) vs. String
//                // should we simply do a contains here and then proceed to compilation
//                final boolean isSemanticallyEqual = expectedCallParam.equalsIgnoreCase(childLevel1.getText());
//                logger.info("childLevel1Text: {}, isSemanticallyEqual: {}", childLevel1Text, isSemanticallyEqual);
//
//                return isSemanticallyEqual;
//            }
//        }

//        return false;
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
}
