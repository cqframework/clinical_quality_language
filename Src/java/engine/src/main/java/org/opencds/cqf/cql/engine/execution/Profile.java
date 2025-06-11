package org.opencds.cqf.cql.engine.execution;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.Retrieve;

/**
 * A profile is a tree such that paths through the tree correspond to
 * chains of invocations (or in other words snapshots of the engine
 * stack) that occurred during evaluation.
 * <p>
 * Individual nodes thus correspond to expression or function
 * definitions being called in a particular context. Nodes track the
 * number of invocations, the current CQL context, the time spent
 * evaluating and the cache hit ratio (if applicable) in that
 * context. A profile is built by creating and updating nodes whenever
 * the engine finishes the evaluation of a relevant element.
 * <p>
 * Profiles for multiple evaluations can be merged into a combined
 * profile, for example to represent the performance characteristics
 * of use-cases that consists of multiple expression evaluations or to
 * form a combined picture of evaluations that were performed
 * concurrently in multiple engine instances.
 * <p>
 * This class provides the {@link #render render} methods to render a
 * profile as a simple
 * <a href="https://en.wiktionary.org/wiki/flamegraph">flamegraph</a>.
 */
public class Profile {

    /**
     * Represents all invocations of a particular CQL expression in a
     * specific static context.
     * <p>
     * The static context consists of the chain of expression or
     * function evaluations and the respective current CQL context
     * that lead to the evaluation of the expression of the
     * node. Another way to describe the static context is that it
     * could be formed by aggregating call stacks in a way that
     * disregards the concrete arguments of function calls but retains
     * the current CQL context of each evaluation.
     * <p>
     * As a result, the parent of a node is a node that corresponds to
     * the function or expression that called or used the expression
     * of the node in question. The children of a node correspond to
     * functions or expressions that are called or used by the
     * expression of the node in question.
     * <p>
     * The information associated with these invocations is
     * <ul>
     *     <li>The CQL expression</li>
     *     <li>The name of the CQL context, but not its value</li>
     *     <li>The number of invocations</li>
     *     <li>The cumulative real time spent in all invocations</li>
     *     <li>The number of cache misses, if the expression is eligible for caching</li>
     * </ul>
     *
     * @author Jan Moringen
     */
    public static class Node {
        public Map<String, IdentityHashMap<Element, Node>> children = new HashMap<>();
        public Element expression;
        public String context;
        public long count = 0;
        public long time = 0;
        public long misses = 0;

        public Node(final Element expression, final String context) {
            this.expression = expression;
            this.context = context;
        }

        public void addInvocation(long startTime, long endTime, boolean isHit) {
            final var elapsed = endTime - startTime;
            assert elapsed >= 0;
            this.count += 1;
            this.time += elapsed;
            this.misses += isHit ? 0 : 1;
        }

        public Node ensureChild(final Element expression, final String context) {
            return this.children
                    .computeIfAbsent(context, context2 -> new IdentityHashMap<>())
                    .computeIfAbsent(expression, expression2 -> new Node(expression, context));
        }

        public Node register(final Collection<State.ActivationFrame> stack) {
            final var frames = new ArrayList<>(stack);
            return registerStep(frames, frames.size() - 1);
        }

        private Node registerStep(final List<State.ActivationFrame> stack, int index) {
            if (index == -1) {
                final var frame = stack.get(0);
                addInvocation(frame.startTime, frame.endTime, frame.isCached);
                return this;
            } else {
                final var frame = stack.get(index);
                final var expression = frame.element;
                final var contextName = frame.contextName;
                final var child = expression != null ? ensureChild(expression, contextName) : this;
                return child.registerStep(stack, index - 1);
            }
        }

        public Node merge(final Node other) {
            this.count += other.count;
            this.time += other.time;
            this.misses += other.misses;
            other.children.forEach((context, mapForContext) -> mapForContext.forEach((element, otherChild) -> {
                final var child = ensureChild(element, context);
                child.merge(otherChild);
            }));
            return this;
        }

        public void render(final Writer writer) throws IOException {
            final var duration = this.time / 1_000_000_000.0;
            final var maxDepth = 10; // FIXME(jmoringe): compute
            final var scaleX = 4000.0 / duration;
            final var scaleY = (10.0 * 80.0) / maxDepth;
            renderStep(writer, 0, 0, 0, scaleX, scaleY);
        }

        public double renderStep(final Writer writer, int i, double x, int depth, double scaleX, double scaleY)
                throws IOException {
            final var timeInSeconds = this.time / 1_000_000_000.0;
            final var x1 = scaleX * x;
            final var x2 = scaleX * (x + timeInSeconds);
            final var y1 = scaleY * depth;
            final var y2 = scaleY * (depth + 1);
            final var red = (50 * depth) % 256;
            final var green = (50 * i) % 256;
            final var blue = this.expression instanceof FunctionDef ? 128 : 0;
            final var color = String.format("#%02x%02x%02x", red, green, blue);
            final var idString = String.format("%d-%f", depth, x);
            final var rectString = String.format(
                    "  <rect x=\"%f\" y=\"%f\" width=\"%f\" height=\"%f\" style=\"fill:%s;stroke:%s;fill-opacity:0.5;\"/>\n",
                    x1, y1, x2 - x1, y2 - y1, color, color);
            writer.append(rectString);
            writer.append(String.format(
                    "    <defs>\n      <clipPath id=\"%s\">%s</clipPath>\n    </defs>\n", idString, rectString));
            writer.append(String.format(
                    "    <text x=\"%f\" y=\"%f\" clip-path=\"url(#%s)\"><tspan>%s</tspan></text>\n",
                    x1, y1 + (y2 - y1) * .15, idString, expressionLabel(this.expression)));
            writer.append(String.format(
                    "    <text x=\"%f\" y=\"%f\" clip-path=\"url(#%s)\"><tspan>%,3d ms</tspan></text>\n",
                    x1, y1 + (y2 - y1) * .35, idString, this.time / 1_000_000));
            writer.append(String.format(
                    "    <text x=\"%f\" y=\"%f\" clip-path=\"url(#%s)\"><tspan>%,3d Calls</tspan></text>\n",
                    x1, y1 + (y2 - y1) * .55, idString, this.count));
            writer.append(String.format(
                    "    <text x=\"%f\" y=\"%f\" clip-path=\"url(#%s)\"><tspan>%,3d Misses</tspan></text>\n",
                    x1, y1 + (y2 - y1) * .75, idString, this.misses));

            final var sorted = this.children.values().stream()
                    .flatMap(contextMap -> contextMap.values().stream())
                    .sorted(Comparator.comparing(node -> -node.time))
                    .collect(Collectors.toList());
            var xc = x;
            for (var child : sorted) {
                xc = child.renderStep(writer, i++, xc, depth + 1, scaleX, scaleY);
            }
            return x + timeInSeconds;
        }

        private String expressionLabel(final Element expression) {
            final var result = new StringBuilder();
            if (expression == null) {
                result.append("«root»");
            } else if (expression instanceof Retrieve) {
                result.append(String.format(
                        "[%s]", ((Retrieve) expression).getDataType().getLocalPart()));
            } else if (expression instanceof ExpressionDef) {
                result.append(((ExpressionDef) expression).getName());
                if (expression instanceof FunctionDef) {
                    result.append("()");
                }
            } else {
                result.append("unknown expression type");
            }
            if (this.context != null) {
                result.append(" (");
                result.append(this.context);
                result.append(")");
            }
            return result.toString();
        }

        @Override
        public String toString() {
            return String.format(
                    "Node{expression=%s, context=%s, count=%d, time=%s ms, misses=%d}",
                    expressionLabel(this.expression), this.context, this.count, this.time, this.misses);
        }
    }

    private final Node tree = new Node(null, null);

    // Tracks the stack of nodes which correspond to currently active
    // ActivationFrames for incremental profile construction.
    private final Deque<Node> stack = new ArrayDeque<>();

    /**
     * Creates a new, empty Profile.
     * <p>
     * Use methods for populating the profile.
     *
     * @see #enter Entering an activation frame for incremental profile construction
     * @see #leave Leaving an activation frame for incremental profile construction
     * @see #register Adding a snapshop of the engine stack to the profile
     */
    public Profile() {}

    /**
     * Returns the tree of {@link Node}s of this profile.
     *
     * @return The root {@link Node} of this profile. Note that the
     *         root node is not associated with a particular
     *         expression or context; Both are {@code null}. The
     *         direct children of the root node correspond to
     *         top-level function calls or expression evaluations.
     */
    public Node getTree() {
        return this.tree;
    }

    /**
     * Records in the profile the fact that the evaluator has entered the supplied activation frame.
     * <p>
     * The activation frame must be fully populated in terms of element being evaluated, context, variables and evaluation start time. The end time has to be omitted, of course, since it becomes available when the evaluator leaves the activation frame.
     * Each call of this method must be followed by a {@link #leave} call for the same activation frame.
     *
     * @param frame An activation frame that the evaluator has just entered or is about to enter.
     */
    public void enter(final State.ActivationFrame frame) {
        final var topNode = this.stack.peek();
        final Node newNode;
        if (topNode == null) {
            assert frame.element == null;
            assert frame.contextName == null;
            newNode = this.tree;
        } else {
            newNode = topNode.ensureChild(frame.element, frame.contextName);
        }
        this.stack.push(newNode);
    }

    /**
     * Records in the profile the fact that the evaluator has left the
     * supplied activation frame.
     * <p>
     * The activation frame must be fully populated in terms of
     * element being evaluated, context, variables and evaluation
     * start and end time.  For each call of this method, there must
     * be a preceding {@link #enter} call for the same activation
     * frame.
     *
     * @param frame An activation frame that the evaluator has just
     *              left or is about to leave.
     */
    public void leave(final State.ActivationFrame frame) {
        final var topNode = this.stack.peek();
        assert topNode != null;
        assert topNode.expression == frame.element;
        assert Objects.equals(topNode.context, frame.contextName);
        topNode.addInvocation(frame.startTime, frame.endTime, frame.isCached);
        this.stack.pop();
    }

    /**
     * Integrates a complete engine stack snapshot into the profile.
     *
     * @see #enter Entering an activation frame for incremental profile construction
     * @see #leave Leaving an activation frame for incremental profile construction
     */
    public void register(final Collection<State.ActivationFrame> stack) {
        this.tree.register(stack);
    }

    /**
     * Merges information from the other profile into this profile and
     * returns the modified profile.
     *
     * @param other The other profile.
     * @return This profile with the added information.
     */
    public Profile merge(final Profile other) {
        this.tree.merge(other.tree);
        return this;
    }

    /**
     * Produces a new profile by merging the information in the
     * provided collection of profiles.
     *
     * @param profiles A collection of profiles that should be merged.
     * @return A new profile that contains all information from the
     *         provided collection of profiles.
     */
    public static Profile merge(final Collection<Profile> profiles) {
        final var result = new Profile();
        profiles.forEach(result::merge);
        return result;
    }

    /**
     * Renders a flamegraph-SVG representation of the profile to the
     * provided writer.
     *
     * @param writer A writer to which the flamegraph-SVG
     *               representation will be written. This method does
     *               not flush or close the writer.
     * @throws IOException When writing to the writer throws an Exception.
     */
    public void render(final Writer writer) throws IOException {
        writer.append("<svg>\n");
        this.tree.render(writer);
        writer.append("</svg>\n");
    }

    /**
     * Renders a flamegraph-SVG representation of the profile to the
     * provided stream.
     *
     * @param outputStream A stream to which the flamegraph-SVG
     *                     representation will be written. This method
     *                     does not flush or close the stream.
     * @throws IOException When writing to the stream throws an Exception.
     */
    public void render(final OutputStream outputStream) throws IOException {
        try (var writer = new OutputStreamWriter(outputStream)) {
            render(writer);
        }
    }

    /**
     * Tries to create the designated file and write a flamegraph-SVG
     * representation of the profile to it.
     *
     * @param outputFile The file to which the flamegraph-SVG
     *                   representation should be written.
     * @throws IOException If opening the file or writing to it throws
     *                     an exception.
     */
    public void render(final Path outputFile) throws IOException {
        try (var stream = new FileOutputStream(outputFile.toFile())) {
            render(stream);
        }
    }
}
