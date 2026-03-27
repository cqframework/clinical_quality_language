package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.cqframework.cql.cql2elm.utils.IdentityHashMap
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.OperandDefinition
import org.hl7.cql.model.DataType

/**
 * The compilation database produced by the elaboration fold. Contains all metadata that
 * [EmissionContext] needs to produce ELM, keyed by the final (post-elaboration) expression
 * identities.
 *
 * ## Why this replaces TypeTable + ConversionTable
 *
 * Today, [TypeTable] is built by [TypeResolver], then [Lowering] rewrites the tree (breaking
 * identities), then post-lowering re-typing rebuilds types and [TypeTable.mergeFrom] patches
 * them together. [ConversionTable] is built by [CoercionInserter] before [Lowering], so
 * [Lowering] must [ConversionTable.rekey] entries from old to new expression identities.
 *
 * [ElaborationResult] is built by a single elaboration fold that both inserts coercions and
 * applies lowering in one walk. Every entry is keyed by the expression identity that the fold
 * *creates* — the same identity that [EmissionContext] will see. No merge, no rekey.
 *
 * ## Coercion handling
 *
 * Simple coercions become structural AST nodes during elaboration:
 * - `ImplicitCast(targetType)` → [ImplicitCastExpression] wrapping the child
 * - `OperatorConversion("ToDecimal")` → [ConversionExpression] wrapping the child
 * - `LibraryConversion` → [FunctionCallExpression] wrapping the child
 *
 * Complex coercions that require multi-node ELM constructs (Query trees for list/interval
 * conversions, Case/Is/As trees for choice narrowing) remain as metadata in [coercions].
 * These are too heavyweight to represent as AST nodes — they're ELM-specific emission
 * concerns. The elaboration fold records them here; [EmissionContext] reads them the same
 * way it reads the current [ConversionTable], but without the rekey problem.
 *
 * ## Lifecycle
 *
 * Built incrementally during the elaboration fold (each `on*` handler writes entries for
 * the expression it returns). Read-only after the fold completes. Wrapped in a [SemanticModel]
 * alongside [SymbolTable], [OperatorRegistry], and [ModelContext] for downstream consumption.
 *
 * **Not thread-safe.** Owned by a single elaboration pass.
 */
class ElaborationResult {

    // --- Types ---

    private val types = IdentityHashMap<Expression, DataType>()

    /** Record the inferred type for an elaborated expression. */
    fun setType(expression: Expression, type: DataType?) {
        if (type != null) {
            types[expression] = type
            typedCount++
        }
    }

    /** Look up the inferred type of an elaborated expression. */
    fun getType(expression: Expression): DataType? = types[expression]

    // --- Operator resolutions ---

    private val operatorResolutions = IdentityHashMap<Expression, OperatorResolution>()

    /** Transfer an operator resolution from the input TypeTable to this result. */
    fun setOperatorResolution(expression: Expression, resolution: OperatorResolution?) {
        if (resolution != null) operatorResolutions[expression] = resolution
    }

    fun getOperatorResolution(expression: Expression): OperatorResolution? =
        operatorResolutions[expression]

    // --- Identifier resolutions ---

    private val identifierResolutions = IdentityHashMap<IdentifierExpression, Resolution>()

    fun setIdentifierResolution(expression: IdentifierExpression, resolution: Resolution?) {
        if (resolution != null) identifierResolutions[expression] = resolution
    }

    fun getIdentifierResolution(expression: IdentifierExpression): Resolution? =
        identifierResolutions[expression]

    // --- Function call resolutions ---

    private val functionCallResolutions =
        IdentityHashMap<FunctionCallExpression, FunctionDefinition>()

    fun setFunctionCallResolution(expression: FunctionCallExpression, definition: FunctionDefinition?) {
        if (definition != null) functionCallResolutions[expression] = definition
    }

    fun getFunctionCallResolution(expression: FunctionCallExpression): FunctionDefinition? =
        functionCallResolutions[expression]

    // --- Operand types ---

    private val operandTypes = IdentityHashMap<OperandDefinition, DataType>()

    fun setOperandType(operand: OperandDefinition, type: DataType?) {
        if (type != null) operandTypes[operand] = type
    }

    fun getOperandType(operand: OperandDefinition): DataType? = operandTypes[operand]

    // --- External function return types ---

    private val externalFunctionReturnTypes = IdentityHashMap<FunctionDefinition, DataType>()

    fun setExternalFunctionReturnType(funcDef: FunctionDefinition, type: DataType?) {
        if (type != null) externalFunctionReturnTypes[funcDef] = type
    }

    fun getExternalFunctionReturnType(funcDef: FunctionDefinition): DataType? =
        externalFunctionReturnTypes[funcDef]

    // --- Membership kinds ---

    private val membershipKinds = IdentityHashMap<Expression, MembershipKind>()

    fun setMembershipKind(expression: Expression, kind: MembershipKind?) {
        if (kind != null) membershipKinds[expression] = kind
    }

    fun getMembershipKind(expression: Expression): MembershipKind? = membershipKinds[expression]

    // --- Model conversions ---

    private val modelConversions =
        IdentityHashMap<Expression, org.cqframework.cql.cql2elm.model.Conversion>()

    fun setModelConversion(
        expression: Expression,
        conversion: org.cqframework.cql.cql2elm.model.Conversion?,
    ) {
        if (conversion != null) modelConversions[expression] = conversion
    }

    fun getModelConversion(
        expression: Expression,
    ): org.cqframework.cql.cql2elm.model.Conversion? = modelConversions[expression]

    // --- Complex coercions (metadata, not structural) ---

    private val coercions =
        IdentityHashMap<Expression, MutableMap<ConversionSlot, MutableList<ImplicitConversion>>>()

    /**
     * Record a complex coercion that cannot be represented as a single AST node. These are
     * applied by [EmissionContext] during code generation. Simple coercions (ImplicitCast,
     * OperatorConversion, LibraryConversion) should be inserted as AST nodes during elaboration
     * instead of recorded here.
     */
    fun addCoercion(parent: Expression, slot: ConversionSlot, conversion: ImplicitConversion) {
        val slotMap = coercions.getOrPut(parent) { mutableMapOf() }
        val list = slotMap.getOrPut(slot) { mutableListOf() }
        list.add(conversion)
    }

    /** Get complex coercions at a given parent/slot, for [EmissionContext]. */
    fun getCoercions(parent: Expression, slot: ConversionSlot): List<ImplicitConversion> {
        val slotMap = coercions[parent] ?: return emptyList()
        return slotMap[slot] ?: emptyList()
    }

    // --- Error tracking ---

    private val errors = mutableSetOf<Expression>()

    fun addError(expression: Expression) { errors.add(expression) }

    fun hasError(expression: Expression): Boolean = expression in errors

    // --- Metrics ---

    /** Incremented by [setType] for each expression that receives a non-null type. */
    var typedCount: Int = 0
        private set
}
