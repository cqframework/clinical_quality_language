package org.opencds.cqf.cql.engine.execution

import kotlin.text.StringBuilder
import kotlinx.io.Sink
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.Retrieve
import org.opencds.cqf.cql.engine.execution.State.ActivationFrame
import org.opencds.cqf.cql.engine.util.createIdentityHashMap

/**
 * A profile is a tree such that paths through the tree correspond to chains of invocations (or in
 * other words snapshots of the engine stack) that occurred during evaluation.
 *
 * Individual nodes thus correspond to expression or function definitions being called in a
 * particular context. Nodes track the number of invocations, the current CQL context, the time
 * spent evaluating and the cache hit ratio (if applicable) in that context. A profile is built by
 * creating and updating nodes whenever the engine finishes the evaluation of a relevant element.
 *
 * Profiles for multiple evaluations can be merged into a combined profile, for example to represent
 * the performance characteristics of use-cases that consists of multiple expression evaluations or
 * to form a combined picture of evaluations that were performed concurrently in multiple engine
 * instances.
 *
 * This class provides the [render][.render] methods to render a profile as a simple
 * [flamegraph](https://en.wiktionary.org/wiki/flamegraph).
 */
@Suppress("MagicNumber")
class Profile
    /**
     * Creates a new, empty Profile.
     *
     * Use methods for populating the profile.
     *
     * @see .enter Entering an activation frame for incremental profile construction
     * @see .leave Leaving an activation frame for incremental profile construction
     * @see .register Adding a snapshop of the engine stack to the profile
     */
{
    /**
     * Represents all invocations of a particular CQL expression in a specific static context.
     *
     * The static context consists of the chain of expression or function evaluations and the
     * respective current CQL context that lead to the evaluation of the expression of the node.
     * Another way to describe the static context is that it could be formed by aggregating call
     * stacks in a way that disregards the concrete arguments of function calls but retains the
     * current CQL context of each evaluation.
     *
     * As a result, the parent of a node is a node that corresponds to the function or expression
     * that called or used the expression of the node in question. The children of a node correspond
     * to functions or expressions that are called or used by the expression of the node in
     * question.
     *
     * The information associated with these invocations is
     * * The CQL expression
     * * The name of the CQL context, but not its value
     * * The number of invocations
     * * The cumulative real time spent in all invocations
     * * The number of cache misses, if the expression is eligible for caching
     *
     * @author Jan Moringen
     */
    class Node(var expression: Element?, var context: String?) {
        var children = mutableMapOf<String?, MutableMap<Element?, Node>?>()
        var count: Long = 0
        var time: Long = 0
        var misses: Long = 0

        fun addInvocation(startTime: Long, endTime: Long, isHit: Boolean) {
            val elapsed = endTime - startTime
            check(elapsed >= 0)
            this.count += 1
            this.time += elapsed
            this.misses += (if (isHit) 0 else 1).toLong()
        }

        fun ensureChild(expression: Element?, context: String?): Node {
            return this.children
                .getOrPut(context) { createIdentityHashMap() }!!
                .getOrPut(expression) { Node(expression, context) }
        }

        fun register(stack: MutableCollection<ActivationFrame>): Node? {
            val frames = stack.toMutableList()
            return registerStep(frames, frames.size - 1)
        }

        private fun registerStep(stack: MutableList<ActivationFrame>, index: Int): Node? {
            if (index == -1) {
                val frame = stack[0]
                addInvocation(frame.startTime, frame.endTime, frame.isCached)
                return this
            } else {
                val frame = stack[index]
                val expression = frame.element
                val contextName = frame.contextName
                val child = if (expression != null) ensureChild(expression, contextName) else this
                return child.registerStep(stack, index - 1)
            }
        }

        fun merge(other: Node): Node {
            this.count += other.count
            this.time += other.time
            this.misses += other.misses
            other.children.forEach { (context, mapForContext) ->
                mapForContext!!.forEach { (element: Element?, otherChild: Node?) ->
                    val child = ensureChild(element, context)
                    child.merge(otherChild!!)
                }
            }
            return this
        }

        fun render(sink: Sink) {
            val duration = this.time / 1000000000.0
            val maxDepth = 10 // FIXME(jmoringe): compute
            val scaleX = 4000.0 / duration
            val scaleY = (10.0 * 80.0) / maxDepth
            renderStep(sink, 0, 0.0, 0, scaleX, scaleY)
        }

        fun renderStep(
            sink: Sink,
            i: Int,
            x: Double,
            depth: Int,
            scaleX: Double,
            scaleY: Double,
        ): Double {
            var i = i
            val timeInSeconds = this.time / 1000000000.0
            val x1 = scaleX * x
            val x2 = scaleX * (x + timeInSeconds)
            val y1 = scaleY * depth
            val y2 = scaleY * (depth + 1)
            val red = (50 * depth) % 256
            val green = (50 * i) % 256
            val blue = if (this.expression is FunctionDef) 128 else 0
            val color =
                "#${(red).toString(16).padStart(2, '0')}${(green).toString(16).padStart(2, '0')}${(blue).toString(16).padStart(2, '0')}"
            val idString = "${depth}-${x}"
            val rectString =
                "  <rect x=\"${x1}\" y=\"${y1}\" width=\"${x2 - x1}\" height=\"${y2 - y1}\" style=\"fill:${color};stroke:${color};fill-opacity:0.5;\"/>\n"
            sink.writeString(rectString)
            sink.writeString(
                "    <defs>\n      <clipPath id=\"${idString}\">${rectString}</clipPath>\n    </defs>\n"
            )
            sink.writeString(
                "    <text x=\"${x1}\" y=\"${y1 + (y2 - y1) * .15}\" clip-path=\"url(#${idString})\"><tspan>${expressionLabel(this.expression)}</tspan></text>\n"
            )
            sink.writeString(
                "    <text x=\"${x1}\" y=\"${y1 + (y2 - y1) * .35}\" clip-path=\"url(#${idString})\"><tspan>${this.time / 1000000} ms</tspan></text>\n"
            )
            sink.writeString(
                "    <text x=\"${x1}\" y=\"${y1 + (y2 - y1) * .55}\" clip-path=\"url(#${idString})\"><tspan>${this.count} Calls</tspan></text>\n"
            )
            sink.writeString(
                "    <text x=\"${x1}\" y=\"${y1 + (y2 - y1) * .75}\" clip-path=\"url(#${idString})\"><tspan>${this.misses} Misses</tspan></text>\n"
            )

            val sorted =
                this.children.values
                    .flatMap { contextMap -> contextMap!!.values }
                    .sortedBy { node -> -node.time }
            var xc = x
            for (child in sorted) {
                xc = child.renderStep(sink, i++, xc, depth + 1, scaleX, scaleY)
            }
            return x + timeInSeconds
        }

        private fun expressionLabel(expression: Element?): String {
            val result = StringBuilder()
            if (expression == null) {
                result.append("«root»")
            } else if (expression is Retrieve) {
                result.append("[${expression.dataType!!.getLocalPart()}]")
            } else if (expression is ExpressionDef) {
                result.append(expression.name)
                if (expression is FunctionDef) {
                    result.append("()")
                }
            } else {
                result.append("unknown expression type")
            }
            if (this.context != null) {
                result.append(" (")
                result.append(this.context)
                result.append(")")
            }
            return result.toString()
        }

        override fun toString(): String {
            return "Node{expression=${expressionLabel(this.expression)}, context=${this.context}, count=${this.count}, time=${this.time} ms, misses=${this.misses}}"
        }
    }

    /**
     * Returns the tree of [Node]s of this profile.
     *
     * @return The root [Node] of this profile. Note that the root node is not associated with a
     *   particular expression or context; Both are `null`. The direct children of the root node
     *   correspond to top-level function calls or expression evaluations.
     */
    val tree: Node = Node(null, null)

    // Tracks the stack of nodes which correspond to currently active
    // ActivationFrames for incremental profile construction.
    private val stack = ArrayDeque<Node>()

    /**
     * Records in the profile the fact that the evaluator has entered the supplied activation frame.
     *
     * The activation frame must be fully populated in terms of element being evaluated, context,
     * variables and evaluation start time. The end time has to be omitted, of course, since it
     * becomes available when the evaluator leaves the activation frame. Each call of this method
     * must be followed by a [.leave] call for the same activation frame.
     *
     * @param frame An activation frame that the evaluator has just entered or is about to enter.
     */
    fun enter(frame: ActivationFrame) {
        val topNode = this.stack.firstOrNull()
        val newNode: Node
        if (topNode == null) {
            check(frame.element == null)
            check(frame.contextName == null)
            newNode = this.tree
        } else {
            newNode = topNode.ensureChild(frame.element, frame.contextName)
        }
        this.stack.addFirst(newNode)
    }

    /**
     * Records in the profile the fact that the evaluator has left the supplied activation frame.
     *
     * The activation frame must be fully populated in terms of element being evaluated, context,
     * variables and evaluation start and end time. For each call of this method, there must be a
     * preceding [.enter] call for the same activation frame.
     *
     * @param frame An activation frame that the evaluator has just left or is about to leave.
     */
    fun leave(frame: ActivationFrame) {
        val topNode = this.stack.first()
        check(topNode.expression === frame.element)
        check(topNode.context == frame.contextName)
        topNode.addInvocation(frame.startTime, frame.endTime, frame.isCached)
        this.stack.removeFirst()
    }

    /**
     * Integrates a complete engine stack snapshot into the profile.
     *
     * @see .enter Entering an activation frame for incremental profile construction
     * @see .leave Leaving an activation frame for incremental profile construction
     */
    fun register(stack: MutableCollection<ActivationFrame>) {
        this.tree.register(stack)
    }

    /**
     * Merges information from the other profile into this profile and returns the modified profile.
     *
     * @param other The other profile.
     * @return This profile with the added information.
     */
    fun merge(other: Profile): Profile {
        this.tree.merge(other.tree)
        return this
    }

    /**
     * Renders a flamegraph-SVG representation of the profile to the provided writer.
     *
     * @param writer A writer to which the flamegraph-SVG representation will be written. This
     *   method does not flush or close the writer.
     * @throws IOException When writing to the writer throws an Exception.
     */
    fun render(writer: Sink) {
        writer.writeString("<svg>\n")
        this.tree.render(writer)
        writer.writeString("</svg>\n")
    }

    /**
     * Tries to create the designated file and write a flamegraph-SVG representation of the profile
     * to it.
     *
     * @param outputFile The file to which the flamegraph-SVG representation should be written.
     */
    fun render(outputFile: Path) {
        SystemFileSystem.sink(outputFile).buffered().use { sink -> render(sink) }
    }

    companion object {
        /**
         * Produces a new profile by merging the information in the provided collection of profiles.
         *
         * @param profiles A collection of profiles that should be merged.
         * @return A new profile that contains all information from the provided collection of
         *   profiles.
         */
        fun merge(profiles: MutableCollection<Profile?>): Profile {
            val result = Profile()
            profiles.forEach { other -> result.merge(other!!) }
            return result
        }
    }
}
