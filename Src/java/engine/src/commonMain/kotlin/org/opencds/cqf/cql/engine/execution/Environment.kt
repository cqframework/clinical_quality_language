package org.opencds.cqf.cql.engine.execution

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName
import org.hl7.elm.r1.*
import org.opencds.cqf.cql.engine.data.DataProvider
import org.opencds.cqf.cql.engine.data.ExternalFunctionProvider
import org.opencds.cqf.cql.engine.data.SystemDataProvider
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.exception.InvalidCast
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CodeSystem
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.CqlClassInstance
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.ValueSet
import org.opencds.cqf.cql.engine.runtime.getNamedTypeForCqlValue
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider

/**
 * The Environment class represents the current CQL execution environment. Meaning, things that are
 * set up outside of the CQL engine
 */
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
class Environment
@JvmOverloads
constructor(
    val libraryManager: LibraryManager?,
    dataProviders: MutableMap<String?, DataProvider?>? = null,
    val terminologyProvider: TerminologyProvider? = null,
) {
    val dataProviders = mutableMapOf<String?, DataProvider?>()

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
        var target = target
        //  The path attribute may include qualifiers (.) and indexers ([x])
        val qualifiersAndIndexers =
            path.split('.', '[', ']').map { it.trim() }.filter { it.isNotEmpty() }
        for (qualifierOrIndexer in qualifiersAndIndexers) {
            val indexer = qualifierOrIndexer.toIntOrNull()
            target =
                if (indexer == null) {
                    resolveProperty(target, qualifierOrIndexer)
                } else {
                    (target as Iterable<*>).elementAtOrNull(indexer)
                }
        }
        return target
    }

    fun resolveProperty(target: Any?, property: String): Any? {
        if (target == null) {
            return null
        }

        return when (target) {
            is Quantity -> {
                when (property) {
                    "value" -> target.value
                    "unit" -> target.unit
                    else -> null
                }
            }
            is Ratio -> {
                when (property) {
                    "numerator" -> target.numerator
                    "denominator" -> target.denominator
                    else -> null
                }
            }
            is Code -> {
                when (property) {
                    "code" -> target.code
                    "display" -> target.display
                    "system" -> target.system
                    "version" -> target.version
                    else -> null
                }
            }
            is Concept -> {
                when (property) {
                    "display" -> target.display
                    "codes" -> target.codes
                    else -> null
                }
            }
            is CodeSystem -> {
                when (property) {
                    "id" -> target.id
                    "version" -> target.version
                    "name" -> target.name
                    else -> null
                }
            }
            is ValueSet -> {
                when (property) {
                    "id" -> target.id
                    "version" -> target.version
                    "name" -> target.name
                    "codesystems" -> target.codeSystems
                    else -> null
                }
            }
            is Interval -> {
                when (property) {
                    "low" -> target.low
                    "lowClosed" -> target.lowClosed
                    "high" -> target.high
                    "highClosed" -> target.highClosed
                    else -> null
                }
            }
            is Tuple -> target.getElement(property)
            is CqlClassInstance -> target.elements[property]
            else -> throw IllegalArgumentException("Could not resolve path '$property' on $target.")
        }
    }

    fun `as`(operand: Any?, type: TypeSpecifier, isStrict: Boolean): Any? {
        if (`is`(operand, type) == true) {
            return operand
        }

        if (isStrict) {
            throw InvalidCast("Cannot cast $operand to $type.")
        }

        return null
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

        when (target) {
            is Quantity -> {
                when (path) {
                    "value" -> target.value = value as BigDecimal?
                    "unit" -> target.unit = value as String?
                    else -> throw IllegalArgumentException("Could not set $path on Quantity.")
                }
            }
            is Ratio -> {
                when (path) {
                    "numerator" -> target.numerator = value as Quantity?
                    "denominator" -> target.denominator = value as Quantity?
                    else -> throw IllegalArgumentException("Could not set $path on Ratio.")
                }
            }
            is Code -> {
                when (path) {
                    "code" -> target.code = value as String?
                    "display" -> target.display = value as String?
                    "system" -> target.system = value as String?
                    "version" -> target.version = value as String?
                    else -> throw IllegalArgumentException("Could not set $path on Code.")
                }
            }
            is Concept -> {
                when (path) {
                    "display" -> target.display = value as String?
                    "codes" ->
                        target.codes = @Suppress("UNCHECKED_CAST") (value as MutableList<Code?>?)
                    else -> throw IllegalArgumentException("Could not set $path on Concept.")
                }
            }
            is CodeSystem -> {
                when (path) {
                    "id" -> target.id = value as String?
                    "version" -> target.version = value as String?
                    "name" -> target.name = value as String?
                    else -> throw IllegalArgumentException("Could not set $path on CodeSystem.")
                }
            }
            is ValueSet -> {
                when (path) {
                    "id" -> target.id = value as String?
                    "version" -> target.version = value as String?
                    "name" -> target.name = value as String?
                    "codesystems" ->
                        target.setCodeSystems(
                            @Suppress("UNCHECKED_CAST") (value as MutableList<CodeSystem?>?)
                        )
                    else -> throw IllegalArgumentException("Could not set $path on ValueSet.")
                }
            }
            is Interval -> {
                when (path) {
                    "low" -> target.low = value
                    "high" -> target.high = value
                    else -> throw IllegalArgumentException("Could not set $path on $target.")
                }
            }
            is CqlClassInstance -> target.elements[path] = value
            else -> throw IllegalArgumentException("Could not set $path on $target.")
        }
    }

    fun `is`(operand: Any?, type: TypeSpecifier): Boolean? {
        // System.Any is a supertype of all types
        if (type is NamedTypeSpecifier && type.name == QName("urn:hl7-org:elm-types:r1", "Any")) {
            return true
        }

        if (operand == null) {
            return false
        }

        when (type) {
            is NamedTypeSpecifier -> {
                val operandNamedType = getNamedTypeForCqlValue(operand)

                if (operandNamedType == null) {
                    return false
                }

                val provider =
                    resolveDataProviderByModelUriOrNull(operandNamedType.getNamespaceURI())

                if (provider == null) {
                    return null
                }

                return provider.`is`(operandNamedType.getLocalPart(), type.name!!)
            }
            is ListTypeSpecifier -> {
                if (operand is Iterable<*>) {
                    if (operand.any()) {
                        for (item in operand) {
                            val result = `is`(item, type.elementType!!)
                            if (result == null) {
                                return null
                            }
                            if (result == false) {
                                return false
                            }
                        }
                        return true
                    }
                    // An empty list has type List<Any>
                    return type.elementType == QName("urn:hl7-org:elm-types:r1", "Any")
                }
                return false
            }
            is IntervalTypeSpecifier -> {
                if (operand is Interval) {
                    val lowResult = `is`(operand.low, type.pointType!!)
                    if (lowResult == false) {
                        return false
                    }

                    val highResult = `is`(operand.high, type.pointType!!)
                    if (highResult == false) {
                        return false
                    }

                    if (lowResult == true || highResult == true) {
                        return true
                    }

                    return null
                }
                return false
            }
            is TupleTypeSpecifier -> {
                if (
                    operand is Tuple &&
                        operand.elements.keys == type.element.map { it.name!! }.toSet()
                ) {

                    for (elementDefinition in type.element) {
                        val elementValue = operand.elements[elementDefinition.name!!]
                        val result = `is`(elementValue, elementDefinition.elementType!!)
                        if (result == null) {
                            return null
                        }
                        if (result == false) {
                            return false
                        }
                    }
                    return true
                }
                return false
            }
            is ChoiceTypeSpecifier -> {
                var foundNull = false
                for (choice in type.choice) {
                    val result = `is`(operand, choice)
                    if (result == null) {
                        foundNull = true
                    }
                    if (result == true) {
                        return true
                    }
                }
                return if (foundNull) {
                    null
                } else {
                    false
                }
            }
            else -> {
                return false
            }
        }
    }

    // -- DataProvider resolution
    fun registerDataProvider(modelUri: String?, dataProvider: DataProvider?) {
        dataProviders[modelUri] = dataProvider
    }

    @JsName("resolveDataProviderByQName")
    fun resolveDataProvider(dataType: QName): DataProvider {
        var dataType = dataType
        dataType = fixupQName(dataType)
        return resolveDataProviderByModelUri(dataType.getNamespaceURI())
    }

    fun resolveDataProviderByModelUri(modelUri: String?): DataProvider {
        return resolveDataProviderByModelUriOrNull(modelUri)
            ?: throw CqlException("Could not resolve data provider for model '${modelUri}'.")
    }

    fun resolveDataProviderByModelUriOrNull(modelUri: String?): DataProvider? {
        return dataProviders[modelUri]
    }

    fun resolveOperandType(operandDef: OperandDef): TypeSpecifier {
        return if (operandDef.operandTypeSpecifier != null) {
            operandDef.operandTypeSpecifier!!
        } else {
            NamedTypeSpecifier().withName(operandDef.operandType)
        }
    }

    fun matchesTypes(functionDef: FunctionDef, arguments: kotlin.collections.List<*>): Boolean {
        val operands = functionDef.operand

        // if argument length is mismatched, don't compare
        if (arguments.size != operands.size) {
            return false
        }

        return arguments.zip(operands).all { (argument, operand) ->
            argument == null || `is`(argument, resolveOperandType(operand)) != false
        }
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
