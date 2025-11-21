package org.opencds.cqf.cql.engine.execution

import org.hamcrest.Description
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.exception.Backtrace.FunctionoidFrame
import org.opencds.cqf.cql.engine.exception.CqlException

internal class BacktraceTest : CqlTestBase() {
    internal class FunctionMatcher(private val expectedName: String?) :
        TypeSafeMatcher<FunctionDef>() {
        override fun matchesSafely(functionDefinition: FunctionDef): Boolean {
            return functionDefinition.name == expectedName
        }

        override fun describeTo(description: Description) {
            description.appendText("a function named ").appendValue(expectedName)
        }

        companion object {
            fun isFunction(expectedName: String?): FunctionMatcher {
                return FunctionMatcher(expectedName)
            }
        }
    }

    internal class ExpressionDefinitionMatcher(private val expectedName: String?) :
        TypeSafeMatcher<ExpressionDef>() {
        override fun matchesSafely(expressionDefinition: ExpressionDef): Boolean {
            return expressionDefinition.name == expectedName
        }

        override fun describeTo(description: Description) {
            description.appendText("an expression definition named ").appendValue(expectedName)
        }

        companion object {
            fun isExpressionDefinition(expectedName: String?): ExpressionDefinitionMatcher {
                return ExpressionDefinitionMatcher(expectedName)
            }
        }
    }

    internal class VariableMatcher(
        private val expectedName: String?,
        private val expectedValue: Any?,
    ) : TypeSafeMatcher<Variable>() {
        override fun matchesSafely(variable: Variable): Boolean {
            return variable.name == expectedName && variable.value == expectedValue
        }

        override fun describeTo(description: Description) {
            description
                .appendText("an argument with name ")
                .appendValue(expectedName)
                .appendText(" and value ")
                .appendValue(expectedValue)
        }

        companion object {
            fun isVariable(expectedName: String?, expectedValue: Any?): VariableMatcher {
                return VariableMatcher(expectedName, expectedValue)
            }
        }
    }

    internal class VersionedIdentifierMatcher(
        private val expectedId: String?,
        private val expectedVersion: String?,
    ) : TypeSafeMatcher<VersionedIdentifier>() {
        override fun matchesSafely(vi: VersionedIdentifier): Boolean {
            return true
        }

        override fun describeTo(description: Description) {
            description
                .appendText("a library id")
                .appendValue(expectedId)
                .appendText("a library version")
                .appendValue(expectedVersion)
        }

        companion object {
            fun isVersionedIdentifier(
                expectedId: String?,
                expectedVersion: String?,
            ): VersionedIdentifierMatcher {
                return VersionedIdentifierMatcher(expectedId, expectedVersion)
            }
        }
    }

    internal class ContextMatcher(
        private val expectedName: String?,
        private val expectedValue: Any?,
    ) : TypeSafeMatcher<FunctionoidFrame>() {
        override fun matchesSafely(frame: FunctionoidFrame): Boolean {
            return frame.contextName == expectedName && frame.contextValue == expectedValue
        }

        override fun describeTo(description: Description) {
            description
                .appendText("a context with name ")
                .appendValue(expectedName)
                .appendText(" and value ")
                .appendValue(expectedValue)
        }

        companion object {
            fun hasContext(expectedName: String?, expectedValue: Any?): ContextMatcher {
                return ContextMatcher(expectedName, expectedValue)
            }
        }
    }

    @Test
    fun backtrace_smoke() {
        try {
            engine.evaluate { library("BacktraceTest") }.onlyResultOrThrow
            Assertions.fail<Any?>("Expected exception but none was thrown")
        } catch (e: CqlException) {
            MatcherAssert.assertThat(e.backtrace, Matchers.notNullValue())
            val backtrace = e.backtrace
            val frames = backtrace.frames
            MatcherAssert.assertThat(frames, Matchers.hasSize(3))
            // Frame 1
            val frame1 = frames!![0]
            MatcherAssert.assertThat(frame1, Matchers.instanceOf(FunctionoidFrame::class.java))
            val frame1Functionoid = frame1 as FunctionoidFrame
            val definition1 = frame1Functionoid.definition
            MatcherAssert.assertThat(definition1, Matchers.instanceOf(FunctionDef::class.java))
            MatcherAssert.assertThat<FunctionDef?>(
                definition1 as FunctionDef?,
                FunctionMatcher.isFunction("F"),
            )
            MatcherAssert.assertThat(
                frame1Functionoid.arguments,
                Matchers.contains(VariableMatcher.isVariable("X", 3)),
            )
            MatcherAssert.assertThat(
                frame1Functionoid.localVariables,
                Matchers.containsInAnyOrder(
                    VariableMatcher.isVariable("A", 7),
                    VariableMatcher.isVariable("_", 1),
                ),
            )
            MatcherAssert.assertThat(
                frame1Functionoid.libraryIdentifier,
                VersionedIdentifierMatcher.isVersionedIdentifier(
                    "BacktraceTest",
                    expectedVersion = null,
                ),
            )
            MatcherAssert.assertThat(
                frame1Functionoid,
                ContextMatcher.hasContext("Unfiltered", null),
            )
            // Frame 2
            val frame2 = frames[1]
            MatcherAssert.assertThat(frame2, Matchers.instanceOf(FunctionoidFrame::class.java))
            val frame2Functionoid = frame2 as FunctionoidFrame
            val definition2 = frame2Functionoid.definition
            MatcherAssert.assertThat(definition2, Matchers.instanceOf(FunctionDef::class.java))
            MatcherAssert.assertThat<FunctionDef?>(
                definition2 as FunctionDef?,
                FunctionMatcher.isFunction("G"),
            )
            MatcherAssert.assertThat(
                frame2Functionoid.arguments,
                Matchers.contains(
                    VariableMatcher.isVariable("Y", 2),
                    VariableMatcher.isVariable("Z", "hi"),
                ),
            )
            MatcherAssert.assertThat(frame2Functionoid.localVariables, Matchers.empty())
            MatcherAssert.assertThat(
                frame2Functionoid.libraryIdentifier,
                VersionedIdentifierMatcher.isVersionedIdentifier(
                    "BacktraceTest",
                    expectedVersion = null,
                ),
            )
            MatcherAssert.assertThat(
                frame2Functionoid,
                ContextMatcher.hasContext("Unfiltered", null),
            )
            MatcherAssert.assertThat(frame2Functionoid.contextValue, Matchers.equalTo<Any?>(null))
            // Frame 3
            val frame3 = frames[2]
            MatcherAssert.assertThat(frame3, Matchers.instanceOf(FunctionoidFrame::class.java))
            val frame3Functionoid = frame3 as FunctionoidFrame
            val definition3 = frame3Functionoid.definition
            MatcherAssert.assertThat(definition3, Matchers.instanceOf(ExpressionDef::class.java))
            MatcherAssert.assertThat(
                definition3,
                ExpressionDefinitionMatcher.isExpressionDefinition("E2"),
            )
            MatcherAssert.assertThat(frame3Functionoid.arguments, Matchers.empty())
            MatcherAssert.assertThat(frame3Functionoid.localVariables, Matchers.empty())
            MatcherAssert.assertThat(
                frame3Functionoid.libraryIdentifier,
                VersionedIdentifierMatcher.isVersionedIdentifier(
                    "BacktraceTest",
                    expectedVersion = null,
                ),
            )
            MatcherAssert.assertThat(
                frame3Functionoid,
                ContextMatcher.hasContext("Unfiltered", null),
            )
        }
    }
}
