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
 *
 * @author Jan Moringen
 */
public class Backtrace {

    /**
     * Represents the evaluation of a CQL expression.
     *
     * @see FunctionoidFrame Subclass for functions and expression
     *                       definitions.
     */
    public static class Frame {

        private final Expression expression;

        public Frame(final Expression expression) {
            this.expression = expression;
        }

        /**
         * Returns the expression that was being evaluated when the
         * backtrace frame was captured.
         *
         * @return The expression.
         */
        public Expression getExpression() {
            return this.expression;
        }
    }

    /**
     * Instances of this subclass of {@link Frame} represent the
     * invocation of a function with specific arguments or the
     * evaluation of an expression definition.
     * <p>
     * In addition to the (sub)expression being evaluated, this class
     * captures the surrounding function or expression definition,
     * function arguments, local variables as well as the name and
     * value of the CQL context.
     */
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
         * Returns the definition in which the current expression was
         * being evaluated when the backtrace frame was captured.
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
         * Returns the arguments with which the {@link FunctionDef}
         * expression was invoked.
         * <p>
         * Returns an empty list if the surrounding expression was an
         * {@link ExpressionDef} but not a FunctionDef.
         *
         * @return A list of the {@link Variable}s that correspond to
         *         function call arguments.
         */
        public List<Variable> getArguments() {
            return this.arguments;
        }

        /**
         * Returns the local variable bindings in the scope of
         * expression evaluation of the frame.
         *
         * @return A list of the {@link Variable}s that correspond to
         *         local variable bindings.
         */
        public List<Variable> getLocalVariables() {
            return this.localVariables;
        }

        /**
         * Returns the name of the CQL context that was current during
         * the evaluation represented by the frame.
         *
         * @return The name of the CQL context.
         */
        public String getContextName() {
            return this.contextName;
        }

        /**
         * Returns the value of the CQL context that was current
         * during the evaluation represented by the frame.
         *
         * @return The value of the CQL context.
         */
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
        // innermost ActivationFrame which corresponds to an
        // expression or function definition which is definitionFrame.
        // Variable bindings in frames closer to the top of the stack
        // than definitionFrame correspond to local variables which
        // where established in the scope of the top-level expression
        // or function.  Each variable binding in definitionFrame
        // itself corresponds to either an invocation argument, if the
        // frame represents a function invocation, or more local
        // variables.  Classify all relevant bindings into those two
        // categories, mainly for more informative presentation in
        // backtraces.
        final List<Variable> arguments = new LinkedList<>();
        final List<Variable> localVariables = new LinkedList<>();
        for (State.ActivationFrame frame : stack) {
            if (frame == definitionFrame) {
                final List<String> parameterNames;
                if (containingDefinition instanceof FunctionDef) {
                    parameterNames = ((FunctionDef) containingDefinition)
                            .getOperand().stream().map(OperandDef::getName).collect(Collectors.toList());
                } else {
                    parameterNames = List.of();
                }
                frame.variables.forEach(variable -> {
                    if (parameterNames.contains(variable.getName())) {
                        arguments.add(variable);
                    } else {
                        localVariables.add(variable);
                    }
                });
                arguments.sort(Comparator.comparing(argument -> parameterNames.indexOf(argument.getName())));
                break;
            } else {
                localVariables.addAll(frame.variables);
            }
        }
        addFrame(new FunctionoidFrame(
                expression, containingDefinition, arguments, localVariables, contextName, contextValue));
    }
}
