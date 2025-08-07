package org.opencds.cqf.cql.engine.elm.executing;

import java.util.concurrent.atomic.AtomicLong;
import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.execution.ExpressionResult;
import org.opencds.cqf.cql.engine.execution.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionDefEvaluator {
    private static final Logger log = LoggerFactory.getLogger(ExpressionDefEvaluator.class);

    // Number of times we've retrieved an expression from the cache
    private static final AtomicLong cacheRetrieveCounter = new AtomicLong();
    // Number of times we've evaluated a new expression
    private static final AtomicLong newExpressionCounter = new AtomicLong();
    // Total number of expressions evaluated (cached and not cached)
    private static final AtomicLong totalCounter = new AtomicLong();

    public static Object internalEvaluate(
            ExpressionDef expressionDef, State state, ElmLibraryVisitor<Object, State> visitor) {
        boolean isEnteredContext = false;
        if (expressionDef.getContext() != null) {
            isEnteredContext = state.enterContext(expressionDef.getContext());
        }
        try {
            state.pushEvaluatedResourceStack();
            VersionedIdentifier libraryId = state.getCurrentLibrary().getIdentifier();
            final boolean isExpressionCachingEnabled = state.getCache().isExpressionCachingEnabled();
            final boolean isExpressionCached = state.getCache().isExpressionCached(libraryId, expressionDef.getName());
            //            log.info(
            //                    "1234: CACHING ENABLED: {} IS EXPRESSION CACHED: {} libraryId: [{}] definition [{}]",
            //                    rightPad(isExpressionCachingEnabled, 5),
            //                    rightPad(isExpressionCached, 5),
            //                    rightPad(libraryId.getId(), 10),
            //                    rightPad(expressionDef.getName(), 20));

            if (isExpressionCachingEnabled && isExpressionCached) {

                var er = state.getCache().getCachedExpression(libraryId, expressionDef.getName());
                state.getEvaluatedResources().addAll(er.evaluatedResources());
                final long cacheCount = cacheRetrieveCounter.incrementAndGet();
                final long totalCount = totalCounter.incrementAndGet();

                log.info(
                        "1234: HITTING CACHE: libraryId: [{}] definition [{}], cacheCount: [{}], totalCount: [{}]",
                        rightPad(libraryId.getId(), 10),
                        rightPad(expressionDef.getName(), 20),
                        rightPad(cacheCount, 10),
                        rightPad(totalCount, 10));

                // TODO(jmoringe): make public interface
                final var frame = state.getTopActivationFrame();
                assert frame.element == expressionDef;
                frame.isCached = true;

                return er.value();
            }

            Object value = visitor.visitExpression(expressionDef.getExpression(), state);

            if (state.getCache().isExpressionCachingEnabled()) {
                var er = new ExpressionResult(value, state.getEvaluatedResources());
                state.getCache().cacheExpression(libraryId, expressionDef.getName(), er);
                final long newEvalCount = newExpressionCounter.incrementAndGet();
                final long totalCount = totalCounter.incrementAndGet();
                log.info(
                        "1234: ADDING TO EXPRESSION CACHE libraryId: [{}] definition [{}] with new eval count [{}], totalCount [{}]",
                        rightPad(libraryId.getId(), 10),
                        rightPad(expressionDef.getName(), 20),
                        rightPad(newEvalCount, 10),
                        rightPad(totalCount, 10));
            }

            return value;
        } finally {
            state.popEvaluatedResourceStack();
            // state.enterContext.getContext() == null will result in isEnteredContext = false, which means pop() won't
            // be called
            state.exitContext(isEnteredContext);
        }
    }

    private static String rightPad(Object o, int length) {
        if (o == null) {
            return null;
        }
        String s = o.toString();
        if (s.length() >= length) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
