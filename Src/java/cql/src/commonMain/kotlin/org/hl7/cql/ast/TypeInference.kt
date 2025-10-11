package org.hl7.cql.ast

/**
 * Records inferred types for AST nodes using [AstNodeId] keys. Entries are optional to allow
 * analyses to defer or omit type assignments when information is unavailable.
 */
class TypeTable internal constructor(private val ids: AstIdTable) {

    private val entries = mutableMapOf<AstNodeId, TypeSpecifier?>()

    fun assign(node: AstNode, type: TypeSpecifier?) {
        val id = ids.idFor(node)
        entries[id] = type
    }

    fun get(node: AstNode): TypeSpecifier? = entries[ids.idFor(node)]

    fun get(id: AstNodeId): TypeSpecifier? = entries[id]

    fun asMap(): Map<AstNodeId, TypeSpecifier?> = entries.toMap()

    companion object {
        fun empty(ids: AstIdTable): TypeTable = TypeTable(ids)
    }
}

data class TypeInferenceResult(
    val typeTable: TypeTable,
    val problems: List<Problem> = emptyList(),
)

interface TypeInferencer {
    fun infer(library: Library, ids: AstIdTable): TypeInferenceResult

    fun infer(expression: Expression, ids: AstIdTable): TypeInferenceResult
}

/**
 * Placeholder implementation that reserves space for inferred types without performing any
 * computation. Future versions will populate the [TypeTable] based on the CQL type system.
 */
class StubTypeInferencer : TypeInferencer {

    override fun infer(library: Library, ids: AstIdTable): TypeInferenceResult =
        TypeInferenceResult(TypeTable.empty(ids))

    override fun infer(expression: Expression, ids: AstIdTable): TypeInferenceResult =
        TypeInferenceResult(TypeTable.empty(ids))
}
