package org.hl7.cql.model

import java.util.*

@Suppress("TooManyFunctions", "ComplexCondition")
open class ClassType
@JvmOverloads
constructor(
    final override val name: String,
    baseType: DataType? = null,
    val elements: MutableList<ClassTypeElement> = mutableListOf(),
    /**
     * Generic class parameters such 'S', 'T extends MyType'.
     *
     * For instance, for the generic type Map&lt;K,V extends Person&gt;, two generic parameters will
     * be returned: K and V extends Person. The latter parameter has a constraint restricting the
     * type of the bound type to be a valid subtype of Person.
     */
    var genericParameters: MutableList<TypeParameter> = mutableListOf()
) : BaseDataType(baseType), NamedType {
    init {
        require(name.isNotEmpty()) { "A class type must have a name." }
    }

    override val namespace: String
        get() {
            // TODO Should this not be the last occurrence rather than the first occurrence?
            val qualifierIndex = name.indexOf('.')
            return if (qualifierIndex > 0) {
                name.substring(0, qualifierIndex)
            } else ""
        }

    override val simpleName: String
        get() {
            // TODO Should this not be the last occurrence rather than the first occurrence?
            val qualifierIndex = name.indexOf('.')
            return if (qualifierIndex > 0) {
                name.substring(qualifierIndex + 1)
            } else name
        }

    var identifier: String? = null
    var label: String? = null
    override var target: String? = null
    var isRetrievable: Boolean = false

    var primaryCodePath: String? = null

    var primaryValueSetPath: String? = null

    private val relationships: MutableList<Relationship> = ArrayList()

    fun getRelationships(): List<Relationship> {
        return relationships
    }

    fun addRelationship(relationship: Relationship) {
        relationships.add(relationship)
    }

    private val targetRelationships: MutableList<Relationship> = ArrayList()

    fun getTargetRelationships(): List<Relationship> {
        return targetRelationships
    }

    fun addTargetRelationship(relationship: Relationship) {
        targetRelationships.add(relationship)
    }

    private val searches: MutableList<SearchType> = ArrayList()

    fun getSearches(): List<SearchType> {
        return searches
    }

    fun addSearch(search: SearchType) {
        searches.add(search)
    }

    fun findSearch(searchPath: String): SearchType? {
        return searches.firstOrNull() { it.name == searchPath }
    }

    /**
     * Adds a parameter declaration to the generic type.
     *
     * @param genericParameter
     */
    fun addGenericParameter(genericParameter: TypeParameter) {
        genericParameters.add(genericParameter)
    }

    /**
     * Adds collection of type parameters to existing set.
     *
     * @param parameters
     */
    fun addGenericParameter(parameters: Collection<TypeParameter>) {
        for (parameter in parameters) {
            internalAddParameter(parameter)
        }
    }

    /**
     * Returns the parameter with the given parameter identifier. If not found in the given class,
     * it looks in the parent class.
     *
     * @param identifier
     * @return Generic parameter with the given name in the current class or in the base class. Null
     *   if none found.
     */
    fun getGenericParameterByIdentifier(identifier: String): TypeParameter? {
        return getGenericParameterByIdentifier(identifier, false)
    }

    /**
     * Returns the parameter with the given parameter identifier. If inCurrentClassOnly is false, if
     * not found in the given class, then it looks in the parent class. If inCurrentClassOnly is
     * true, only looks for parameter in the given class.
     *
     * @param identifier
     * @param inCurrentClassOnly
     * @return Class' generic parameter
     */
    fun getGenericParameterByIdentifier(
        identifier: String,
        inCurrentClassOnly: Boolean
    ): TypeParameter? {
        var param: TypeParameter? = null
        for (genericParameter in genericParameters) {
            if (identifier.equals(genericParameter.identifier, ignoreCase = true)) {
                param = genericParameter
                break
            }
        }
        if (!inCurrentClassOnly && param == null && baseType is ClassType) {
            param = (baseType as ClassType).getGenericParameterByIdentifier(identifier)
        }
        return param
    }

    val sortedElements: List<ClassTypeElement>
        get() = elements.sortedWith { o1, o2 -> o1.name.compareTo(o2.name) }

    private var baseElementMap: LinkedHashMap<String, ClassTypeElement>? = null
        get() {
            if (field == null) {
                field = LinkedHashMap()
                if (baseType is ClassType) {
                    (baseType as ClassType).gatherElements(field!!)
                }
            }

            return field
        }

    private fun gatherElements(elementMap: LinkedHashMap<String, ClassTypeElement>) {
        if (baseType is ClassType) {
            (baseType as ClassType).gatherElements(elementMap)
        }

        for (element in elements) {
            elementMap[element.name] = element
        }
    }

    val allElements: List<ClassTypeElement>
        get() {
            // Get the baseClass elements into a map by name
            val elementMap = LinkedHashMap(baseElementMap)

            // Add this class's elements, overwriting baseClass definitions where applicable
            for (el in elements) {
                elementMap[el.name] = el
            }

            return elementMap.values.toList()
        }

    private fun internalAddElement(element: ClassTypeElement) {
        val existingElement = baseElementMap!![element.name]
        if (
            existingElement != null &&
                (existingElement.type !is TypeParameter) &&
                (!(element.type.isSubTypeOf(existingElement.type) ||
                    (existingElement.type is ListType &&
                        element.type.isSubTypeOf(existingElement.type.elementType)) ||
                    (existingElement.type is IntervalType &&
                        element.type.isSubTypeOf(existingElement.type.pointType)) ||
                    (existingElement.type is ChoiceType &&
                        element.type.isCompatibleWith(existingElement.type))))
        ) {
            throw InvalidRedeclarationException(this, existingElement, element)
        }

        elements.add(element)
    }

    private fun internalAddParameter(parameter: TypeParameter) {
        // TODO Flesh out and retain method only if needed.

        genericParameters.add(parameter)
    }

    fun addElement(element: ClassTypeElement) {
        internalAddElement(element)
    }

    fun addElements(elements: Collection<ClassTypeElement>) {
        for (element in elements) {
            internalAddElement(element)
        }
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is ClassType) {
            return this.name == other.name
        }

        return false
    }

    override fun toString(): String {
        return this.name
    }

    override fun toLabel(): String {
        return if (this.label == null) this.name else label!!
    }

    val tupleType: TupleType
        get() = buildTupleType()

    private fun addTupleElements(
        classType: ClassType,
        elements: LinkedHashMap<String, TupleTypeElement>
    ) {
        // Add base elements first
        val baseType = classType.baseType
        if (baseType is ClassType) {
            addTupleElements(baseType, elements)
        }

        for ((name1, type, prohibited) in classType.elements) {
            if (!prohibited) {
                val tupleElement = TupleTypeElement(name1, type, false)
                elements[tupleElement.name] = tupleElement
            }
        }
    }

    private fun buildTupleType(): TupleType {
        val tupleElements = LinkedHashMap<String, TupleTypeElement>()

        addTupleElements(this, tupleElements)

        return TupleType(tupleElements.values)
    }

    override fun isCompatibleWith(other: DataType): Boolean {
        if (other is TupleType) {
            return tupleType == other
            // GitHub #115: It's incorrect for a class type to be considered compatible with another
            // class type on the
            // basis of the inferred tuple type alone.
            // } else if (other instanceof ClassType) {
            //    ClassType classType = (ClassType)other;
            //    return getTupleType().equals(classType.getTupleType());
        }

        return super.isCompatibleWith(other)
    }

    override val isGeneric: Boolean
        get() = genericParameters.isNotEmpty()

    override fun isInstantiable(callType: DataType, context: InstantiationContext): Boolean {
        return if (callType is ClassType && callType.elements.size == elements.size) {
            sortedElements.zip(callType.sortedElements).all { (thisElement, thatElement) ->
                thisElement.type.isInstantiable(thatElement.type, context)
            }
        } else false
    }

    override fun instantiate(context: InstantiationContext): DataType {
        if (!isGeneric) {
            return this
        }

        return ClassType(
            name,
            baseType,
            elements.map { ClassTypeElement(it.name, it.type.instantiate(context)) }.toMutableList()
        )
    }
}