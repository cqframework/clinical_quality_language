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

        public FunctionoidFrame(
                final Expression expression,
                final ExpressionDef definition,
                final List<Variable> arguments,
                final List<Variable> localVariables) {
            super(expression);
            this.definition = definition;
            this.arguments = arguments;
            this.localVariables = localVariables;
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
            final Expression expression) {
        // When the EvaluationVisitor unwinds through
        // EvaluationVisitor.visitExpression calls, every call has a
        // unique expression that is being evaluated but not every
        // call has a unique ActivationFrame since each
        // ActivationFrame is associated with the respective innermost
        // expression definition or function definition enclosing the
        // expression which is being evaluated in the call. For that
        // reason, this method is called for multiple expressions with
        // the same surrounding ActivationFrame. If the frame at the
        // end of our frame list already describes the definition or
        // function that surrounds the expression for which the
        // visitExpression is being unwound, everything has already
        // been recorded: the innermost expression, the containing
        // definition or function and all local variables and
        // arguments.
        if (!frames.isEmpty()) {
            final var currentFrame = this.frames.get(frames.size() - 1);
            if (currentFrame instanceof FunctionoidFrame) {
                if (((FunctionoidFrame) currentFrame).getDefinition() == containingDefinition) {
                    return;
                }
            }
        }
        // Walk the ActivationFrames from the top of the stack to the
        // innermost ActivationFrame which corresponds to and
        // expression or function definition which is definitionFrame.
        // Variable bindings in frames closer to the top of the stack
        // than definitionFrame correspond to local variables which
        // where established in the scope of the top-level expression
        // or function.  Each variable binding in definitionFrame
        // itself corresponds to either an invocation argument, if the
        // frame represents a function invocation, or more local
        // variables.  Classify all relevant binding into those two
        // categories, mainly for more informative presentation in
        // backtraces.
        final List<Variable> arguments = new LinkedList<>();
        final List<Variable> localVariables = new LinkedList<>();
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
        addFrame(new FunctionoidFrame(expression, containingDefinition, arguments, localVariables));
    }
}
