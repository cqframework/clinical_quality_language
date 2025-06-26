package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hl7.elm.r1.ExpressionDef;
import org.junit.jupiter.api.Test;

public class ProfilingTest extends CqlTestBase {

    private static class NodeMatcher extends TypeSafeMatcher<Profile.Node> {

        private final Matcher<String> contextMatcher;

        private final Matcher<Long> countMatcher;

        private final Matcher<Long> timeMatcher;

        private final Matcher<Long> missesMatcher;

        private final Map<String, Map<String, Matcher<Profile.Node>>> childMatchers;

        public NodeMatcher(
                final Matcher<String> contextMatcher,
                final Matcher<Long> countMatcher,
                final Matcher<Long> timeMatcher,
                final Matcher<Long> missesMatcher,
                final Map<String, Map<String, Matcher<Profile.Node>>> childMatchers) {
            this.contextMatcher = contextMatcher;
            this.countMatcher = countMatcher;
            this.timeMatcher = timeMatcher;
            this.missesMatcher = missesMatcher;
            this.childMatchers = childMatchers;
        }

        public NodeMatcher(
                final Matcher<String> contextMatcher,
                final Matcher<Long> countMatcher,
                final Matcher<Long> timeMatcher,
                final Matcher<Long> missesMatcher) {
            this(contextMatcher, countMatcher, timeMatcher, missesMatcher, Map.of());
        }

        @Override
        protected boolean matchesSafely(Profile.Node node) {
            if (!(this.contextMatcher.matches(node.context)
                    && this.countMatcher.matches(node.count)
                    && this.timeMatcher.matches(node.time)
                    && this.missesMatcher.matches(node.misses))) {
                return false;
            }
            // TODO: should we ensure that there are no extra children in the node?
            for (var entry : this.childMatchers.entrySet()) {
                final String context = entry.getKey();
                final var matchers = entry.getValue();
                final var contextChildren = node.children.get(context);
                if (contextChildren == null) {
                    return false;
                }
                for (final var e : matchers.entrySet()) {
                    final var expressionName = e.getKey();
                    final var matcher = e.getValue();
                    final var childElement = contextChildren.keySet().stream()
                            .filter(expression -> {
                                if (expression instanceof ExpressionDef expressionDef) {
                                    return expressionDef.getName().equals(expressionName);
                                } else {
                                    return false;
                                }
                            })
                            .findFirst();
                    if (childElement.isPresent()) {
                        final var child = contextChildren.get(childElement.get());
                        if (child == null) {
                            return false;
                        }
                        if (!matcher.matches(child)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a node with context ");
            contextMatcher.describeTo(description);
            description.appendText(" and count ");
            countMatcher.describeTo(description);
            description.appendText(" and time ");
            timeMatcher.describeTo(description);
            description.appendText(" and misses ");
            missesMatcher.describeTo(description);
            description.appendText(" and children");
            childMatchers.forEach((contextName, contextMatchers) -> {
                description.appendText(String.format("\n  in context %s", contextName));
                contextMatchers.forEach((expressionName, nodeMatcher) -> {
                    description.appendText(String.format("\n    %s -> ", expressionName));
                    nodeMatcher.describeTo(description);
                });
            });
        }
    }

    private Profile produceProfile() {
        final var environment = new Environment(getLibraryManager());
        final var engine = new CqlEngine(
                environment,
                Set.of(
                        CqlEngine.Options.EnableExpressionCaching,
                        CqlEngine.Options.EnableProfiling)); // TODO: engine options to CqlTestBase.getEngine instead?
        final var result = engine.evaluate("ProfilingTest");
        final var debugResult = result.getDebugResult();
        return debugResult.getProfile();
    }

    @Test
    public void profilingSmoke() {
        final var profile = produceProfile();
        final var tree = profile.getTree();
        assertThat(tree.expression, nullValue());
        assertThat(
                tree,
                new NodeMatcher(
                        nullValue(String.class),
                        equalTo(1L),
                        greaterThanOrEqualTo(0L),
                        equalTo(1L),
                        Map.of(
                                "Unfiltered",
                                Map.of(
                                        "E1",
                                        new NodeMatcher(
                                                equalTo("Unfiltered"),
                                                equalTo(1L),
                                                greaterThanOrEqualTo(0L),
                                                equalTo(1L),
                                                Map.of(
                                                        "Unfiltered",
                                                        Map.of(
                                                                "G",
                                                                new NodeMatcher(
                                                                        equalTo("Unfiltered"),
                                                                        equalTo(1L),
                                                                        greaterThanOrEqualTo(0L),
                                                                        equalTo(1L),
                                                                        Map.of(
                                                                                "Unfiltered",
                                                                                Map.of(
                                                                                        "F",
                                                                                        new NodeMatcher(
                                                                                                equalTo("Unfiltered"),
                                                                                                equalTo(12L),
                                                                                                greaterThanOrEqualTo(
                                                                                                        0L),
                                                                                                equalTo(12L)))))))),
                                        "E2",
                                        new NodeMatcher(
                                                equalTo("Unfiltered"),
                                                equalTo(1L),
                                                greaterThanOrEqualTo(0L),
                                                equalTo(1L),
                                                Map.of(
                                                        "Unfiltered",
                                                        Map.of(
                                                                "G",
                                                                new NodeMatcher(
                                                                        equalTo("Unfiltered"),
                                                                        equalTo(1L),
                                                                        greaterThanOrEqualTo(0L),
                                                                        equalTo(1L),
                                                                        Map.of(
                                                                                "Unfiltered",
                                                                                Map.of(
                                                                                        "F",
                                                                                        new NodeMatcher(
                                                                                                equalTo("Unfiltered"),
                                                                                                equalTo(22L),
                                                                                                greaterThanOrEqualTo(
                                                                                                        0L),
                                                                                                equalTo(22L)))))))),
                                        "E3",
                                        new NodeMatcher(
                                                equalTo("Unfiltered"),
                                                equalTo(1L),
                                                greaterThanOrEqualTo(0L),
                                                equalTo(1L),
                                                Map.of(
                                                        "Unfiltered",
                                                        Map.of(
                                                                "E2",
                                                                new NodeMatcher(
                                                                        equalTo("Unfiltered"),
                                                                        equalTo(1L),
                                                                        greaterThanOrEqualTo(0L),
                                                                        equalTo(0L))))),
                                        "E4",
                                        new NodeMatcher(
                                                equalTo("Unfiltered"),
                                                equalTo(1L),
                                                greaterThanOrEqualTo(0L),
                                                equalTo(1L),
                                                Map.of(
                                                        "Unfiltered",
                                                        Map.of(
                                                                "E2",
                                                                new NodeMatcher(
                                                                        equalTo("Unfiltered"),
                                                                        equalTo(1L),
                                                                        greaterThanOrEqualTo(0L),
                                                                        equalTo(0L)))))))));
    }

    @Test
    public void renderSmoke() throws IOException {
        final var profile = produceProfile();
        final var writer = new StringWriter();
        profile.render(writer);
        writer.close();
        final var svgString = writer.toString();
        assertThat(svgString, startsWith("<svg>"));
        assertThat(svgString, endsWith("</svg>\n"));
    }
}
