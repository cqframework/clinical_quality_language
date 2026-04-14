package org.opencds.cqf.cql.engine.model

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName

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
    fun getContextPath(contextType: String?, targetType: String?): Any?

    /**
     * Check whether or not a specified `valueType` is a subtype of the specified `type`.
     *
     * @param valueType A model type, e.g. `"Patient"`
     * @param type E.g. `QName("http://hl7.org/fhir", "DomainResource")`
     * @return `true` when `valueType` is a subtype of (or the same type as) the specified `type`,
     *   otherwise `false`.
     */
    fun `is`(valueType: String, type: QName): Boolean?

    /**
     * Create an instance of the model object that corresponds to the specified type.
     *
     * @param typeName Model type to create
     * @return new instance of the specified model type
     */
    fun createInstance(typeName: String?): Any?

    /**
     * Ensure that for a given object each implementation can introspect that object in its own way
     * to resolve a String ID.
     *
     * @param target An Object from which an implementation can resolve an ID.
     * @return The ID resolved from the target Object.
     */
    fun resolveId(target: Any?): String?
}
