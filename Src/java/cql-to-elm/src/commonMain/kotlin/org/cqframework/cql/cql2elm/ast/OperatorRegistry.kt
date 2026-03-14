package org.cqframework.cql.cql2elm.ast

import org.cqframework.cql.cql2elm.ModelResolver
import org.cqframework.cql.cql2elm.TypeBuilder
import org.cqframework.cql.cql2elm.model.CallContext
import org.cqframework.cql.cql2elm.model.Conversion
import org.cqframework.cql.cql2elm.model.ConversionMap
import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.cql2elm.model.OperatorMap
import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.cqframework.cql.cql2elm.model.SystemLibraryHelper
import org.cqframework.cql.cql2elm.model.SystemModel
import org.cqframework.cql.elm.IdObjectFactory
import org.hl7.cql.model.DataType
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.SystemModelInfoProvider

/**
 * Operator resolution backed by the existing [OperatorMap], [ConversionMap], and
 * [SystemLibraryHelper] infrastructure. This ensures we use the canonical type instances from the
 * [SystemModel] and the full operator/conversion resolution logic from the legacy translator.
 */
class OperatorRegistry(
    val systemModel: SystemModel,
    val systemOperators: OperatorMap,
    val conversionMap: ConversionMap,
    val typeBuilder: TypeBuilder,
) {
    /**
     * Resolve an operator by name and operand types, returning the full [OperatorResolution] which
     * includes the matched [org.cqframework.cql.cql2elm.model.Operator], any needed conversions,
     * and the result type.
     */
    fun resolve(
        name: String,
        operandTypes: List<DataType>,
        allowPromotionAndDemotion: Boolean = false,
    ): OperatorResolution? {
        val callContext =
            CallContext(
                "System",
                name,
                allowPromotionAndDemotion = allowPromotionAndDemotion,
                allowFluent = false,
                mustResolve = false,
                operandTypes = operandTypes,
            )
        return systemOperators.resolveOperator(callContext, conversionMap)
    }

    /** Look up the system type by simple name (e.g., "Integer", "Decimal"). */
    fun type(name: String): DataType =
        systemModel.resolveTypeName(name)
            ?: throw IllegalArgumentException("Unknown system type: $name")

    /**
     * Find the conversion operator name for a given [Conversion], if any. Returns the operator name
     * (e.g., "ToDecimal", "ToLong") or null if the conversion has no associated operator.
     */
    fun conversionOperatorName(conversion: Conversion): String? = conversion.operator?.name

    companion object {
        /**
         * Create an OperatorRegistry pre-loaded with the System library operators and conversions.
         * This bootstraps the same way the legacy translator does via [SystemLibraryHelper.load].
         */
        fun createSystemRegistry(): OperatorRegistry {
            // Build a SystemModel from the system model info
            val systemModelInfoProvider = SystemModelInfoProvider()
            val systemModelInfo =
                checkNotNull(systemModelInfoProvider.load(ModelIdentifier(id = "System"))) {
                    "Could not load System model info"
                }
            val systemModel = SystemModel(systemModelInfo)

            // Create a TypeBuilder with a simple ModelResolver that returns the system model
            val objectFactory = IdObjectFactory()
            val modelResolver =
                object : ModelResolver {
                    override fun getModel(modelName: String): Model {
                        if (modelName == "System") return systemModel
                        throw IllegalArgumentException("Model '$modelName' is not supported")
                    }
                }
            val typeBuilder = TypeBuilder(objectFactory, modelResolver)

            // Use SystemLibraryHelper to populate a CompiledLibrary with all system operators
            val systemLibrary = SystemLibraryHelper.load(systemModel, typeBuilder)

            // Extract the ConversionMap from the system library's conversions
            val conversionMap = ConversionMap()
            for (conversion in systemLibrary.getConversions()) {
                conversionMap.add(conversion)
            }

            return OperatorRegistry(
                systemModel = systemModel,
                systemOperators = systemLibrary.operatorMap,
                conversionMap = conversionMap,
                typeBuilder = typeBuilder,
            )
        }
    }
}
