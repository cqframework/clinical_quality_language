package org.opencds.cqf.cql.engine.execution

import java.util.function.Consumer
import javax.xml.namespace.QName
import org.cqframework.cql.cql2elm.LibraryManager
import org.hl7.elm.r1.*
import org.opencds.cqf.cql.engine.data.DataProvider
import org.opencds.cqf.cql.engine.data.ExternalFunctionProvider
import org.opencds.cqf.cql.engine.data.SystemDataProvider
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider

/**
 * The Environment class represents the current CQL execution environment. Meaning, things that are
 * set up outside of the CQL engine
 */
class Environment
@JvmOverloads
constructor(
    val libraryManager: LibraryManager?,
    dataProviders: MutableMap<String?, DataProvider?>? = null,
    val terminologyProvider: TerminologyProvider? = null,
) {
    val dataProviders = mutableMapOf<String?, DataProvider?>()

    private val packageMap = mutableMapOf<String?, DataProvider?>()

    // -- ExternalFunctionProviders -- TODO the registration of these... Should be
    // part of the LibraryManager?
    //
    private val externalFunctionProviders = HashMap<VersionedIdentifier, ExternalFunctionProvider>()

    // External function provider
    init {
        if (dataProviders != null) {
            for (dp in dataProviders.entries) {
                this.registerDataProvider(dp.key, dp.value!!)
            }
        }

        if (!this.dataProviders.containsKey("urn:hl7-org:elm-types:r1")) {
            this.registerDataProvider("urn:hl7-org:elm-types:r1", SystemDataProvider())
        }
    }

    fun registerExternalFunctionProvider(
        identifier: VersionedIdentifier,
        provider: ExternalFunctionProvider,
    ) {
        externalFunctionProviders[identifier] = provider
    }

    fun getExternalFunctionProvider(identifier: VersionedIdentifier?): ExternalFunctionProvider {
        val provider = externalFunctionProviders.get(identifier)
        if (provider == null) {
            throw CqlException(
                String.format(
                    "Could not resolve external function provider for library '%s'.",
                    identifier,
                )
            )
        }
        return provider
    }

    // -- DataProvider "Helpers"
    fun resolvePath(target: Any?, path: String): Any? {
        if (target == null) {
            return null
        }

        // TODO: Path may include .'s and []'s.
        // For now, assume no qualifiers or indexers...
        val clazz: Class<*> = target.javaClass

        if (clazz.getPackage().name.startsWith("java.lang")) {
            throw CqlException(
                String.format(
                    "Invalid path: %s for type: %s - this is likely an issue with the data model.",
                    path,
                    clazz.getName(),
                )
            )
        }

        val dataProvider = resolveDataProvider(clazz.getPackage().name)
        return dataProvider!!.resolvePath(target, path)
    }

    fun `as`(operand: Any?, type: Class<*>, isStrict: Boolean): Any? {
        if (operand == null) {
            return null
        }

        if (type.isAssignableFrom(operand.javaClass)) {
            return operand
        }

        val provider = resolveDataProvider(type.getPackage().name, false)
        if (provider != null) {
            return provider.`as`(operand, type, isStrict)
        }

        return null
    }

    fun objectEqual(left: Any?, right: Any?): Boolean? {
        if (left == null) {
            return null
        }

        val clazz: Class<*> = left.javaClass

        val dataProvider = resolveDataProvider(clazz.getPackage().name)
        return dataProvider!!.objectEqual(left, right)
    }

    fun objectEquivalent(left: Any?, right: Any?): Boolean? {
        if ((left == null) && (right == null)) {
            return true
        }

        if (left == null) {
            return false
        }

        val clazz: Class<*> = left.javaClass

        val dataProvider = resolveDataProvider(clazz.getPackage().name)
        return dataProvider!!.objectEquivalent(left, right)
    }

    fun createInstance(typeName: QName): Any? {
        var typeName = typeName
        typeName = fixupQName(typeName)
        val dataProvider = resolveDataProvider(typeName)
        return dataProvider.createInstance(typeName.localPart)
    }

    fun setValue(target: Any?, path: String, value: Any?) {
        if (target == null) {
            return
        }

        val clazz: Class<*> = target.javaClass

        val dataProvider = resolveDataProvider(clazz.getPackage().name)
        dataProvider!!.setValue(target, path, value)
    }

    fun `is`(operand: Any?, type: Class<*>): Boolean? {
        if (operand == null) {
            return null
        }

        if (type.isAssignableFrom(operand.javaClass)) {
            return true
        }

        val provider = resolveDataProvider(type.getPackage().name, false)
        if (provider != null) {
            return provider.`is`(operand, type)
        }

        return false
    }

    // -- DataProvider resolution
    fun registerDataProvider(modelUri: String?, dataProvider: DataProvider?) {
        dataProviders[modelUri] = dataProvider
        dataProvider!!.packageNames.forEach(Consumer { pn -> packageMap[pn] = dataProvider })
    }

    fun resolveDataProvider(dataType: QName): DataProvider {
        var dataType = dataType
        dataType = fixupQName(dataType)
        val dataProvider = dataProviders[dataType.namespaceURI]
        if (dataProvider == null) {
            throw CqlException(
                String.format(
                    "Could not resolve data provider for model '%s'.",
                    dataType.namespaceURI,
                )
            )
        }

        return dataProvider
    }

    fun resolveDataProviderByModelUri(modelUri: String?): DataProvider {
        val dataProvider = dataProviders[modelUri]
        if (dataProvider == null) {
            throw CqlException(
                String.format("Could not resolve data provider for model '%s'.", modelUri)
            )
        }

        return dataProvider
    }

    @JvmOverloads
    fun resolveDataProvider(packageName: String?, mustResolve: Boolean = true): DataProvider? {
        val dataProvider = packageMap.get(packageName)
        if (dataProvider == null && mustResolve) {
            throw CqlException(
                String.format("Could not resolve data provider for package '%s'.", packageName)
            )
        }

        return dataProvider
    }

    fun resolveType(typeName: QName?): Class<*>? {
        var typeName = typeName
        typeName = fixupQName(typeName!!)
        val dataProvider = resolveDataProvider(typeName)
        return dataProvider.resolveType(typeName.localPart)
    }

    fun resolveType(typeSpecifier: TypeSpecifier?): Class<*>? {
        if (typeSpecifier is NamedTypeSpecifier) {
            return resolveType(typeSpecifier.name)
        } else if (typeSpecifier is ListTypeSpecifier) {
            // TODO: This doesn't allow for list-distinguished overloads...
            return MutableList::class.java
            // return resolveType(((ListTypeSpecifier)typeSpecifier).getElementType());
        } else if (typeSpecifier is IntervalTypeSpecifier) {
            // TODO: This doesn't allow for interval-distinguished overloads
            return Interval::class.java
        } else if (typeSpecifier is ChoiceTypeSpecifier) {
            // TODO: This doesn't allow for choice-distinguished overloads...
            return Any::class.java
        } else {
            // TODO: This doesn't allow for tuple-distinguished overloads....
            return Tuple::class.java
        }
    }

    fun resolveType(value: Any?): Class<*>? {
        if (value == null) {
            return null
        }

        if (value is TypeSpecifier) {
            return resolveType(value)
        }

        val packageName = value.javaClass.getPackage().name

        // May not be necessary, idea is to sync with the use of List.class for
        // ListTypeSpecifiers in the resolveType above
        if (value is Iterable<*>) {
            return MutableList::class.java
        }

        if (value is Tuple) {
            return Tuple::class.java
        }

        // Primitives should just use the type
        // BTR: Well, we should probably be explicit about all and only the types we
        // expect
        if (packageName.startsWith("java")) {
            return value.javaClass
        }

        val dataProvider = resolveDataProvider(value.javaClass.getPackage().name)
        return dataProvider!!.resolveType(value)
    }

    fun resolveOperandType(operandDef: OperandDef): Class<*>? {
        if (operandDef.operandTypeSpecifier != null) {
            return resolveType(operandDef.operandTypeSpecifier)
        } else {
            return resolveType(operandDef.operandType)
        }
    }

    fun isType(argumentType: Class<*>?, operandType: Class<*>): Boolean {
        return argumentType == null || operandType.isAssignableFrom(argumentType)
    }

    fun matchesTypes(functionDef: FunctionDef, arguments: List<*>): Boolean {
        var isMatch = true

        val operands = functionDef.operand

        // if argument length is mismatched, don't compare
        if (arguments.size != operands.size) {
            return false
        }

        for (i in arguments.indices) {
            isMatch = isType(resolveType(arguments[i]), this.resolveOperandType(operands[i])!!)
            if (!isMatch) {
                break
            }
        }

        return isMatch
    }

    fun fixupQName(typeName: QName): QName {
        // When a Json library is deserialized on Android
        if (typeName.namespaceURI == null || typeName.namespaceURI.isEmpty()) {
            if (typeName.localPart != null && typeName.localPart.startsWith("{")) {
                val closeIndex = typeName.localPart.indexOf('}')
                if (closeIndex > 0 && typeName.localPart.length > closeIndex) {
                    return QName(
                        typeName.localPart.substring(1, closeIndex),
                        typeName.localPart.substring(closeIndex + 1),
                    )
                }
            }
        }

        return typeName
    }

    fun resolveLibrary(identifier: VersionedIdentifier): Library? {
        return this.libraryManager!!.resolveLibrary(identifier).library
    }
}
