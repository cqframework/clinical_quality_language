package org.hl7.cql.model

import org.apache.commons.lang3.StringUtils

/**
 * The GenericClassSignatureParser is a convenience class for the parsing of generic signature and
 * the creation of the corresponding CQL DataTypes, namely, GenericClassType and
 * GenericClassProfileType. The former is used to capture the declaration of a GenericClass such as
 * List&lt;T&gt;. The latter is used to capture a new type such as 'IntegerList' formed by binding
 * types to generic parameters such as List&lt;Integer&gt;.
 */
@Suppress("MagicNumber", "TooGenericExceptionThrown", "TooManyFunctions", "NestedBlockDepth")
class GenericClassSignatureParser(
    /** A generic signature such as List&lt;T&gt; or a bound signature such as List&lt;Person&gt; */
    var genericSignature: String,
    /** The base type for the class type or the profile. */
    var baseType: String?,
    /** The name of a bound type such as PersonList = List&lt;Person&gt; */
    var boundGenericTypeName: String?,
    private val resolvedTypes: MutableMap<String, DataType>
) {
    @JvmOverloads
    constructor(
        genericSignature: String,
        resolvedTypes: MutableMap<String, DataType> = HashMap()
    ) : this(genericSignature, null, null, resolvedTypes)

    /**
     * Parses a generic type declaration such as Map&lt;K,V&gt;.
     *
     * @return Generic class constructed from this definition.
     */
    fun parseGenericSignature(): ClassType {
        var genericTypeName = genericSignature
        var params = listOf<String>()
        if (isValidGenericSignature) {
            genericTypeName = genericSignature.substring(0, genericSignature.indexOf('<'))
            val parameters =
                genericSignature.substring(
                    genericSignature.indexOf('<') + 1,
                    genericSignature.lastIndexOf('>')
                )
            params =
                escapeNestedCommas(parameters).split(",".toRegex()).dropLastWhile { it.isEmpty() }
        }
        var baseTypeName = baseType
        var baseTypeParameters: Array<String>? = null
        if (baseType != null && baseType!!.contains("<")) {
            baseTypeName = baseType!!.substring(0, baseType!!.indexOf('<'))
            val baseTypeParameterString =
                baseType!!.substring(baseType!!.indexOf('<') + 1, baseType!!.lastIndexOf('>'))
            baseTypeParameters =
                escapeNestedCommas(baseTypeParameterString)
                    .split(",".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()
        }
        val baseDataType = resolveTypeName(baseTypeName)
        val genericClassType = ClassType(genericTypeName, baseDataType)
        for (param in params) {
            val paramType = handleParameterDeclaration(unescapeNestedCommas(param))
            genericClassType.addGenericParameter(paramType)
        }
        if (baseTypeParameters != null) {
            var index = 0
            for (baseTypeParameter in baseTypeParameters) {
                if (
                    baseTypeParameter.length == 1 &&
                        genericClassType.getGenericParameterByIdentifier(baseTypeParameter) == null
                ) {
                    throw RuntimeException("Cannot resolve symbol $baseTypeParameter")
                } else {
                    val boundType = resolveTypeName(unescapeNestedCommas(baseTypeParameter))
                    val baseTypeClass = baseDataType as ClassType?
                    val baseClassFields: List<ClassTypeElement> = baseTypeClass!!.elements
                    val myParam = baseTypeClass.genericParameters[index].identifier
                    println(boundType.toString() + " replaces param " + myParam)
                    for ((name) in baseClassFields) {
                        val myElement = ClassTypeElement(name, boundType!!, false, false, null)
                        genericClassType.addElement(myElement)
                    }
                }
                index++
            }
        }
        return genericClassType
    }

    /**
     * Method handles a generic parameter declaration such as T, or T extends MyType.
     *
     * @param parameterString
     * @return Type parameter for this parameter for this string declaration.
     */
    private fun handleParameterDeclaration(parameterString: String): TypeParameter {
        val paramComponents =
            parameterString.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (paramComponents.size == 1) {
            TypeParameter(
                StringUtils.trim(parameterString),
                TypeParameter.TypeParameterConstraint.NONE,
                null
            )
        } else if (paramComponents.size == 3) {
            if (paramComponents[1].equals(EXTENDS, ignoreCase = true)) {
                TypeParameter(
                    paramComponents[0],
                    TypeParameter.TypeParameterConstraint.TYPE,
                    resolveTypeName(paramComponents[2])
                )
            } else {
                throw RuntimeException("Invalid parameter syntax: $parameterString")
            }
        } else {
            throw RuntimeException("Invalid parameter syntax: $parameterString")
        }
    }

    /**
     * Identifies the data type for the named type. If the argument is null, the return type will be
     * null.
     *
     * @param parameterType
     * @return The parameter's type
     */
    private fun resolveTypeName(parameterType: String?): DataType? {
        return if (isValidGenericSignature(parameterType)) {
            handleBoundType(parameterType!!)
        } else {
            if (parameterType == null) {
                null
            } else {
                resolveType(parameterType)
            }
        }
    }

    /**
     * Method resolves bound type if it exists or else creates it and adds it to the resolved type
     * index.
     *
     * @param boundGenericSignature
     * @return The bound type created or resolved
     */
    private fun handleBoundType(boundGenericSignature: String): DataType {
        var resolvedType =
            resolvedTypes[escapeNestedAngleBrackets(boundGenericSignature)] as? ClassType
        if (resolvedType != null) {
            return resolvedType
        } else {
            val genericTypeName =
                boundGenericSignature.substring(0, boundGenericSignature.indexOf('<'))
            resolvedType = resolveType(genericTypeName) as ClassType
            val newType = ClassType(escapeNestedAngleBrackets(boundGenericSignature), resolvedType)
            val parameters =
                boundGenericSignature.substring(
                    boundGenericSignature.indexOf('<') + 1,
                    boundGenericSignature.lastIndexOf('>')
                )
            val params =
                escapeNestedCommas(parameters)
                    .split(",".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            var index = 0
            for (param in params) {
                var param = param
                var boundParam: DataType? = null
                param = unescapeNestedCommas(param)
                boundParam =
                    if (isValidGenericSignature(param)) {
                        handleBoundType(param)
                    } else {
                        resolveType(param)
                    }
                val typeParameter = resolvedType.genericParameters[index]
                for ((name, type) in resolvedType.elements) {
                    if (type is TypeParameter) {
                        if (
                            (type as TypeParameter)
                                .identifier
                                .equals(typeParameter.identifier, ignoreCase = true)
                        ) {
                            val newElement = ClassTypeElement(name, boundParam, false, false, null)
                            newType.addElement(newElement)
                        }
                    }
                }
                index++
            }
            resolvedTypes[newType.name] = newType
            return newType
        }
    }

    val isValidGenericSignature: Boolean
        /**
         * Returns true if the generic signature assigned to this object is well-formed.
         *
         * @return True if the generic signature is valid
         */
        get() = isValidGenericSignature(genericSignature)

    /**
     * Returns true if the generic signature passed as an argument is well-formed.
     *
     * @param genericSignature
     * @return True if the generic signature is valid
     */
    fun isValidGenericSignature(genericSignature: String?): Boolean {
        return areBracketsPaired(genericSignature) &&
            closingBracketsComeAfterOpeningBrackets(genericSignature)
    }

    /**
     * Counts the number of &lt; in this signature.
     *
     * @param signatureString
     * @return
     */
    /**
     * Counts the number of &lt; in this signature.
     *
     * @return
     */
    private fun openBracketCount(signatureString: String? = genericSignature): Int {
        var matchCount = 0
        if (signatureString != null) {
            matchCount = StringUtils.countMatches(signatureString, OPEN_BRACKET)
        }
        return matchCount
    }

    /**
     * Counts the number of &gt; in this signature.
     *
     * @param signatureString
     * @return
     */
    /**
     * Counts the number of &gt; in this signature.
     *
     * @return
     */
    private fun closeBracketCount(signatureString: String? = genericSignature): Int {
        var matchCount = 0
        if (signatureString != null) {
            matchCount = StringUtils.countMatches(signatureString, CLOSE_BRACKET)
        }
        return matchCount
    }

    /**
     * Method returns if the number of &lt; matches the number of &gt;
     *
     * @param signatureString
     * @return
     */
    /**
     * Method returns if the number of &lt; matches the number of &gt;
     *
     * @return
     */
    private fun areBracketsPaired(signatureString: String? = genericSignature): Boolean {
        var paired = false
        if (signatureString != null) {
            val openCount = openBracketCount(signatureString)
            val closeCount = closeBracketCount(signatureString)
            paired = (openCount == closeCount) && (openCount > 0)
        }
        return paired
    }

    /**
     * Simple check to make sure that closing brackets come after opening brackets.
     *
     * @param signatureString
     * @return
     */
    /**
     * Simple check to make sure that closing brackets come after opening brackets.
     *
     * @return
     */
    private fun closingBracketsComeAfterOpeningBrackets(
        signatureString: String? = genericSignature
    ): Boolean {
        return signatureString != null &&
            signatureString.lastIndexOf('<') < signatureString.indexOf('>')
    }

    /**
     * Convenience method for the parsing of nested comma-separated parameters. Call before
     * unescapeNestedCommas when done processing the top level of nested parameters.
     *
     * @param signature
     * @return
     */
    private fun escapeNestedCommas(signature: String): String {
        val signatureCharArray = signature.toCharArray()
        var openBracketCount = 0
        for (index in signatureCharArray.indices) {
            val c = signatureCharArray[index]
            if (c == '<') {
                openBracketCount++
            } else if (c == '>') {
                openBracketCount--
            } else if (c == ',' && openBracketCount > 0) {
                signatureCharArray[index] = '|'
            }
        }
        return String(signatureCharArray)
    }

    /**
     * Convenience method for the parsing of nested comma-separated parameters. Call after
     * escapeNestedCommas when handling the top level of nested parameters.
     *
     * @param escapedSignature
     * @return
     */
    private fun unescapeNestedCommas(escapedSignature: String): String {
        return escapedSignature.replace("\\|".toRegex(), ",")
    }

    /**
     * Method looks up data type of typeName is index.
     *
     * @param typeName
     * @return
     */
    private fun resolveType(typeName: String): DataType {
        val type = resolvedTypes[typeName] ?: throw RuntimeException("Unable to resolve $typeName")
        return type
    }

    private fun escapeNestedAngleBrackets(genericSignature: String): String {
        return genericSignature.replace("<".toRegex(), "[").replace(">".toRegex(), "]")
    }

    companion object {
        const val OPEN_BRACKET = '<'
        const val CLOSE_BRACKET = '>'
        const val EXTENDS: String = "extends"
    }
}
