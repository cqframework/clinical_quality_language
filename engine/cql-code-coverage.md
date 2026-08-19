# CQL Code Coverage

The engine can collect and report CQL code coverage information for executed libraries. The generated coverage report shows which lines of CQL were reached (visited) during evaluation and reflects which statements and expressions were executed.

Here is an example of how to generate an LCOV coverage report:

```kotlin
// Enable coverage collection by the engine
engine.state.engineOptions.add(CqlEngine.Options.EnableCoverageCollection)

// Evaluate the libraries as usual
engine.evaluate("Tests")

// Export the LCOV content as a string
val lcov =
  engine.state.globalCoverage.exportLcovInfo(
    listOf(
      VersionedIdentifier().withId("Library1"),
      VersionedIdentifier().withId("Library2"),
    )
  )
```

When the `EnableCoverageCollection` option is active, the engine's evaluation visitor keeps track of which ELM expressions it visited. To generate the CQL line coverage report, the engine combines the visit counts with the `locator` information from the ELM nodes. This also means that CQL coverage information can only be reported for ELM libraries compiled with the `EnableLocators` option enabled.

The `lcov.info` output can be used with LCOV tools to generate summaries or HTML reports:

```bash
# Print summary
lcov --summary lcov.info --branch-coverage

# Generate HTML report
genhtml lcov.info --output-dir out/ --branch-coverage
```

The LCOV format is compatible with code coverage services like Codecov. An example report for a CQL library is available [here](https://app.codecov.io/gh/cqframework/clinical_quality_language/pull/1634/blob/Src/java/engine/src/test/resources/org/opencds/cqf/cql/engine/execution/CoverageTest/Library2.cql). This is how to interpret line colors in such reports specifically in the context of CQL:
* white - there are no ELM statements (ExpressionDefs/FunctionDefs) on this line
* red - none of the ELM statements on this line were evaluated
* green - every ELM Expression on this line has been evaluated
* yellow - there are ELM Expressions on this line that weren't evaluated

The line is also green if it has no ELM Expressions BUT has statement(s) that were evaluated.
