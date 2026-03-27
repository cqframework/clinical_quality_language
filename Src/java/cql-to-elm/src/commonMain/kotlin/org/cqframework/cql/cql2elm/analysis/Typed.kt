package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.Expression
import org.hl7.cql.model.DataType

/**
 * The result type of the elaboration fold: an [Expression] paired with its inferred [DataType].
 *
 * ## Role in the pipeline
 *
 * The elaboration fold (`ExpressionFold<Typed>`) merges coercion insertion and lowering into a
 * single tree walk. It reads the [TypeTable] produced by [TypeResolver] (types, operator
 * resolutions, identifier resolutions) and produces:
 *
 * 1. A new AST with coercions and lowering applied — the root's [expr].
 * 2. An [ElaborationResult] (types + resolutions + coercions) keyed by the final expression
 *    identities — the compilation database that [EmissionContext] reads.
 *
 * [Typed] is what flows *between* fold handlers during the walk. Each `on*` handler receives
 * its children as [Typed] and returns [Typed] for the parent. It is ephemeral — only the root
 * [Typed] and the [ElaborationResult] survive after the fold completes.
 *
 * ## Why only two fields
 *
 * A parent handler needs exactly two things from each child:
 * - [expr]: the elaborated child expression, to embed in the parent's own node.
 * - [type]: the child's type, to decide whether a coercion is needed.
 *
 * Operator resolutions, identifier resolutions, membership kinds, and other metadata are
 * needed by codegen, not by parent handlers. They go into [ElaborationResult] as a side effect
 * of the elaboration fold, keyed by the final expression identities. This keeps [Typed] small
 * (allocated per expression during the walk) and keeps the fold handler signatures clean.
 *
 * ## Identity semantics
 *
 * When an expression passes through unchanged (no coercion, no lowering), the handler returns
 * the original [Expression] instance — preserving reference identity. When a child is modified
 * (coercion node inserted, operator lowered), the parent detects this via `child.expr !== expr.left`
 * and copies its own node. This is the same identity-preservation pattern used by [RewritingFold]
 * and [Lowering] today.
 *
 * ## Relationship to the current design
 *
 * Today, three separate folds walk the tree after [TypeResolver]:
 * - [CoercionInserter] (`ExpressionFold<Unit>`) — records coercions in [ConversionTable]
 * - [Lowering] (`ExpressionFold<Expression>`) — rewrites the tree, reads [TypeTable]
 * - Post-lowering re-typing — re-runs [TypeResolver], merges via [TypeTable.mergeFrom]
 *
 * The elaboration fold replaces all three. It walks once, inserts coercion nodes, applies
 * lowering, and records metadata — all keyed by the final expression identities it creates.
 * No convergence loop, no [ConversionTable.rekey], no [TypeTable.mergeFrom].
 */
data class Typed(
    /** The elaborated expression — possibly rewritten with coercion or lowering nodes. */
    val expr: Expression,
    /** The inferred type of [expr], or null if type inference failed. */
    val type: DataType?,
)
