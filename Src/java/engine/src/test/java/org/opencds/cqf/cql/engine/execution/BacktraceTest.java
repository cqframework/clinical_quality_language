package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.opencds.cqf.cql.engine.execution.BacktraceTest.ContextMatcher.hasContext;
import static org.opencds.cqf.cql.engine.execution.BacktraceTest.ExpressionDefinitionMatcher.isExpressionDefinition;
import static org.opencds.cqf.cql.engine.execution.BacktraceTest.FunctionMatcher.isFunction;
import static org.opencds.cqf.cql.engine.execution.BacktraceTest.VariableMatcher.isVariable;

import java.util.Objects;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.FunctionDef;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.exception.Backtrace;
import org.opencds.cqf.cql.engine.exception.CqlException;

class BacktraceTest extends CqlTestBase {

    static class FunctionMatcher extends TypeSafeMatcher<FunctionDef> {

        private final String expectedName;

        public FunctionMatcher(final String expectedName) {
            this.expectedName = expectedName;
        }

        @Override
        protected boolean matchesSafely(final FunctionDef functionDefinition) {
            return Objects.equals(functionDefinition.getName(), expectedName);
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("a function named ").appendValue(expectedName);
        }

        public static FunctionMatcher isFunction(final String expectedName) {
            return new FunctionMatcher(expectedName);
        }
    }

    static class ExpressionDefinitionMatcher extends TypeSafeMatcher<ExpressionDef> {

        private final String expectedName;

        public ExpressionDefinitionMatcher(final String expectedName) {
            this.expectedName = expectedName;
        }

        @Override
        protected boolean matchesSafely(final ExpressionDef expressionDefinition) {
            return Objects.equals(expressionDefinition.getName(), expectedName);
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("an expression definition named ").appendValue(expectedName);
        }

        public static ExpressionDefinitionMatcher isExpressionDefinition(final String expectedName) {
            return new ExpressionDefinitionMatcher(expectedName);
        }
    }

    static class VariableMatcher extends TypeSafeMatcher<Variable> {

        private final String expectedName;
        private final Object expectedValue;

        public VariableMatcher(final String expectedName, final Object expectedValue) {
            this.expectedName = expectedName;
            this.expectedValue = expectedValue;
        }

        @Override
        public boolean matchesSafely(final Variable variable) {
            return Objects.equals(variable.getName(), expectedName)
                    && Objects.equals(variable.getValue(), expectedValue);
        }

        @Override
        public void describeTo(final Description description) {
            description
                    .appendText("an argument with name ")
                    .appendValue(expectedName)
                    .appendText(" and value ")
                    .appendValue(expectedValue);
        }

        public static VariableMatcher isVariable(final String expectedName, final Object expectedValue) {
            return new VariableMatcher(expectedName, expectedValue);
        }
    }

    static class ContextMatcher extends TypeSafeMatcher<Backtrace.FunctionoidFrame> {

        private final String expectedName;
        private final Object expectedValue;

        public ContextMatcher(final String expectedName, final Object expectedValue) {
            this.expectedName = expectedName;
            this.expectedValue = expectedValue;
        }

        @Override
        public boolean matchesSafely(Backtrace.FunctionoidFrame frame) {
            return Objects.equals(frame.getContextName(), expectedName)
                    && Objects.equals(frame.getContextValue(), expectedValue);
        }

        @Override
        public void describeTo(Description description) {
            description
                    .appendText("a context with name ")
                    .appendValue(expectedName)
                    .appendText(" and value ")
                    .appendValue(expectedValue);
        }

        public static ContextMatcher hasContext(final String expectedName, final Object expectedValue) {
            return new ContextMatcher(expectedName, expectedValue);
        }
    }

    @Test
    public void backtrace_smoke() {
        try {
            engine.evaluate("BacktraceTest");
            fail("Expected exception but none was thrown");
        } catch (CqlException e) {
            assertThat(e.getBacktrace(), notNullValue());
            final var backtrace = e.getBacktrace();
            final var frames = backtrace.getFrames();
            assertThat(frames, hasSize(3));
            // Frame 1
            final var frame1 = frames.get(0);
            assertThat(frame1, instanceOf(Backtrace.FunctionoidFrame.class));
            final var frame1Functionoid = (Backtrace.FunctionoidFrame) frame1;
            final var definition1 = frame1Functionoid.getDefinition();
            assertThat(definition1, instanceOf(FunctionDef.class));
            assertThat((FunctionDef) definition1, isFunction("F"));
            assertThat(frame1Functionoid.getArguments(), contains(isVariable("X", 3)));
            assertThat(
                    frame1Functionoid.getLocalVariables(), containsInAnyOrder(isVariable("A", 7), isVariable("_", 1)));
            assertThat(frame1Functionoid, hasContext("Unfiltered", null));
            // Frame 2
            final var frame2 = frames.get(1);
            assertThat(frame2, instanceOf(Backtrace.FunctionoidFrame.class));
            final var frame2Functionoid = (Backtrace.FunctionoidFrame) frame2;
            final var definition2 = frame2Functionoid.getDefinition();
            assertThat(definition2, instanceOf(FunctionDef.class));
            assertThat((FunctionDef) definition2, isFunction("G"));
            assertThat(frame2Functionoid.getArguments(), contains(isVariable("Y", 2), isVariable("Z", "hi")));
            assertThat(frame2Functionoid.getLocalVariables(), empty());
            assertThat(frame2Functionoid, hasContext("Unfiltered", null));
            assertThat(frame2Functionoid.getContextValue(), equalTo(null));
            // Frame 3
            final var frame3 = frames.get(2);
            assertThat(frame3, instanceOf(Backtrace.FunctionoidFrame.class));
            final var frame3Functionoid = (Backtrace.FunctionoidFrame) frame3;
            final var definition3 = frame3Functionoid.getDefinition();
            assertThat(definition3, instanceOf(ExpressionDef.class));
            assertThat(definition3, isExpressionDefinition("E2"));
            assertThat(frame3Functionoid.getArguments(), empty());
            assertThat(frame3Functionoid.getLocalVariables(), empty());
            assertThat(frame3Functionoid, hasContext("Unfiltered", null));
        }
    }
}
