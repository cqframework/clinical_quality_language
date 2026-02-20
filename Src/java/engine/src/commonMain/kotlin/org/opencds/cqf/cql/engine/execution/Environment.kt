package org.opencds.cqf.cql.engine.execution

import kotlin.jvm.JvmOverloads
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.shared.QName
import org.hl7.elm.r1.*
import org.opencds.cqf.cql.engine.data.DataProvider
import org.opencds.cqf.cql.engine.data.ExternalFunctionProvider
import org.opencds.cqf.cql.engine.data.SystemDataProvider
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider
import org.opencds.cqf.cql.engine.util.JavaClass
import org.opencds.cqf.cql.engine.util.javaClass
import org.opencds.cqf.cql.engine.util.javaClassPackageName
import org.opencds.cqf.cql.engine.util.kotlinClassToJavaClass

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
        val provider =
            externalFunctionProviders[identifier]
                ?: throw CqlException(
                    "Could not resolve external function provider for library '${identifier}'."
                )
        return provider
    }

    // -- DataProvider "Helpers"
    fun resolvePath(target: Any?, path: String): Any? {
        if (target == null) {
            return null
        }

        // TODO: Path may include .'s and []'s.
        // For now, assume no qualifiers or indexers...
        val clazz: JavaClass<*> = target.javaClass

        if (clazz.getPackageName().startsWith("java.lang")) {
            throw CqlException(
                "Invalid path: $path for type: ${clazz.getName()} - this is likely an issue with the data model."
            )
        }

        val dataProvider = resolveDataProvider(clazz.getPackageName())
        return dataProvider!!.resolvePath(target, path)
    }

    fun `as`(operand: Any?, type: JavaClass<*>, isStrict: Boolean): Any? {
        if (operand == null) {
            return null
        }

        // Special case for Iterable instances being cast to CQL Lists.
        // See https://github.com/cqframework/clinical_quality_language/issues/1577.
        if (
            kotlinClassToJavaClass(Iterable::class).isAssignableFrom(type) && operand is Iterable<*>
        ) {
            return operand
        }

        if (type.isInstance(operand)) {
            return operand
        }

        val provider = resolveDataProvider(type.getPackageName(), false)
        if (provider != null) {
            return provider.`as`(operand, type, isStrict)
        }

        return null
    }

    fun objectEqual(left: Any?, right: Any?): Boolean? {
        if (left == null) {
            return null
        }

        val clazz: JavaClass<*> = left.javaClass

        val dataProvider = resolveDataProvider(clazz.getPackageName())
        return dataProvider!!.objectEqual(left, right)
    }

    fun objectEquivalent(left: Any?, right: Any?): Boolean? {
        if ((left == null) && (right == null)) {
            return true
        }

        if (left == null) {
            return false
        }

        val clazz: JavaClass<*> = left.javaClass

        val dataProvider = resolveDataProvider(clazz.getPackageName())
        return dataProvider!!.objectEquivalent(left, right)
    }

    fun createInstance(typeName: QName): Any? {
        var typeName = typeName
        typeName = fixupQName(typeName)
        val dataProvider = resolveDataProvider(typeName)
        return dataProvider.createInstance(typeName.getLocalPart())
    }

    fun setValue(target: Any?, path: String, value: Any?) {
        if (target == null) {
            return
        }

        val clazz: JavaClass<*> = target.javaClass

        val dataProvider = resolveDataProvider(clazz.getPackageName())
        dataProvider!!.setValue(target, path, value)
    }

    fun `is`(operand: Any?, type: JavaClass<*>): Boolean? {
        if (operand == null) {
            return null
        }

        // Special case for Iterable instances being checked against CQL List type.
        // See https://github.com/cqframework/clinical_quality_language/issues/1577.
        if (
            kotlinClassToJavaClass(Iterable::class).isAssignableFrom(type) && operand is Iterable<*>
        ) {
            return true
        }

        if (type.isInstance(operand)) {
            return true
        }

        val provider = resolveDataProvider(type.getPackageName(), false)
        if (provider != null) {
            return provider.`is`(operand, type)
        }

        return false
    }

    // -- DataProvider resolution
    fun registerDataProvider(modelUri: String?, dataProvider: DataProvider?) {
        dataProviders[modelUri] = dataProvider
        dataProvider!!.packageNames.forEach { pn -> packageMap[pn] = dataProvider }
    }

    fun resolveDataProvider(dataType: QName): DataProvider {
        var dataType = dataType
        dataType = fixupQName(dataType)
        return resolveDataProviderByModelUri(dataType.getNamespaceURI())
    }

    fun resolveDataProviderByModelUri(modelUri: String?): DataProvider {
        val dataProvider =
            dataProviders[modelUri]
                ?: throw CqlException("Could not resolve data provider for model '${modelUri}'.")

        return dataProvider
    }

    @JvmOverloads
    fun resolveDataProvider(packageName: String?, mustResolve: Boolean = true): DataProvider? {
        val dataProvider = packageMap[packageName]
        if (dataProvider == null && mustResolve) {
            throw CqlException("Could not resolve data provider for package '${packageName}'.")
        }

        return dataProvider
    }

    fun resolveType(typeName: QName?): JavaClass<*>? {
        var typeName = typeName
        typeName = fixupQName(typeName!!)
        val dataProvider = resolveDataProvider(typeName)
        return dataProvider.resolveType(typeName.getLocalPart())
    }

    fun resolveType(typeSpecifier: TypeSpecifier?): JavaClass<*>? {
        return when (typeSpecifier) {
            is NamedTypeSpecifier -> resolveType(typeSpecifier.name)
            is ListTypeSpecifier ->
                // TODO: This doesn't allow for list-distinguished overloads...
                kotlinClassToJavaClass(MutableList::class)
            // return resolveType(((ListTypeSpecifier)typeSpecifier).getElementType());
            is IntervalTypeSpecifier ->
                // TODO: This doesn't allow for interval-distinguished overloads
                kotlinClassToJavaClass(Interval::class)
            is ChoiceTypeSpecifier ->
                // TODO: This doesn't allow for choice-distinguished overloads...
                kotlinClassToJavaClass(Any::class)
            else ->
                // TODO: This doesn't allow for tuple-distinguished overloads....
                kotlinClassToJavaClass(Tuple::class)
        }
    }

    fun resolveType(value: Any?): JavaClass<*>? {
        if (value == null) {
            return null
        }

        if (value is TypeSpecifier) {
            return resolveType(value)
        }

        val packageName = value.javaClassPackageName

        // May not be necessary, idea is to sync with the use of List.class for
        // ListTypeSpecifiers in the resolveType above
        if (value is Iterable<*>) {
            return kotlinClassToJavaClass(MutableList::class)
        }

        if (value is Tuple) {
            return kotlinClassToJavaClass(Tuple::class)
        }

        // Primitives should just use the type
        // BTR: Well, we should probably be explicit about all and only the types we
        // expect
        if (packageName.startsWith("java")) {
            return value.javaClass
        }

        val dataProvider = resolveDataProvider(value.javaClassPackageName)
        return dataProvider!!.resolveType(value)
    }

    fun resolveOperandType(operandDef: OperandDef): JavaClass<*>? {
        return if (operandDef.operandTypeSpecifier != null) {
            resolveType(operandDef.operandTypeSpecifier)
        } else {
            resolveType(operandDef.operandType)
        }
    }

    fun isType(argumentType: JavaClass<*>?, operandType: JavaClass<*>): Boolean {
        return argumentType == null || operandType.isAssignableFrom(argumentType)
    }

    fun matchesTypes(functionDef: FunctionDef, arguments: kotlin.collections.List<*>): Boolean {
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
        if (typeName.getNamespaceURI().isEmpty()) {
            if (typeName.getLocalPart().startsWith("{")) {
                val closeIndex = typeName.getLocalPart().indexOf('}')
                if (closeIndex > 0 && typeName.getLocalPart().length > closeIndex) {
                    return QName(
                        typeName.getLocalPart().substring(1, closeIndex),
                        typeName.getLocalPart().substring(closeIndex + 1),
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
