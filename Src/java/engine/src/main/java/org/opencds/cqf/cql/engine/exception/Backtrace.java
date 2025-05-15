package org.opencds.cqf.cql.engine.exception;

import java.util.*;
import java.util.stream.Collectors;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.OperandDef;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.execution.Variable;

/**
 * A backtrace represents a stack of call-like frames from the root of
 * an evaluation to a particular sub-expression, commonly a
 * sub-expression in which an exception was thrown.
 */
public class Backtrace {

    public static class Frame {

        private final Expression expression;

        public Frame(final Expression expression) {
            this.expression = expression;
        }

        /**
         * Return the expression that was being evaluated when the
         * backtrace frame was captured.
         *
         * @return The expression.
         */
        public Expression getExpression() {
            return this.expression;
        }
    }

    public static class FunctionoidFrame extends Frame {

        private final ExpressionDef definition;

        private final List<Variable> arguments;

        private final List<Variable> localVariables;

        private final String contextName;

        private final Object contextValue;

        public FunctionoidFrame(
                final Expression expression,
                final ExpressionDef definition,
                final List<Variable> arguments,
                final List<Variable> localVariables,
                final String contextName,
                final Object contextValue) {
            super(expression);
            this.definition = definition;
            this.arguments = arguments;
            this.localVariables = localVariables;
            this.contextName = contextName;
            this.contextValue = contextValue;
        }

        /**
         * Return the definition in which the current expression was
         * being evaluated when the backtrace frame was capture.
         * <p>
         * Definitions are either direct instances of ExpressionDef
         * which correspond to define foo: ... in CQL libraries or
         * instances of FunctionDef which correspond to define
         * function foo(...): ... in CQL libraries. In either case,
         * getExpression() returns the sub-expression within the
         * definition expression that was being evaluated.
         *
         * @return The containing definition expression.
         */
        public ExpressionDef getDefinition() {
            return this.definition;
        }

        /**
         * Return the arguments with which the FunctionDef expression
         * was invoked.
         * <p>
         * Returns an empty list if the surrounding expression was an
         * ExpressionDef but not a FunctionDef.
         *
         * @return A list of the variables that correspond to function
         *         call arguments.
         */
        public List<Variable> getArguments() {
            return this.arguments;
        }

        public List<Variable> getLocalVariables() {
            return this.localVariables;
        }

        public String getContextName() {
            return this.contextName;
        }

        public Object getContextValue() {
            return this.contextValue;
        }
    }

    private final List<Frame> frames = new LinkedList<>();

    public List<Frame> getFrames() {
        return this.frames;
    }

    public void addFrame(final Frame frame) {
        this.frames.add(frame);
    }

    public void maybeAddFrame(
            final ExpressionDef containingDefinition,
            final State.ActivationFrame definitionFrame,
            final Deque<State.ActivationFrame> stack,
            final String contextName,
            final Object contextValue,
            final Expression expression) {
        // TODO(jmoringe): explain this
        if (!frames.isEmpty()) {
            final var currentFrame = this.frames.get(frames.size() - 1);
            if (currentFrame instanceof FunctionoidFrame) {
                if (((FunctionoidFrame) currentFrame).getDefinition() == containingDefinition) {
                    return;
                }
            }
        }
        List<Variable> arguments = new LinkedList<>();
        List<Variable> localVariables = new LinkedList<>();
        for (State.ActivationFrame frame : stack) {
            if (frame == definitionFrame) {
                final Set<String> parameterNames;
                if (containingDefinition instanceof FunctionDef) {
                    parameterNames = ((FunctionDef) containingDefinition)
                            .getOperand().stream().map(OperandDef::getName).collect(Collectors.toSet());
                } else {
                    parameterNames = new HashSet<>();
                }
                frame.variables.forEach(variable -> {
                    if (parameterNames.contains(variable.getName())) {
                        arguments.add(variable);
                    } else {
                        localVariables.add(variable);
                    }
                });
                break;
            } else {
                localVariables.addAll(frame.variables);
            }
        }
        addFrame(new FunctionoidFrame(expression,
                containingDefinition,
                arguments,
                localVariables,
                contextName,
                contextValue));
    }
}
