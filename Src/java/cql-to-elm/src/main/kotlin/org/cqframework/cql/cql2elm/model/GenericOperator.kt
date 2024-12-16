package org.cqframework.cql.cql2elm.model

import java.util.*
import org.hl7.cql.model.DataType
import org.hl7.cql.model.TypeParameter

class GenericOperator(
    name: String,
    signature: Signature,
    resultType: DataType?,
    private val typeParameters: List<TypeParameter>
) : Operator(name, signature, resultType) {
    constructor(
        name: String,
        signature: Signature,
        resultType: DataType?,
        vararg typeParameters: TypeParameter,
    ) : this(name, signature, resultType, typeParameters.toList())

    fun instantiate(
        callSignature: Signature,
        operatorMap: OperatorMap,
        conversionMap: ConversionMap,
        allowPromotionAndDemotion: Boolean
    ): InstantiationResult {
        return instantiate(
            callSignature,
            null,
            operatorMap,
            conversionMap,
            allowPromotionAndDemotion
        )
    }

    @Suppress("UnusedParameter")
    private fun instantiate(
        callSignature: Signature,
        parameters: Map<TypeParameter, DataType?>?,
        operatorMap: OperatorMap,
        conversionMap: ConversionMap,
        allowPromotionAndDemotion: Boolean
    ): InstantiationResult {
        val typeMap: MutableMap<TypeParameter, DataType?> = HashMap()

        for (p in typeParameters) {
            typeMap[p] = null
        }

        if (parameters != null) {
            typeMap.putAll(parameters)
        }

        val context =
            InstantiationContextImpl(typeMap, operatorMap, conversionMap, allowPromotionAndDemotion)

        val instantiable = signature.isInstantiable(callSignature, context)
        if (instantiable) {
            val result =
                Operator(name, signature.instantiate(context), resultType!!.instantiate(context))
            result.accessLevel = accessLevel
            result.libraryName = libraryName
            return InstantiationResult(this, result, context.conversionScore)
        }

        return InstantiationResult(this, null, context.conversionScore)
    }
}
