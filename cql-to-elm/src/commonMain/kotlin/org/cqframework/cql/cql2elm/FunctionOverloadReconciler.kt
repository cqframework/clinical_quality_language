package org.cqframework.cql.cql2elm

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.elm.visiting.BaseElmLibraryVisitor
import org.hl7.cql.model.DataType
import org.hl7.cql.model.NamedType
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.OperandDef
import org.hl7.elm.r1.OperandRef
import org.hl7.elm.r1.toJsonObject

/**
 * Reconciles same-named function overloads that collapse to the same runtime (ELM) signature.
 *
 * In CQL, overloads may be declared on distinct types -- for example the profile types `Procedure`
 * and `ProcedureNotDone` -- that the translator erases to a shared model-info `target` when it
 * builds the ELM operand QNames (see [TypeBuilder.dataTypeToQName]). The engine dispatches on those
 * QNames and has no concept of a profile (by design: the target map exists precisely so the engine
 * never sees profiles), so two overloads that differ only by profile become indistinguishable at
 * runtime and the engine throws `Ambiguous call to operator`.
 *
 * Because the engine must stay profile-free, two overloads that share a runtime signature genuinely
 * cannot be told apart at evaluation. After a library is compiled this reconciler therefore either:
 * - **collapses** the overloads to a single [FunctionDef] when their bodies are equivalent (the
 *   common case: the overloads do the same thing), so the engine sees one unambiguous function; or
 * - **reports a translation error** when the bodies differ, since the translator cannot faithfully
 *   represent two distinct runtime behaviors under one runtime signature. Accepting the library and
 *   failing at runtime is the worst outcome (see issues #1435 and #1855).
 *
 * Scope: this reconciles collisions **within a single library**. Overloads that collide across
 * included libraries are not reconciled here.
 *
 * See cqframework/clinical_quality_language issues #1435 and #1855.
 */
internal class FunctionOverloadReconciler(private val libraryBuilder: LibraryBuilder) {

    fun reconcile() {
        val statements = libraryBuilder.library.statements ?: return
        statements.def
            .filterIsInstance<FunctionDef>()
            .groupBy { runtimeSignature(it) }
            .forEach { (signature, overloads) ->
                if (overloads.size > 1) {
                    resolveCollision(signature, overloads, statements)
                }
            }
    }

    private fun resolveCollision(
        signature: String,
        overloads: List<FunctionDef>,
        statements: Library.Statements,
    ) {
        // Bodies that could not be canonicalized (e.g. a prior compile error left no expression)
        // are left alone; translation has already failed and emitting a second diagnostic here
        // would only add noise.
        val bodies = overloads.map { canonicalBody(it) }
        if (bodies.any { it == null }) {
            return
        }
        if (bodies.all { it == bodies[0] }) {
            // Equivalent bodies: keep the first overload, drop the rest from the emitted ELM so the
            // engine resolves a single, unambiguous FunctionDef.
            overloads.drop(1).forEach { statements.def.remove(it) }
        } else {
            libraryBuilder.recordParsingException(
                CqlSemanticException(
                    "Function '${overloads[0].name}' has overloads that are indistinguishable at " +
                        "runtime (all resolve to the signature $signature) but have different " +
                        "definitions. The execution engine does not distinguish the declared types " +
                        "(for example, profile types that share a model-info target), so these " +
                        "overloads would be ambiguous when the function is called. Declare a single " +
                        "overload on the shared base type, or make the overload definitions identical.",
                    null,
                    CqlCompilerException.ErrorSeverity.Error,
                )
            )
        }
    }

    /**
     * The function's name together with the model-info-`target`-substituted QName of each operand
     * -- i.e. the signature as the engine sees it after [TypeBuilder.dataTypeToQName] erases
     * profiles. Two overloads share a key exactly when the engine cannot tell their signatures
     * apart.
     */
    private fun runtimeSignature(functionDef: FunctionDef): String {
        return functionDef.name +
            functionDef.operand.joinToString(prefix = "(", postfix = ")") { operandType(it) }
    }

    private fun operandType(operand: OperandDef): String {
        val dataType = operand.resultType
        // dataTypeToQName performs the target substitution, but only for named types. Non-named
        // operand types (lists, intervals, tuples) are compared structurally via their stable
        // string form; they do not participate in the profile/target collision this addresses.
        return if (dataType is NamedType) {
            libraryBuilder.dataTypeToQName(dataType as DataType).toString()
        } else {
            dataType?.toString() ?: "<unknown>"
        }
    }

    /**
     * A canonical string form of the function body for equivalence comparison, or null when the
     * body cannot be canonicalized. Operand references are normalized positionally (so overloads
     * whose parameters differ only in name still match), and cosmetic/derived keys -- `localId`,
     * `locator`, `annotation`, and the inferred result-type annotations -- are stripped so that
     * comment edits, source positions, and profile-vs-target type annotations do not block a
     * collapse.
     */
    private fun canonicalBody(functionDef: FunctionDef): String? {
        val body = functionDef.expression
        return when {
            functionDef.external == true -> EXTERNAL_BODY
            body == null -> null
            else -> canonicalBodyOf(functionDef, body)
        }
    }

    private fun canonicalBodyOf(functionDef: FunctionDef, body: Expression): String {
        val operandRefs = mutableListOf<OperandRef>()
        OperandRefCollector.visitElement(body, operandRefs)
        val originalNames = operandRefs.map { it.name }
        val nameToToken =
            functionDef.operand.withIndex().associate { (index, operand) ->
                operand.name to "\$operand$index"
            }
        return try {
            operandRefs.forEach { ref -> nameToToken[ref.name]?.let { ref.name = it } }
            canonicalize(body.toJsonObject(false)).toString()
        } finally {
            operandRefs.forEachIndexed { index, ref -> ref.name = originalNames[index] }
        }
    }

    private fun canonicalize(element: JsonElement): JsonElement {
        return when (element) {
            is JsonObject ->
                JsonObject(
                    element
                        .filterKeys { it !in STRIPPED_KEYS }
                        .mapValues { canonicalize(it.value) }
                        .toList()
                        .sortedBy { (key, _) -> key }
                        .toMap()
                )
            is JsonArray -> JsonArray(element.map { canonicalize(it) })
            else -> element
        }
    }

    private companion object {
        /** Sentinel canonical body for external functions, which have no expression to compare. */
        private const val EXTERNAL_BODY = "<external>"

        /**
         * ELM JSON keys excluded from body equivalence (cosmetic, provenance, and derived types).
         */
        private val STRIPPED_KEYS =
            setOf("localId", "locator", "annotation", "resultTypeName", "resultTypeSpecifier")

        /** Collects every [OperandRef] within an expression tree so its name can be normalized. */
        private val OperandRefCollector =
            object : BaseElmLibraryVisitor<Unit, MutableList<OperandRef>>() {
                override fun defaultResult(elm: Element, context: MutableList<OperandRef>) = Unit

                override fun aggregateResult(aggregate: Unit, nextResult: Unit) = Unit

                override fun visitOperandRef(elm: OperandRef, context: MutableList<OperandRef>) {
                    context.add(elm)
                    return super.visitOperandRef(elm, context)
                }
            }
    }
}
