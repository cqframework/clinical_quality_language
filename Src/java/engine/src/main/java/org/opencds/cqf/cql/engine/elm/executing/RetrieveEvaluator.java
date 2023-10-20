package org.opencds.cqf.cql.engine.elm.executing;

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.*;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.ValueSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RetrieveEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveEvaluator .class);
    // LUKETODO:  change is going to be in here
    // LUKETODO:  It'll need some notion of "recontextualizing" the retrieve. This could be a stack of current contexts, or perhaps a separate branch statement.


    private static Iterable<Code> previousCodes = null;

    @SuppressWarnings("unchecked")
    public static Object internalEvaluate(Retrieve elm, State state, ElmLibraryVisitor<Object, State> visitor) {
        QName dataType = state.getEnvironment().fixupQName(elm.getDataType());
        DataProvider dataProvider = state.getEnvironment().resolveDataProvider(dataType);
        Iterable<Code> codes = null;
        String valueSet = null;
        if (elm.getCodes() != null) {
            if (elm.getCodes() instanceof ValueSetRef) {
                ValueSet vs = ValueSetRefEvaluator.toValueSet(state, (ValueSetRef)elm.getCodes());
                valueSet = vs.getId();
            }
            else {
                Object codesResult = visitor.visitExpression(elm.getCodes(), state);
                if (codesResult instanceof ValueSet) {
                    valueSet = ((ValueSet)codesResult).getId();
                } else if (codesResult instanceof String) {
                    List<Code> codesList = new ArrayList<>();
                    codesList.add(new Code().withCode((String) codesResult));
                    codes = codesList;
                } else if (codesResult instanceof Code) {
                    List<Code> codesList = new ArrayList<>();
                    codesList.add((Code) codesResult);
                    codes = codesList;
                } else if (codesResult instanceof Concept) {
                    List<Code> codesList = new ArrayList<>();
                    for (Code conceptCode : ((Concept) codesResult).getCodes()) {
                        codesList.add(conceptCode);
                    }
                    codes = codesList;
                } else {
                    codes = (Iterable<Code>) codesResult;
                }
            }
        }
        Interval dateRange = null;
        if (elm.getDateRange() != null) {
            dateRange = (Interval) visitor.visitExpression(elm.getDateRange(), state);
        }

        Optional<String> optContextFromElm = Optional.empty();
        Optional<String> optContextPathFromElm = Optional.empty();
        List<Code> previousCodesForElm = List.of();

        final Expression context = elm.getContext();

        if (context != null) {
            logger.info("context class: {}", context.getClass());

            if (context instanceof org.hl7.elm.r1.ExpressionRef) {
                final ExpressionRef expressionRef = (ExpressionRef) context;

                final String name = expressionRef.getName();

                logger.info("name: {}", name);

                final List<ExpressionDef> matchingExpressionRefs = state.getCurrentLibrary().getStatements().getDef()
                        .stream()
                        .filter(expression -> name.equals(expression.getName()))
                        .collect(Collectors.toList());

                if (! matchingExpressionRefs.isEmpty()) {
                    final ExpressionDef expressionDef = matchingExpressionRefs.get(0);

                    final Expression expression = expressionDef.getExpression();

                    if (expression instanceof SingletonFrom) {
                        final SingletonFrom singletonFrom = (SingletonFrom) expression;

                        final Expression operand = singletonFrom.getOperand();

                        if (operand instanceof Retrieve) {
                            final Retrieve retrieve = (Retrieve) operand;

                            final Expression retrieveExpression = retrieve.getCodes();

                            if (retrieveExpression instanceof ToList) {
                                final ToList toList = (ToList) retrieveExpression;

                                final Expression toListOperand = toList.getOperand();

                                if (toListOperand instanceof org.hl7.elm.r1.List) {
                                    final org.hl7.elm.r1.List toListOperandAsList = (org.hl7.elm.r1.List) toListOperand;

                                    logger.info("toListOperandAsList: {}", toListOperandAsList);

                                    final List<Expression> element = toListOperandAsList.getElement();

                                    if (! element.isEmpty()) {
                                        final Expression toListExpression = element.get(0);

                                        if (toListExpression instanceof Property) {
                                            final Property property = (Property) toListExpression;

                                            final String contextResultTypeString = context.getResultType().toString();

                                            // LUKETODO:  this is probably completely wrong
                                            optContextFromElm = Optional.of(contextResultTypeString.contains(".") ? contextResultTypeString.split("\\.")[1] : contextResultTypeString);
                                            optContextPathFromElm = Optional.ofNullable(property.getPath());
                                            previousCodesForElm = StreamSupport.stream(previousCodes.spliterator(), false)
                                                    .collect(Collectors.toList());

                                            // LUKETODO:  Codes:  xyz
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        final String currentContext = optContextFromElm.orElse(state.getCurrentContext());
        final String contextPath = optContextPathFromElm.orElse((String) dataProvider.getContextPath(state.getCurrentContext(), dataType.getLocalPart()));
        final Iterable<Code> codesToUse = previousCodesForElm.isEmpty() ? codes : previousCodesForElm;

        Iterable<Object> result = dataProvider.retrieve(currentContext,
                contextPath,
                state.getCurrentContextValue(), dataType.getLocalPart(), elm.getTemplateId(),
                elm.getCodeProperty(), codesToUse, valueSet, elm.getDateProperty(), elm.getDateLowProperty(), elm.getDateHighProperty(),
                dateRange);

        previousCodes = codes;

        // LUKETODO:  it's only at this point that I have the practitioner
        // LUKETODO:  we don't have access to the types here (Practitioner/Patient/etc)
        // LUKETODO:  how to model this in a general way?
        // LUKETODO:  can I get the expression name "Primary Care Doctor" ? from somewhere in here, or just the resource type?

        // TODO: We probably shouldn't eagerly load this, but we need to track
        // this throughout the engine and only add it to the list when it's actually used
        var evaluatedResource = state.getEvaluatedResources();
        if (result instanceof List) {
            evaluatedResource.addAll((List<?>)result);
        } else {
            for (var o : result) {
                evaluatedResource.add(o);
            }
        }

        return result;
    }
}
