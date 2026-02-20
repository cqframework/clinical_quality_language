package org.opencds.cqf.cql.engine.model

import kotlin.reflect.KClass
import org.opencds.cqf.cql.engine.exception.InvalidCast
import org.opencds.cqf.cql.engine.util.javaClassName
import org.opencds.cqf.cql.engine.util.kotlinClassToJavaClassName

/**
 * Provides support for mapping a logical model (e.g. QDM or FHIR) onto a Kotlin implementation of
 * that model. Different implementations of the same model might map to different implementation
 * schemes with the simplest example being classes in different package names, but also possibly
 * with different property naming schemes, etc.
 */
interface BaseModelResolver {
    @get:Deprecated("Use getPackageNames() instead")
    @set:Deprecated("Use setPackageNames#String instead")
    var packageName: String?

    var packageNames: MutableList<String?>
        /**
         * Return the package names of Java objects supported by this model
         *
         * @return list of Java package names for model objects that support this model.
         */
        get() = mutableListOf(this.packageName)
        /**
         * Set the package names of Java objects supported by this model
         *
         * @param packageNames list of Java package names for model objects that support this model.
         */
        set(packageNames) {
            // Intentionally empty. This provides backwards
            // compatibility for models that implement a single
            // package name and still use the get/setPackageName
            // methods.
        }

    /**
     * Resolve the provided path expression for the provided target. Paths can be things like simple
     * dotted property notation (e.g. Patient.id) or more complex things like list indexed property
     * expressions (e.g. Patient.name[0].given). The exact details are configured in the model
     * definition and passed to the ELM file during CQL to ELM translation.
     *
     * @return result of the provided expression. Null is expected whenever a path doesn't exist on
     *   the target.
     */
    fun resolvePath(target: Any?, path: String?): Any?

    /**
     * Get the path expression that expresses the relationship between the `targetType` and the
     * given `contextType`. For example, in a FHIR model, with context type `Patient` and targetType
     * Condition, the resulting path is `subject` because that is the model property on the
     * Condition object that links the Condition to the Patient.
     */
    fun getContextPath(contextType: String?, targetType: String?): Any?

    /**
     * Resolve the Kotlin class that corresponds to the given model type
     *
     * @param typeName Model type name. In the ELM, model objects are namespaced (e.g.
     *   FHIR.Patient), but the namespace is removed prior to calling this method, so the input
     *   would just be Patient.
     * @return Class object that represents the specified model type
     */
    fun resolveKType(typeName: String?): KClass<*>?

    /**
     * Resolve the Kotlin class that corresponds to the given model object instance.
     *
     * @param value Object instance
     * @return Class object that represents the specified value
     */
    fun resolveKType(value: Any?): KClass<*>?

    /**
     * Check whether or not a specified value instance is of the specified type.
     *
     * @param value
     * @param type
     * @return true when the value is of the specified type, otherwise false.
     */
    fun `is`(value: Any?, type: KClass<*>?): Boolean? {
        if (value == null) {
            return null
        }

        return type!!.isInstance(value)
    }

    /**
     * Cast the specified value to the specified type. When type conversion is not possible, null
     * should be returned unless the isStrict flag is set to true wherein an Exception will be
     * thrown.
     *
     * @param value model object instance
     * @param type type to which the value should be case
     * @param isStrict flag indicating how to handle invalid type conversion
     * @return the result of the value conversion or null if conversion is not possible.
     */
    fun `as`(value: Any?, type: KClass<*>?, isStrict: Boolean): Any? {
        if (value == null) {
            return null
        }

        if (type!!.isInstance(value)) {
            return value
        }

        if (isStrict) {
            throw InvalidCast(
                "Cannot cast a value of type ${value.javaClassName} as ${kotlinClassToJavaClassName(type)}."
            )
        }

        return null
    }

    /**
     * Create an instance of the model object that corresponds to the specified type.
     *
     * @param typeName Model type to create
     * @return new instance of the specified model type
     */
    fun createInstance(typeName: String?): Any?

    /**
     * Set the value of a particular property on the given model object.
     *
     * @param target model object
     * @param path path to the property that will be set
     * @param value value to set to the property indicated by the path expression
     */
    fun setValue(target: Any?, path: String?, value: Any?)

    /**
     * Compare two objects for equality
     *
     * @param left left hand side of the equality expression
     * @param right right hand side of the equality expression
     * @return flag indicating whether the objects are equal
     */
    fun objectEqual(left: Any?, right: Any?): Boolean?

    /**
     * Compare two objects for equivalence
     *
     * @param left left hand side of the equivalence expression
     * @param right right hand side of the equivalence expression
     * @return flag indicating whether the objects are equal
     */
    fun objectEquivalent(left: Any?, right: Any?): Boolean?

    /**
     * Ensure that for a given object each implementation can introspect that object in its own way
     * to resolve a String ID.
     *
     * @param target An Object from which an implementation can resolve an ID.
     * @return The ID resolved from the target Object.
     */
    fun resolveId(target: Any?): String?
}
