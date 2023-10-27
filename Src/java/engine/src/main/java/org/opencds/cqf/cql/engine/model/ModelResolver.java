package org.opencds.cqf.cql.engine.model;

import java.util.Collections;
import java.util.List;

/**
 * A ModelResolver provides support for mapping a logical model (e.g. QDM or FHIR)
 * onto a Java implementation of that model. Different implementations of the same
 * model might map to different implementation schemes with the simplest example
 * being classes in different package names, but also possibly with different property
 * naming schemes, etc.
 */
public interface ModelResolver {

    /**
     * @deprecated Use getPackageNames() instead
     */
    @Deprecated
    String getPackageName();

    /**
     * @deprecated Use setPackageNames#String instead
     */
    @Deprecated
    void setPackageName(String packageName);

    /**
     * Return the package names of Java objects supported by this model
     *
     * @return list of Java package names for model objects that
     * support this model.
     */
    default List<String> getPackageNames() {
        return Collections.singletonList( getPackageName() );
    }

    /**
     * Set the package names of Java objects supported by this model
     *
     * @param packageNames list of Java package names for model objects
     * that support this model.
     */
    default void setPackageNames(List<String> packageNames) {
        // Intentionally empty. This provides backwards
        // compatibility for models that implement a single
        // package name and still use the get/setPackageName
        // methods.
    }

    /**
     * Resolve the provided path expression for the provided target. Paths
     * can be things like simple dotted property notation (e.g. Patient.id)
     * or more complex things like list indexed property expressions
     * (e.g. Patient.name[0].given). The exact details are configued in the
     * model definition and passed to the ELM file during CQL to ELM
     * translation.
     *
     * @return result of the provided expression. Null is expected whenever a path doesn't
     * exist on the target.
     */
    Object resolvePath(Object target, String path);

    /**
     * Get the path expression that expresses the relationship between
     * the <code>targetType</code> and the given <code>contextType</code>.
     * For example, in a FHIR model, with context type <code>Patient</code> and
     * targetType Condition, the resulting path is <code>subject</code>
     * because that is the model property on the Condition object
     * that links the Condition to the Patient.
     */
    Object getContextPath(String contextType, String targetType);

    /**
     * Resolve the Java class that corresponds to the given model type
     *
     * @param typeName Model type name. In the ELM, model objects
     * are namespaced (e.g. FHIR.Patient), but the namespace is
     * removed prior to calling this method, so the input would just
     * be Patient.
     * @return Class object that represents the specified model type
     */
    Class<?> resolveType(String typeName);

    /**
     * Resolve the Java class that corresponds to the given model object
     * instance.
     *
     * @param value Object instance
     * @return Class object that represents the specified value
     */
    Class<?> resolveType(Object value);

    /**
     * Check whether or not a specified value instance is of the
     * specified type.
     *
     * @param value
     * @param type
     * @return true when the value is of the specified type, otherwise false.
     */
    Boolean is(Object value, Class<?> type);

    /**
     * Cast the specified value to the specified type. When type
     * conversion is not possible, null should be returned unless
     * the isStrict flag is set to true wherein an Exception will
     * be thrown.
     *
     * @param value model object instance
     * @param type type to which the value should be case
     * @param isStrict flag indicating how to handle invalid type conversion
     * @return the result of the value conversion or null if conversion is not possible.
     */
    Object as(Object value, Class<?> type, boolean isStrict);

    /**
     * Create an instance of the model object that corresponds
     * to the specified type.
     *
     * @param typeName Model type to create
     * @return new instance of the specified model type
     */
    Object createInstance(String typeName);

    /**
     * Set the value of a particular property on the given
     * model object.
     *
     * @param target model object
     * @param path path to the property that will be set
     * @param value value to set to the property indicated by the path expression
     */
    void setValue(Object target, String path, Object value);

    /**
     * Compare two objects for equality
     *
     * @param left left hand side of the equality expression
     * @param right right hand side of the equality expression
     * @return flag indicating whether the objects are equal
     */
    Boolean objectEqual(Object left, Object right);

    /**
     * Compare two objects for equivalence
     *
     * @param left left hand side of the equivalence expression
     * @param right right hand side of the equivalence expression
     * @return flag indicating whether the objects are equal
     */
    Boolean objectEquivalent(Object left, Object right);

    /**
     * Ensure that for a given object each implementation can introspect that object in its own way to resolve a String ID.
     *
     * @param target An Object from which an implementation can resolve an ID.
     * @return The ID resolved from the target Object.
     */
    String resolveId(Object target);
}
