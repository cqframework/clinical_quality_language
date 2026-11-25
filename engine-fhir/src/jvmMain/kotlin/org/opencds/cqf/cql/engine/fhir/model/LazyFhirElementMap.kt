package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.BaseRuntimeChildDefinition
import org.hl7.fhir.instance.model.api.IBase
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlList

/**
 * A lazy, memoizing view of a FHIR composite's children as a CQL element map.
 *
 * [FhirModelResolver.toCqlValue] can back a [org.opencds.cqf.cql.engine.runtime.ClassInstance] with
 * this map instead of eagerly converting every declared child. A child is converted on first access
 * and memoized; children that are never read are never converted. This restores the engine 4.6.0
 * behavior of materializing only the fields the logic touches — the fix for the per-retrieve
 * conversion cost and the whole-compartment OOM.
 *
 * ### Key-set identity (load-bearing)
 * The eager converter sets a key for *every* declared child, null-valued when the child is absent.
 * This map reproduces that exactly: [keys], [size] and [containsKey] come from [childrenByName]
 * (built from the same `definition.children`), and [get] returns `null` for a declared-but-absent
 * child. So `containsKey` means "is a declared child," not "has a non-null value" — the invariant
 * the structured-value equality/equivalence operators (which compare by key set and size) rely on.
 *
 * ### equals / hashCode
 * Extending [AbstractMap] provides entry-based `equals`/`hashCode`, so a lazy-backed
 * `ClassInstance` compares and hashes identically to an eager (dense-map) one. Those paths — along
 * with CQL `=`, `~`, set membership and `toString` — simply force full materialization via
 * [entries], which is the correct cost when a resource is actually compared or placed in a set.
 *
 * ### Threading and data stability (why this is opt-in)
 * The memo is a plain [HashMap] mutated on read, so this type relies on the single-thread
 * per-evaluation contract the engine already assumes; one instance must not be read from multiple
 * threads concurrently. Because reads are deferred to access time, it also assumes the underlying
 * HAPI [target] is value-stable for the lifetime of the value — not mutated or recycled after
 * retrieval. Lazy conversion is therefore opt-in and intended for repositories that guarantee this,
 * e.g. a bundle-backed `InMemoryFhirRepository` whose resources are a read-only in-memory snapshot.
 */
internal class LazyFhirElementMap(
    private val target: IBase,
    private val childrenByName: Map<String, BaseRuntimeChildDefinition>,
    private val convert: (Any?) -> Value?,
) : AbstractMap<String, Value?>() {

    // Distinguishes "not yet converted" (absent key) from "converted to null" (present, null).
    private val memo = HashMap<String, Value?>(childrenByName.size)

    override val keys: Set<String>
        get() = childrenByName.keys

    override val size: Int
        get() = childrenByName.size

    override fun isEmpty(): Boolean = childrenByName.isEmpty()

    override fun containsKey(key: String): Boolean = childrenByName.containsKey(key)

    override fun get(key: String): Value? {
        val child = childrenByName[key] ?: return null
        // memo is keyed for every child once converted, storing null for absent children, so
        // containsKey (not a null check) is what distinguishes "not yet converted" from "null".
        if (!memo.containsKey(key)) {
            memo[key] = convertChild(child)
        }
        return memo[key]
    }

    private fun convertChild(child: BaseRuntimeChildDefinition): Value? =
        child.accessor
            .getValues(target)
            .map { convert(it) }
            .let { values ->
                if (child.max == 1) {
                    values.firstOrNull()
                } else {
                    if (values.isEmpty()) null else values.toCqlList()
                }
            }

    /**
     * Materializes every child. This is the whole-map view that drives [AbstractMap]'s entry-based
     * `equals`/`hashCode`, CQL equivalence, and `toString`.
     */
    override val entries: Set<Map.Entry<String, Value?>>
        get() = childrenByName.keys.associateWith { get(it) }.entries

    /** Test seam: how many children have been converted (materialized) so far. */
    internal fun materializedChildCount(): Int = memo.size
}
