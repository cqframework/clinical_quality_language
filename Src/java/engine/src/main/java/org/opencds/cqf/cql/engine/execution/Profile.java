package org.opencds.cqf.cql.engine.execution;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Retrieve;

/**
 * A profile is a tree such that paths through the tree correspond to
 * chains of invocations (or in other words snapshots of the engine
 * stack) that occurred during evaluation.
 * <p>
 * Individual nodes thus correspond to expression or function
 * definitions being called in a particular context. Nodes track the
 * number of invocations, the time spent evaluating and the cache hit
 * ratio (if applicable) in that context. A profile is built by
 * creating and updating nodes whenever the engine finishes the
 * evaluation of a relevant element.
 * <p>
 * Profiles for multiple evaluations can be merged into a combined
 * profile, for example to represent the performance characteristics
 * of use-cases that consists of multiple expression evaluation or the
 * form a combined picture of evaluations that were performed
 * concurrently in multiple engine instances.
 * <p>
 * This class provides the {@link #render render} methods to render a
 * profile as a simple
 * <a href="https://en.wiktionary.org/wiki/flamegraph">flamegraph</a>.
 */
public class Profile {
    public static class Key {
        public final Element element;
        public final String context;

        public Key(final Element element, final String context) {
            this.element = element;
            this.context = context;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equals(element, key.element) && Objects.equals(context, key.context);
        }

        @Override
        public int hashCode() {
            return Objects.hash(element, context);
        }
    }

    public static class Node {
        public Map<Key, Node> children = new HashMap<>();
        public Element expression;
        public String context;
        public long count = 0;
        public long time = 0;
        private long misses = 0;

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
            final var key = new Key(expression, context);
            return this.children.computeIfAbsent(key, (key2) -> new Node(key2.element, key2.context));
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
            other.children.forEach((key, otherChild) -> {
                final var child = ensureChild(key.element, key.context);
                child.merge(otherChild);
            });
            return this;
        }

        public void render(final Writer writer) throws IOException {
            final var duration = this.time / 1_000_000_000.0;
            final var maxDepth = 10; // FIXME(jmoringe): compute
            final var scaleX = 4000.0 / duration;
            final var scaleY = (10.0 * 80.0) / maxDepth;
            renderStep(writer, 0, 0, scaleX, scaleY);
        }

        public double renderStep(final Writer writer, double x, int depth, double scaleX, double scaleY)
                throws IOException {
            final var timeInSeconds = this.time / 1_000_000_000.0;
            final var x1 = scaleX * x;
            final var x2 = scaleX * (x + timeInSeconds);
            final var y1 = scaleY * depth;
            final var y2 = scaleY * (depth + 1);
            final var color = String.format("#%02x%02x00", ((50 * depth) % 256), Math.round(50 * x) % 256);
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
                    .sorted(Comparator.comparing(node -> -node.time))
                    .collect(Collectors.toList());
            var xc = x;
            for (var child : sorted) {
                xc = child.renderStep(writer, xc, depth + 1, scaleX, scaleY);
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
    }

    private final Node tree = new Node(null, null);

    public Profile() {}

    public void register(final Collection<State.ActivationFrame> stack) {
        // TODO(jmoringe): we could be smarter about this: since the
        // evaluator pushes and pop activation frames one at a time,
        // we could remember the current profile node and only go up
        // or down one node at a time like the evaluator.
        this.tree.register(stack);
    }

    public static Profile merge(final Collection<Profile> profiles) {
        final var result = new Profile();
        profiles.forEach(result::merge);
        return result;
    }

    public Profile merge(final Profile other) {
        this.tree.merge(other.tree);
        return this;
    }

    public void render(final Writer writer) throws IOException {
        writer.append("<svg>\n");
        this.tree.render(writer);
        writer.append("</svg>\n");
    }

    public void render(final OutputStream outputStream) throws IOException {
        try (var writer = new OutputStreamWriter(outputStream)) {
            render(writer);
        }
    }

    public void render(final Path outputFile) throws IOException {
        try (var stream = new FileOutputStream(outputFile.toFile())) {
            render(stream);
        }
    }
}
