package org.opencds.cqf.cql.engine.exception

import java.util.Deque
import java.util.LinkedList
import java.util.function.Consumer
import java.util.function.Function
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.OperandDef
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.execution.State.ActivationFrame
import org.opencds.cqf.cql.engine.execution.Variable

/**
 * A backtrace represents a stack of call-like frames from the root of an evaluation to a particular
 * sub-expression, commonly a sub-expression in which an exception was thrown.
 *
 * @author Jan Moringen
 */
class Backtrace {
    /**
     * Represents the evaluation of a CQL expression.
     *
     * @see FunctionoidFrame Subclass for functions and expression definitions.
     */
    open class Frame(
        /**
         * Returns the expression that was being evaluated when the backtrace frame was captured.
         *
         * @return The expression.
         */
        val expression: Expression?
    )

    /**
     * Instances of this subclass of [Frame] represent the invocation of a function with specific
     * arguments or the evaluation of an expression definition.
     *
     * In addition to the (sub)expression being evaluated, this class captures the surrounding
     * function or expression definition, function arguments, local variables as well as the name
     * and value of the CQL context.
     */
    class FunctionoidFrame(
        expression: Expression?,
        /**
         * Returns the definition in which the current expression was being evaluated when the
         * backtrace frame was captured.
         *
         * Definitions are either direct instances of ExpressionDef which correspond to define foo:
         * ... in CQL libraries or instances of FunctionDef which correspond to define function
         * foo(...): ... in CQL libraries. In either case, getExpression() returns the
         * sub-expression within the definition expression that was being evaluated.
         *
         * @return The containing definition expression.
         */
        val definition: ExpressionDef?,
        /**
         * Returns the arguments with which the [FunctionDef] expression was invoked.
         *
         * Returns an empty list if the surrounding expression was an [ExpressionDef] but not a
         * FunctionDef.
         *
         * @return A list of the [Variable]s that correspond to function call arguments.
         */
        val arguments: MutableList<Variable?>?,
        /**
         * Returns the local variable bindings in the scope of expression evaluation of the frame.
         *
         * @return A list of the [Variable]s that correspond to local variable bindings.
         */
        val localVariables: MutableList<Variable?>?,
        /**
         * Returns the name of the Library that was current during the evaluation represented by the
         * frame.
         *
         * @return The name of the Library in context.
         */
        val libraryIdentifier: VersionedIdentifier?,
        /**
         * Returns the name of the CQL context that was current during the evaluation represented by
         * the frame.
         *
         * @return The name of the CQL context.
         */
        val contextName: String?,
        /**
         * Returns the value of the CQL context that was current during the evaluation represented
         * by the frame.
         *
         * @return The value of the CQL context.
         */
        val contextValue: Any?,
    ) : Frame(expression)

    val frames: MutableList<Frame?> = LinkedList<Frame?>()

    fun addFrame(frame: Frame?) {
        this.frames.add(frame)
    }

    fun maybeAddFrame(
        containingDefinition: ExpressionDef?,
        definitionFrame: ActivationFrame?,
        stack: Deque<ActivationFrame>,
        contextName: String?,
        contextValue: Any?,
        libraryIdentifier: VersionedIdentifier?,
        expression: Expression?,
    ) {
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
            val currentFrame = this.frames.get(frames.size - 1)
            if (currentFrame is FunctionoidFrame) {
                if (currentFrame.definition === containingDefinition) {
                    return
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
        val arguments = mutableListOf<Variable?>()
        val localVariables = mutableListOf<Variable?>()
        for (frame in stack) {
            if (frame === definitionFrame) {
                val parameterNames: List<String?>
                if (containingDefinition is FunctionDef) {
                    parameterNames = containingDefinition.operand.map(OperandDef::name)
                } else {
                    parameterNames = listOf()
                }
                frame.variables.forEach(
                    Consumer { variable ->
                        if (parameterNames.contains(variable!!.name)) {
                            arguments.add(variable)
                        } else {
                            localVariables.add(variable)
                        }
                    }
                )
                arguments.sortWith(
                    Comparator.comparing(
                        Function { argument -> parameterNames.indexOf(argument!!.name) }
                    )
                )
                break
            } else {
                localVariables.addAll(frame.variables)
            }
        }
        addFrame(
            FunctionoidFrame(
                expression,
                containingDefinition,
                arguments,
                localVariables,
                libraryIdentifier,
                contextName,
                contextValue,
            )
        )
    }
}
