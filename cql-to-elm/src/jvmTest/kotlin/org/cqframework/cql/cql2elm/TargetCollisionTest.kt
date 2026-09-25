package org.cqframework.cql.cql2elm

import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.Retrieve
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * Repro/regression test for issue #1435: `TypeBuilder.dataTypeToQName` substitutes a NamedType's
 * `target` for its own name when building an ELM QName. Two fluent-function overloads declared
 * across sibling types that share a `target` (here, `WidgetNotDone` declares `target="Widget"`,
 * mirroring USQualityCore's `ProcedureNotDone` -> `Procedure`) therefore serialize to the identical
 * QName, making the overloads indistinguishable to the engine's runtime dispatcher
 * (`FunctionRefEvaluator.pickFunctionDef`), which throws "Ambiguous call to operator ...".
 *
 * The fix splits the shared QName builder: overload-resolution identity (the function signature and
 * the operand type specifiers the dispatcher compares) must preserve the true CQL-level type name,
 * while representation (what `Retrieve.dataType` feeds to a data provider as the literal resource
 * type name) must keep the model-info `target` substitution. This test pins both halves so neither
 * regression (the ambiguous-call crash NOR the unknown-resource-name crash observed when the
 * substitution was flat-removed) can slip back in.
 */
internal class TargetCollisionTest {
    @Test
    fun `sibling types sharing a target do not collide in dispatch, retain representation`() {
        val translator =
            CqlTranslator.fromSource(
                TargetCollisionTest::class
                    .java
                    .getResourceAsStream("TargetCollisionTests/TargetCollisionTest.cql")!!
                    .asSource()
                    .buffered(),
                LibraryManager(modelManager!!),
            )
        assertThat(translator.errors.size, `is`(0))

        val library = translator.toELM()!!
        val statements = library.statements!!.def

        // ---------------------------------------------------------------
        // Identity half: overload resolution must see the true CQL-level type name.
        // ---------------------------------------------------------------
        val callDef = statements.single { it.name == "CallOnNotDone" }
        val query = callDef.expression as Query
        val functionRef = query.`return`!!.expression as FunctionRef

        // The call's signature operand must be "WidgetNotDone" -- the type the retrieve actually
        // produced -- not the shared target "Widget".
        assertThat(
            (functionRef.signature.single() as NamedTypeSpecifier).name!!.localPart,
            `is`("WidgetNotDone"),
        )

        // The two user-declared overloads' operand type specifiers must differ from each other so
        // the runtime dispatcher can tell them apart.
        val functionDefs = statements.filterIsInstance<FunctionDef>()
        val recordedOnNotDone =
            functionDefs.single {
                it.name == "recorded" && it.operand.single().name == "procedureNotDone"
            }
        val recordedOnWidget =
            functionDefs.single { it.name == "recorded" && it.operand.single().name == "procedure" }
        assertThat(
            (recordedOnNotDone.operand.single().operandTypeSpecifier as NamedTypeSpecifier)
                .name!!
                .localPart,
            `is`("WidgetNotDone"),
        )
        assertThat(
            (recordedOnWidget.operand.single().operandTypeSpecifier as NamedTypeSpecifier)
                .name!!
                .localPart,
            `is`("Widget"),
        )

        // ---------------------------------------------------------------
        // Representation half: runtime type lookups must keep the substituted (resolvable) name.
        // ---------------------------------------------------------------
        // A [WidgetNotDone] retrieve must still ask the data provider for "Widget".
        val retrieve = query.source.single().expression as Retrieve
        assertThat(retrieve.dataType!!.localPart, `is`("Widget"))

        // An `is WidgetNotDone` check must still reference the runtime-representable "Widget".
        val isDef = statements.single { it.name == "IsNotDone" }
        val isQuery = isDef.expression as Query
        val isCheck = isQuery.`return`!!.expression as org.hl7.elm.r1.Is
        // NOTE: a type-expression `is` builds its target from a TypeSpecifier (isTypeSpecifier),
        // not from buildIs, so isType is null here -- assert on the specifier instead.
        assertThat((isCheck.isTypeSpecifier as NamedTypeSpecifier).name!!.localPart, `is`("Widget"))
    }

    companion object {
        private var modelManager: ModelManager? = null
        private var modelInfoProvider: ModelInfoProvider? = null

        @BeforeAll
        @JvmStatic
        fun setup() {
            modelManager = ModelManager()
            modelInfoProvider = TargetCollisionModelInfoProvider()
            modelManager!!.modelInfoLoader.registerModelInfoProvider(modelInfoProvider!!)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            modelManager!!.modelInfoLoader.unregisterModelInfoProvider(modelInfoProvider!!)
        }
    }
}
