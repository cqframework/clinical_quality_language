package org.cqframework.cql.cql2elm.model

import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import org.cqframework.cql.cql2elm.ModelManager
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.ClassTypeElement
import org.hl7.cql.model.DataType
import org.hl7.cql.model.GenericClassSignatureParser
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.ModelContext
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ProfileType
import org.hl7.cql.model.Relationship
import org.hl7.cql.model.SearchType
import org.hl7.cql.model.SimpleType
import org.hl7.cql.model.TupleType
import org.hl7.cql.model.TupleTypeElement
import org.hl7.cql.model.TypeParameter
import org.hl7.cql.model.TypeParameter.TypeParameterConstraint
import org.hl7.elm_modelinfo.r1.BoundParameterTypeSpecifier
import org.hl7.elm_modelinfo.r1.ChoiceTypeInfo
import org.hl7.elm_modelinfo.r1.ChoiceTypeSpecifier
import org.hl7.elm_modelinfo.r1.ClassInfo
import org.hl7.elm_modelinfo.r1.ClassInfoElement
import org.hl7.elm_modelinfo.r1.IntervalTypeInfo
import org.hl7.elm_modelinfo.r1.IntervalTypeSpecifier
import org.hl7.elm_modelinfo.r1.ListTypeInfo
import org.hl7.elm_modelinfo.r1.ListTypeSpecifier
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.NamedTypeSpecifier
import org.hl7.elm_modelinfo.r1.ParameterTypeSpecifier
import org.hl7.elm_modelinfo.r1.ProfileInfo
import org.hl7.elm_modelinfo.r1.RelationshipInfo
import org.hl7.elm_modelinfo.r1.SearchInfo
import org.hl7.elm_modelinfo.r1.SimpleTypeInfo
import org.hl7.elm_modelinfo.r1.TupleTypeInfo
import org.hl7.elm_modelinfo.r1.TupleTypeInfoElement
import org.hl7.elm_modelinfo.r1.TupleTypeSpecifier
import org.hl7.elm_modelinfo.r1.TypeInfo
import org.hl7.elm_modelinfo.r1.TypeParameterInfo
import org.hl7.elm_modelinfo.r1.TypeSpecifier

@Suppress(
    "NestedBlockDepth",
    "TooManyFunctions",
    "TooGenericExceptionThrown",
    "ReturnCount",
    "UnusedParameter",
)
class ModelImporter(val modelInfo: ModelInfo, val modelManager: ModelManager?) {
    private val modelIndex: MutableMap<String, Model> = HashMap()
    private val typeInfoIndex: MutableMap<String, TypeInfo> = HashMap()
    private val resolvedTypes: MutableMap<String, DataType> = HashMap()
    private val dataTypes: MutableList<DataType> = ArrayList()
    val conversions: MutableList<Conversion> = ArrayList()
    val contexts: MutableList<ModelContext> = ArrayList()
    private var defaultContext: ModelContext? = null

    init {
        if (modelManager != null) {
            // Import required models
            for (requiredModel in modelInfo.requiredModelInfo) {
                val model =
                    modelManager.resolveModel(
                        ModelIdentifier(
                            system = requiredModel.url,
                            id = requiredModel.name!!,
                            version = requiredModel.version,
                        )
                    )
                modelIndex[requiredModel.name!!] = model
            }

            // Ensure System model is registered
            if (!modelIndex.containsKey("System")) {
                val systemModel = modelManager.resolveModel(ModelIdentifier("System"))
                modelIndex["System"] = systemModel
            }
        }

        // Import model types
        for (t in this.modelInfo.typeInfo) {
            if (t is SimpleTypeInfo) {
                typeInfoIndex[ensureUnqualified(t.name!!)] = t
            } else if (t is ClassInfo && t.name != null) {
                typeInfoIndex[ensureUnqualified(t.name!!)] = t
            }
        }

        // Import model conversions
        for (c in this.modelInfo.conversionInfo) {
            val fromType = resolveTypeNameOrSpecifier(c.fromType, c.fromTypeSpecifier)
            val toType = resolveTypeNameOrSpecifier(c.toType, c.toTypeSpecifier)
            val qualifierIndex = c.functionName!!.indexOf('.')
            val libraryName =
                if (qualifierIndex >= 0) c.functionName!!.substring(0, qualifierIndex) else null
            val functionName =
                if (qualifierIndex >= 0) c.functionName!!.substring(qualifierIndex + 1) else null
            val operator = Operator(functionName!!, Signature(fromType!!), toType)
            operator.libraryName = libraryName

            // All conversions loaded as part of a model are implicit
            val conversion = Conversion(operator, true)
            conversions.add(conversion)
        }

        // Import model contexts
        for (c in this.modelInfo.contextInfo) {
            val contextType = resolveTypeSpecifier(c.contextType!!)
            require(contextType is ClassType) {
                // ERROR:
                "Model context ${c.name} must be a class type."
            }
            val modelContext =
                ModelContext(
                    c.name!!,
                    contextType,
                    c.keyElement!!.split(";".toRegex()).dropLastWhile { it.isEmpty() },
                    c.birthDateElement,
                )

            contexts.add(modelContext)
        }

        // For backwards compatibility with model info files that don't specify contexts, create a
        // default context based
        // on the patient class information if it's present
        if (contexts.isEmpty() && this.modelInfo.patientClassName != null) {
            val contextType = resolveTypeName(this.modelInfo.patientClassName!!)
            if (contextType is ClassType) {
                val modelContext =
                    ModelContext(
                        contextType.simpleName,
                        contextType,
                        mutableListOf("id"),
                        this.modelInfo.patientBirthDatePropertyName,
                    )
                contexts.add(modelContext)
                defaultContext = modelContext
            }
        }

        for (t in this.modelInfo.typeInfo) {
            val type = resolveTypeInfo(t)
            if (type != null) {
                dataTypes.add(type)
            }

            if (t is ClassInfo) {
                importRelationships(t, type as ClassType)
            }
        }
    }

    val types: Map<String, DataType>
        get() = resolvedTypes

    val defaultContextName: String?
        get() {
            if (modelInfo.defaultContext != null) {
                return modelInfo.defaultContext
            }

            return this.defaultContext?.name
        }

    private fun casify(
        typeName: String,
        caseSensitive: Boolean = modelInfo.isCaseSensitive() ?: false,
    ): String {
        return if (caseSensitive) typeName.lowercase() else typeName
    }

    private fun resolveTypeInfo(t: TypeInfo): DataType? {
        return when (t) {
            is SimpleTypeInfo -> resolveSimpleType(t)
            is ClassInfo -> resolveClassType(t)
            is TupleTypeInfo -> resolveTupleType(t)
            is IntervalTypeInfo -> resolveIntervalType(t)
            is ListTypeInfo -> resolveListType(t)
            is ChoiceTypeInfo -> resolveChoiceType(t)
            else -> null
        }
    }

    private fun resolveTypeSpecifier(typeSpecifier: TypeSpecifier): DataType? {
        if (typeSpecifier is NamedTypeSpecifier) {
            var qualifier = typeSpecifier.namespace
            if (qualifier.isNullOrEmpty()) {
                qualifier =
                    typeSpecifier
                        .modelName // For backwards compatibility, modelName is deprecated in favor
                // of
                // namespace
            }
            if (qualifier.isNullOrEmpty()) {
                qualifier = modelInfo.name
            }

            val qualifiedTypeName = "$qualifier.${typeSpecifier.name}"
            return resolveTypeName(qualifiedTypeName)
        }

        if (typeSpecifier is IntervalTypeSpecifier) {
            val pointType =
                resolveTypeNameOrSpecifier(
                    typeSpecifier.pointType,
                    typeSpecifier.pointTypeSpecifier,
                )
            return IntervalType(pointType!!)
        }

        if (typeSpecifier is ListTypeSpecifier) {
            val elementType =
                resolveTypeNameOrSpecifier(
                    typeSpecifier.elementType,
                    typeSpecifier.elementTypeSpecifier,
                )
            if (elementType != null) {
                return ListType(elementType)
            }
        }

        if (typeSpecifier is TupleTypeSpecifier) {
            val elements = mutableListOf<TupleTypeElement>()
            for (specifierElement in typeSpecifier.element) {
                val element =
                    TupleTypeElement(
                        specifierElement.name!!,
                        resolveTypeSpecifier(specifierElement.elementType!!)!!,
                    )
                elements.add(element)
            }
            return TupleType(elements)
        }

        if (typeSpecifier is ChoiceTypeSpecifier) {
            val choices: MutableList<DataType> = ArrayList()
            for (choice in typeSpecifier.choice) {
                val choiceType = resolveTypeSpecifier(choice)!!
                choices.add(choiceType)
            }
            return ChoiceType(choices)
        }

        return null
    }

    private fun resolveTypeName(typeName: String): DataType? {
        // NOTE: Preserving the ability to parse string type specifiers for backwards loading
        // compatibility
        // typeSpecifier: simpleTypeSpecifier | intervalTypeSpecifier | listTypeSpecifier
        // simpleTypeSpecifier: (identifier '.')? identifier
        // intervalTypeSpecifier: 'interval' '<' typeSpecifier '>'
        // listTypeSpecifier: 'list' '<' typeSpecifier '>'
        if (typeName.lowercase().startsWith("interval<")) {
            val pointType =
                resolveTypeName(
                    typeName.substring(typeName.indexOf('<') + 1, typeName.lastIndexOf('>'))
                )
            return IntervalType(pointType!!)
        } else if (typeName.lowercase().startsWith("list<")) {
            val elementType =
                resolveTypeName(
                    typeName.substring(typeName.indexOf('<') + 1, typeName.lastIndexOf('>'))
                )
            return ListType(elementType!!)
        }

        var result = lookupType(typeName)
        if (result == null) {
            val typeInfo =
                lookupTypeInfo(ensureUnqualified(typeName))
                    ?: throw IllegalArgumentException(
                        "Could not resolve type info for type name $typeName."
                    )

            result = resolveTypeInfo(typeInfo)
        }

        return result
    }

    private fun resolveTypeNameOrSpecifier(
        typeName: String?,
        typeSpecifier: TypeSpecifier?,
    ): DataType? {
        return when {
            typeName.isNullOrEmpty() && typeSpecifier == null -> null
            typeSpecifier != null -> resolveTypeSpecifier(typeSpecifier)
            else -> resolveTypeName(typeName!!)
        }
    }

    private fun lookupType(typeName: String): DataType? {
        val resolvedType = resolvedTypes[casify(typeName)]
        if (resolvedType != null) {
            return resolvedType
        }

        val qualifierIndex = typeName.indexOf(".")
        if (qualifierIndex < 0) {
            return null
        }

        val qualifier = typeName.substring(0, qualifierIndex)
        if (qualifier == "") {
            return null
        }

        if (qualifier != modelInfo.name) {
            val model = resolveModel(qualifier) ?: return null

            return model.resolveTypeName(typeName)
        }

        return null
    }

    private fun resolveModel(localIdentifier: String): Model? {
        return modelIndex[localIdentifier]
    }

    private fun lookupTypeInfo(typeName: String): TypeInfo? {
        return typeInfoIndex[typeName]
    }

    // This method is used to ensure backwards compatible loading, type names in model info may be
    // qualified with the
    // model name
    private fun ensureQualified(name: String): String {
        val qualifier = "${modelInfo.name}."
        if (!name.startsWith(qualifier)) {
            return "$qualifier$name"
        }

        return name
    }

    // This method is used to ensure backwards compatible loading, type names in model info may be
    // qualified with the
    // model name
    private fun ensureUnqualified(name: String): String {
        if (name.startsWith("${modelInfo.name}.")) {
            return name.substring(name.indexOf('.') + 1)
        }

        return name
    }

    private fun resolveSimpleType(t: SimpleTypeInfo): SimpleType {
        val qualifiedTypeName = ensureQualified(t.name!!)
        val lookupType = lookupType(qualifiedTypeName)
        require(lookupType !is ClassType) {
            "Expected instance of SimpleType but found instance of $lookupType instead."
        }

        var result = lookupType(qualifiedTypeName) as SimpleType?
        if (result == null) {
            result =
                if (qualifiedTypeName == DataType.ANY.name) {
                    DataType.ANY
                } else {
                    SimpleType(
                        qualifiedTypeName,
                        resolveTypeNameOrSpecifier(t.baseType, t.baseTypeSpecifier),
                        t.target,
                    )
                }
            resolvedTypes[casify(result.name)] = result
        }

        return result
    }

    private fun resolveTypeNameOrSpecifier(element: TupleTypeInfoElement): DataType? {
        var result = resolveTypeNameOrSpecifier(element.elementType, element.elementTypeSpecifier)
        if (result == null) {
            result = resolveTypeNameOrSpecifier(element.type, element.typeSpecifier)
        }

        return result
    }

    private fun resolveTupleTypeElements(
        infoElements: Collection<TupleTypeInfoElement>
    ): Collection<TupleTypeElement> {
        val elements: MutableList<TupleTypeElement> = ArrayList()
        for (e in infoElements) {
            elements.add(TupleTypeElement(e.name!!, resolveTypeNameOrSpecifier(e)!!))
        }
        return elements
    }

    private fun resolveTupleType(t: TupleTypeInfo): TupleType {
        val result =
            TupleType(
                resolveTupleTypeElements(t.element as Collection<TupleTypeInfoElement>)
                    .toMutableList()
            )
        return result
    }

    private fun resolveTypeNameOrSpecifier(element: ClassInfoElement): DataType? {
        var result = resolveTypeNameOrSpecifier(element.elementType, element.elementTypeSpecifier)
        if (result == null) {
            result = resolveTypeNameOrSpecifier(element.type, element.typeSpecifier)
        }

        return result
    }

    /**
     * Converts a list of GenericParameterInfo definitions into their corresponding TypeParameter
     * representations.
     *
     * @param parameterInfoList
     * @return
     */
    private fun resolveGenericParameterDeclarations(
        parameterInfoList: List<TypeParameterInfo>
    ): List<TypeParameter> {
        val genericParameters: MutableList<TypeParameter> = ArrayList()
        for (parameterInfo in parameterInfoList) {
            val constraint = parameterInfo.constraint
            val typeConstraint =
                when {
                    constraint.equals(TypeParameterConstraint.NONE.name, ignoreCase = true) -> {
                        TypeParameterConstraint.NONE
                    }
                    constraint.equals(TypeParameterConstraint.CLASS.name, ignoreCase = true) -> {
                        TypeParameterConstraint.CLASS
                    }
                    constraint.equals(TypeParameterConstraint.TUPLE.name, ignoreCase = true) -> {
                        TypeParameterConstraint.TUPLE
                    }
                    constraint.equals(TypeParameterConstraint.VALUE.name, ignoreCase = true) -> {
                        TypeParameterConstraint.VALUE
                    }
                    constraint.equals(TypeParameterConstraint.CHOICE.name, ignoreCase = true) -> {
                        TypeParameterConstraint.CHOICE
                    }
                    constraint.equals(TypeParameterConstraint.INTERVAL.name, ignoreCase = true) -> {
                        TypeParameterConstraint.INTERVAL
                    }
                    constraint.equals(TypeParameterConstraint.TYPE.name, ignoreCase = true) -> {
                        TypeParameterConstraint.TYPE
                    }
                    else -> TypeParameterConstraint.NONE
                }

            genericParameters.add(
                TypeParameter(
                    parameterInfo.name!!,
                    typeConstraint,
                    resolveTypeName(parameterInfo.constraintType!!),
                )
            )
        }
        return genericParameters
    }

    /**
     * Method resolves the types associated with class elements (i.e., class fields). If the type is
     * not resolved, the type System.Any is assigned to this element.
     *
     * @param classType
     * @param infoElements
     * @return
     */
    private fun resolveClassTypeElements(
        classType: ClassType,
        infoElements: Collection<ClassInfoElement>,
    ): Collection<ClassTypeElement> {
        val elements: MutableList<ClassTypeElement> = ArrayList()
        for (e in infoElements) {
            var elementType: DataType?
            elementType =
                if (isOpenType(e)) {
                    resolveOpenType(classType, e)
                } else if (isBoundParameterType(e)) {
                    resolveBoundType(classType, e)
                } else {
                    resolveTypeNameOrSpecifier(e)
                }
            if (elementType == null) {
                elementType = resolveTypeName("System.Any")
            }
            elements.add(
                ClassTypeElement(
                    e.name!!,
                    elementType!!,
                    e.isProhibited() ?: false,
                    e.isOneBased() ?: false,
                    e.target,
                )
            )
        }
        return elements
    }

    /**
     * Method returns true if class element is an open element bound to a specific type. For
     * instance, if the generic class defines a field:
     * <pre>`T field1;`</pre>
     *
     * A subclass my bind T to a specific type such as System.Quantity such that the definition
     * above becomes:
     * <pre>`System.Quantity field1;`</pre>
     *
     * @param element
     * @return
     */
    private fun isBoundParameterType(element: ClassInfoElement): Boolean {
        return element.elementTypeSpecifier is BoundParameterTypeSpecifier
    }

    /**
     * Method resolves the bound type declaration and returns the type if valid. Method throws an
     * exception if the type cannot be resolved (does not exist) or if the parameter that this type
     * is bound to is not defined in the generic class. Types must be bound to existing generic
     * parameters.
     *
     * @param classType
     * @param e
     * @return
     */
    private fun resolveBoundType(classType: ClassType?, e: ClassInfoElement): DataType? {
        val boundType: DataType?
        val boundParameterTypeSpecifier = e.elementTypeSpecifier as BoundParameterTypeSpecifier
        val parameterName = boundParameterTypeSpecifier.parameterName
        val genericParameter = classType!!.getGenericParameterByIdentifier(parameterName!!)

        if (genericParameter == null) {
            throw RuntimeException("Unknown symbol $parameterName")
        } else {
            boundType = resolveTypeName(boundParameterTypeSpecifier.boundType!!)
        }

        return boundType
    }

    /**
     * Returns true if the element's type is a parameterized (non-bound, non-concrete) type such as
     * <pre>`T myField;`</pre>
     *
     * @param element
     * @return
     */
    private fun isOpenType(element: ClassInfoElement): Boolean {
        return element.elementTypeSpecifier is ParameterTypeSpecifier
    }

    /**
     * Method to validate open types. An open type must reference a parameter defined in the generic
     * class by name and the generic parameter must exist.
     *
     * Open types are class attribute types that reference one of the generic parameter of the class
     * and that have not been bound to a concrete type.
     *
     * @param classType
     * @param e
     * @return
     */
    private fun resolveOpenType(classType: ClassType, e: ClassInfoElement): DataType {
        val elementType: DataType
        val parameterTypeSpecifier = e.elementTypeSpecifier as ParameterTypeSpecifier
        val parameterName = parameterTypeSpecifier.parameterName
        require(!parameterName.isNullOrEmpty()) { "Parameter name must not be null or empty" }
        if (classType.getGenericParameterByIdentifier(parameterName) == null) {
            throw RuntimeException(
                "Open types must reference a valid generic parameter and cannot be null or blank"
            )
        }
        elementType =
            TypeParameter(
                parameterTypeSpecifier.parameterName!!,
                TypeParameterConstraint.TYPE,
                null,
            )
        return elementType
    }

    private fun resolveClassTypeSearch(t: ClassType?, s: SearchInfo): SearchType {
        return SearchType(s.name!!, s.path!!, resolveTypeNameOrSpecifier(s.type, s.typeSpecifier)!!)
    }

    private fun resolveClassType(t: ClassInfo): ClassType {
        requireNotNull(t.name) { "Class definition must have a name." }
        val qualifiedName = ensureQualified(t.name!!)
        var result = lookupType(qualifiedName) as ClassType?

        if (result == null) {
            result =
                if (t is ProfileInfo) {
                    ProfileType(
                        qualifiedName,
                        resolveTypeNameOrSpecifier(t.baseType, t.baseTypeSpecifier) ?: DataType.ANY,
                    )
                } else {
                    // Added to support generic notation in ModelInfo file for class type names
                    // (e.g., MyGeneric<T>) and
                    // base classes (e.g., Map<String,Person>).
                    if (t.name!!.contains("<")) {
                        handleGenericType(t.name!!, t.baseType!!)
                    } else {
                        if (t.baseType != null && t.baseType!!.contains("<")) {
                            handleGenericType(t.name!!, t.baseType!!)
                        } else {
                            ClassType(
                                qualifiedName,
                                resolveTypeNameOrSpecifier(t.baseType, t.baseTypeSpecifier)
                                    ?: DataType.ANY,
                            )
                        }
                    }
                }

            resolvedTypes[casify(result.name)] = result

            result.addGenericParameter(
                resolveGenericParameterDeclarations(t.parameter as List<TypeParameterInfo>)
            )

            result.addElements(
                resolveClassTypeElements(result, t.element as Collection<ClassInfoElement>)
            )

            for (si in t.search) {
                result.addSearch(resolveClassTypeSearch(result, si))
            }

            // Here we handle the case when a type is not a generic but its base type is a generic
            // type whose parameters
            // have all been bound to concrete types (no remaining degrees of freedom) and is not
            // expressed in generic
            // notation in the model-info file.
            if (isParentGeneric(result) && !t.baseType!!.contains("<")) {
                validateFreeAndBoundParameters(result, t)
            }

            result.apply {
                identifier = t.identifier
                label = t.label
                target = t.target
                isRetrievable = t.isRetrievable()!!
                primaryCodePath = t.primaryCodePath
            }
        }

        return result
    }

    private fun resolveContext(contextName: String): ModelContext {
        for (context in this.contexts) {
            if (context.name == contextName) {
                return context
            }
        }

        throw IllegalArgumentException("Could not resolve context name $contextName.")
    }

    private fun resolveRelationship(relationshipInfo: RelationshipInfo): Relationship {
        val modelContext = resolveContext(relationshipInfo.context!!)
        val relationship =
            Relationship(
                modelContext,
                relationshipInfo.relatedKeyElement!!.split(";").dropLastWhile { it.isEmpty() },
            )
        return relationship
    }

    private fun importRelationships(c: ClassInfo, t: ClassType) {
        for (r in c.contextRelationship) {
            t.addRelationship(resolveRelationship(r))
        }

        for (r in c.targetContextRelationship) {
            t.addTargetRelationship(resolveRelationship(r))
        }
    }

    private fun resolveIntervalType(t: IntervalTypeInfo): IntervalType {
        val result = IntervalType(resolveTypeNameOrSpecifier(t.pointType, t.pointTypeSpecifier)!!)
        return result
    }

    private fun resolveListType(t: ListTypeInfo): ListType {
        val result = ListType(resolveTypeNameOrSpecifier(t.elementType, t.elementTypeSpecifier)!!)
        return result
    }

    private fun resolveChoiceType(t: ChoiceTypeInfo): ChoiceType {
        val types = ArrayList<DataType>()
        if (t.choice.isNotEmpty()) {
            for (typeSpecifier in t.choice) {
                types.add(resolveTypeSpecifier(typeSpecifier)!!)
            }
        } else {
            for (typeSpecifier in t.type) {
                types.add(resolveTypeSpecifier(typeSpecifier)!!)
            }
        }
        return ChoiceType(types)
    }

    /**
     * Method checks to see if a class' parameters covers its parent parameters. These represent
     * remaining degrees of freedom in the child class. For instance,
     * <pre>`MyGeneric<T> extends SomeOtherGeneric<String,T>`</pre>
     *
     * All parameters in the parent class, not covered by the child class must be bound to a
     * concrete type. In the above example, the parameter S is bound to the type String.
     *
     * If a parameter in the parent type is not covered by a child parameter nor bound to a concrete
     * type, an exception is thrown indicating that the symbol is not known. In the example below, T
     * is neither covered nor bound and thus is an unknown symbol. `<pre>MyGeneric extends
     * SomeOtherGeneric&lt;String,T&gt;</pre>`
     *
     * @param type
     * @param definition
     */
    fun validateFreeAndBoundParameters(type: ClassType?, definition: ClassInfo) {
        val coveredParameters: MutableList<String> = ArrayList()
        val boundParameters: MutableList<String> = ArrayList()

        (type!!.baseType as ClassType).genericParameters.forEach {
            val parameterName: String = it.identifier
            if (type.getGenericParameterByIdentifier(parameterName, true) != null) {
                coveredParameters.add(parameterName)
            } else {
                boundParameters.add(parameterName)
            }
        }

        if (boundParameters.isNotEmpty()) {
            definition.element.forEach {
                if (it.elementTypeSpecifier is BoundParameterTypeSpecifier) {
                    val name: String =
                        (it.elementTypeSpecifier as BoundParameterTypeSpecifier).parameterName!!
                    val paramIndex: Int = boundParameters.indexOf(name)
                    if (paramIndex >= 0) {
                        boundParameters.removeAt(paramIndex)
                    }
                }
            }
            if (boundParameters.isNotEmpty()) {
                throw RuntimeException("Unknown symbols $boundParameters")
            }
        }
    }

    /**
     * Method returns true if the class' base type is a generic type.
     *
     * @param type
     * @return True if the parent of class 'type' is a generic class.
     */
    fun isParentGeneric(type: ClassType): Boolean {
        return type.baseType is ClassType && type.baseType.isGeneric
    }

    /**
     * Converts a generic type declaration represented as a string into the corresponding generic
     * ClassType (i.e., a class type that specifies generic parameters).
     *
     * @param baseType The base type for the generic class type.
     * @param genericSignature The signature of the generic type such as Map&lt;K,V&gt;.
     * @return
     */
    private fun handleGenericType(genericSignature: String, baseType: String): ClassType {
        val parser = GenericClassSignatureParser(genericSignature, baseType, resolvedTypes)
        val genericClassType = parser.parseGenericSignature()

        return genericClassType
    }

    /**
     * Checks whether descendant is a valid subtype of ancestor.
     *
     * @param descendant
     * @param ancestor
     * @return
     */
    private fun conformsTo(descendant: DataType?, ancestor: DataType?): Boolean {
        val conforms =
            if ((descendant != null && ancestor != null) && descendant == ancestor) {
                true
            } else {
                conformsTo(descendant!!.baseType, ancestor)
            }
        return conforms
    }
}
