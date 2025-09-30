package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ProfilingTest extends CqlTestBase {
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
