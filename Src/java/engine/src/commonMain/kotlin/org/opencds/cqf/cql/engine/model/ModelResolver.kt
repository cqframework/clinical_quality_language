package org.opencds.cqf.cql.engine.model

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Value

/**
 * A ModelResolver provides support for mapping a logical model (e.g. QDM or FHIR) onto a Java
 * implementation of that model. Different implementations of the same model might map to different
 * implementation schemes with the simplest example being classes in different package names, but
 * also possibly with different property naming schemes, etc.
 */
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
interface ModelResolver {
    /**
     * Get the path expression that expresses the relationship between the `targetType` and the
     * given `contextType`. For example, in a FHIR model, with context type `Patient` and targetType
     * Condition, the resulting path is `subject` because that is the model property on the
     * Condition object that links the Condition to the Patient.
     */
    fun getContextPath(contextType: String?, targetType: String?): String?

    /**
     * Check whether or not a specified `valueType` is a subtype of the specified `type`.
     *
     * @param valueType A model type, e.g. `"Patient"`
     * @param type E.g. `QName("http://hl7.org/fhir", "DomainResource")`
     * @return `true` when `valueType` is a subtype of (or the same type as) the specified `type`,
     *   otherwise `false`.
     */
    fun `is`(valueType: String, type: QName): kotlin.Boolean?

    /**
     * Create an instance of the model object that corresponds to the specified type.
     *
     * @param typeName Model type to create
     * @return new instance of the specified model type
     */
    fun createInstance(typeName: String?): Value?

    /**
     * Compare two instances of the same class type for equivalence. The default implementation
     * compares the elements of the structured values using the tuple equivalence semantics.
     *
     * @param left Left hand side of the equivalence expression
     * @param right Right hand side of the equivalence expression
     * @param equivalent Used to compare the elements of the structured values for equivalence
     * @return CQL Boolean indicating whether the instances are equivalent
     */
    fun objectEquivalent(
        left: ClassInstance,
        right: ClassInstance,
        equivalent: (l: Value?, r: Value?) -> Boolean,
    ): Boolean {
        return EquivalentEvaluator.structuredValueElementsEquivalent(
            left.elements,
            right.elements,
            equivalent,
        )
    }

    /**
     * Ensure that for a given object each implementation can introspect that object in its own way
     * to resolve a String ID.
     *
     * @param target An Object from which an implementation can resolve an ID.
     * @return The ID resolved from the target Object.
     */
    fun resolveId(target: Value?): String?
}
