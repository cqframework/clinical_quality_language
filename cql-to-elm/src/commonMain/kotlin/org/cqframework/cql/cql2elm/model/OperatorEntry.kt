package org.cqframework.cql.cql2elm.model

import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.DataType

class OperatorEntry(val name: String) {

    private val signatures = SignatureNodes()
    private val genericOperators: MutableMap<Signature, GenericOperator> = HashMap()

    init {
        require(name.isNotEmpty()) { "name is empty" }
    }

    private class SignatureNode(val operator: Operator) {
        val signature: Signature
            get() = operator.signature

        /*
        The invocation signature is the call signature with arguments of type Any set to the operand types
         */
        private fun getInvocationSignature(
            callSignature: Signature,
            operatorSignature: Signature,
        ): Signature {
            if (callSignature.size == operatorSignature.size) {
                val invocationTypes = mutableListOf<DataType>()
                val callTypes = callSignature.operandTypes
                val operatorTypes = operatorSignature.operandTypes
                var isResolved = false
                for (i in callTypes.indices) {
                    val callType = callTypes[i]
                    val operatorType = operatorTypes[i]
                    if (callType == DataType.ANY && operatorType != DataType.ANY) {
                        isResolved = true
                        invocationTypes.add(operatorType)
                    } else {
                        invocationTypes.add(callType)
                    }
                }
                if (isResolved) {
                    return Signature(invocationTypes)
                }
            }
            return callSignature
        }

        @Suppress("LongParameterList", "UnusedParameter")
        private fun getOperatorResolution(
            operator: Operator,
            callSignature: Signature,
            invocationSignature: Signature,
            conversionMap: ConversionMap,
            operatorMap: OperatorMap,
            allowPromotionAndDemotion: Boolean,
            requireConversions: Boolean,
        ): OperatorResolution? {
            val conversions =
                getConversions(
                    callSignature,
                    operator.signature,
                    conversionMap,
                    operatorMap,
                    allowPromotionAndDemotion,
                )
            if (requireConversions && conversions == null) {
                return null
            }

            val result = OperatorResolution(operator, conversions)
            return result
        }

        @Suppress("ReturnCount")
        fun resolve(
            callContext: CallContext,
            conversionMap: ConversionMap,
            operatorMap: OperatorMap,
        ): List<OperatorResolution> {
            var results: MutableList<OperatorResolution> = ArrayList()
            val invocationSignature =
                getInvocationSignature(callContext.signature, operator.signature)

            // Attempt exact match against this signature
            if (operator.signature == invocationSignature) {
                val result =
                    getOperatorResolution(
                        operator,
                        callContext.signature,
                        invocationSignature,
                        conversionMap,
                        operatorMap,
                        callContext.allowPromotionAndDemotion,
                        false,
                    )
                if (result != null) {
                    results.add(result)
                    return results
                }
            }

            // Attempt to resolve against sub signatures
            results = subSignatures.resolve(callContext, conversionMap, operatorMap)

            // If no subsignatures match, attempt subType match against this signature
            if (results.isEmpty() && operator.signature.isSuperTypeOf(invocationSignature)) {
                val result =
                    getOperatorResolution(
                        operator,
                        callContext.signature,
                        invocationSignature,
                        conversionMap,
                        operatorMap,
                        callContext.allowPromotionAndDemotion,
                        false,
                    )
                if (result != null) {
                    results.add(result)
                    return results
                }
            }

            if (results.isEmpty()) {
                // Attempt to find a conversion path from the call signature to the target signature
                val result =
                    getOperatorResolution(
                        operator,
                        callContext.signature,
                        invocationSignature,
                        conversionMap,
                        operatorMap,
                        callContext.allowPromotionAndDemotion,
                        true,
                    )
                if (result != null) {
                    results.add(result)
                }
            }

            return results
        }

        @Suppress("ReturnCount")
        private fun getConversions(
            callSignature: Signature?,
            operatorSignature: Signature?,
            conversionMap: ConversionMap,
            operatorMap: OperatorMap,
            allowPromotionAndDemotion: Boolean,
        ): Array<Conversion?>? {
            if (
                callSignature == null ||
                    operatorSignature == null ||
                    callSignature.size != operatorSignature.size
            ) {
                return null
            }

            val conversions = arrayOfNulls<Conversion>(callSignature.size)
            val isConvertible =
                callSignature.isConvertibleTo(
                    operatorSignature,
                    conversionMap,
                    operatorMap,
                    allowPromotionAndDemotion,
                    conversions,
                )

            if (isConvertible) {
                return conversions
            }

            return null
        }

        val subSignatures: SignatureNodes = SignatureNodes()

        fun hasSubSignatures(): Boolean {
            return subSignatures.hasSignatures()
        }

        override fun hashCode(): Int {
            return operator.signature.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (other is SignatureNode) {
                return operator.name == other.operator.name && (this.signature == other.signature)
            }

            return false
        }

        override fun toString(): String {
            return operator.toString()
        }
    }

    private class SignatureNodes {
        private val signatures: MutableMap<Signature, SignatureNode> = HashMap()

        fun hasSignatures(): Boolean {
            return signatures.isNotEmpty()
        }

        fun contains(operator: Operator?): Boolean {
            var result = signatures.containsKey(operator!!.signature)
            if (!result) {
                for (n in signatures.values) {
                    result = n.subSignatures.contains(operator)
                    if (result) {
                        break
                    }
                }
            }

            return result
        }

        fun add(node: SignatureNode?) {
            requireNotNull(node) { "node is null." }

            require(!signatures.containsKey(node.signature)) {
                "Operator ${node.operator.name} already has a registration for signature: ${node.signature}."
            }

            var added = false
            for (n in signatures.values) {
                if (n.signature.isSuperTypeOf(node.signature)) {
                    n.subSignatures.add(node)
                    added = true
                    break
                }
            }

            if (!added) {
                for (n in signatures.values.toTypedArray<SignatureNode>()) {
                    if (node.signature.isSuperTypeOf(n.signature)) {
                        signatures.remove(n.signature)
                        node.subSignatures.add(n)
                    }
                }

                signatures[node.signature] = node
            }
        }

        fun resolve(
            callContext: CallContext,
            conversionMap: ConversionMap,
            operatorMap: OperatorMap,
        ): MutableList<OperatorResolution> {
            val results = arrayListOf<OperatorResolution>()

            var signatureCount = 0
            for (n in signatures.values) {
                if (n.signature.size == callContext.signature.size) {
                    signatureCount++

                    // Any subSignature will count as an overload
                    if (n.hasSubSignatures()) {
                        signatureCount++
                    }
                }

                val nodeResults = n.resolve(callContext, conversionMap, operatorMap)
                results.addAll(nodeResults)
            }

            if (signatureCount > 1) {
                for (result in results) {
                    result.operatorHasOverloads = true
                }
            }

            return results
        }
    }

    fun containsOperator(operator: Operator?): Boolean {
        return if (operator is GenericOperator) {
            containsGenericOperator(operator)
        } else {
            signatures.contains(operator)
        }
    }

    fun addOperator(operator: Operator) {
        if (operator is GenericOperator) {
            addGenericOperator(operator)
        } else {
            signatures.add(SignatureNode(operator))
        }
    }

    private fun containsGenericOperator(operator: GenericOperator): Boolean {
        return genericOperators.containsKey(operator.signature)
    }

    private fun addGenericOperator(operator: GenericOperator) {
        require(!genericOperators.containsKey(operator.signature)) {
            "Operator $name already has a generic registration for signature: ${operator.signature}."
        }

        genericOperators[operator.signature] = operator
    }

    @Suppress("NestedBlockDepth")
    private fun expandChoices(callSignature: Signature): List<Signature> {
        val signatures = ArrayList<Signature>()
        if (callSignature.containsChoices) {
            val operandList = ArrayList<ArrayList<DataType>>()
            for (operand in callSignature.operandTypes) {
                val list = ArrayList<DataType>()
                if (operand is ChoiceType) {
                    for (type in operand.types) {
                        list.add(type)
                    }
                } else {
                    list.add(operand)
                }
                operandList.add(list)
            }

            val result = arrayOfNulls<DataType>(callSignature.size)
            collectSignatures(operandList, result, 0, signatures)
        } else {
            signatures.add(callSignature)
        }
        return signatures
    }

    private fun collectSignatures(
        operandList: ArrayList<ArrayList<DataType>>,
        result: Array<DataType?>,
        k: Int,
        signatures: MutableList<Signature>,
    ) {
        if (k == operandList.size) {
            val noNulls = result.toList().requireNoNulls()
            signatures.add(Signature(noNulls))
        } else {
            for (j in operandList[k].indices) {
                result[k] = operandList[k][j]
                collectSignatures(operandList, result, k + 1, signatures)
            }
        }
    }

    fun resolve(
        callContext: CallContext,
        operatorMap: OperatorMap,
        conversionMap: ConversionMap,
    ): List<OperatorResolution> {
        // Attempt to instantiate any generic signatures
        // If the callContext signature contains choices, attempt instantiation with all possible
        // combinations of
        // the call signature (ouch, this could really hurt...)
        val callSignatures = expandChoices(callContext.signature)
        for (callSignature in callSignatures) {
            val instantiations =
                instantiate(
                    callSignature,
                    operatorMap,
                    conversionMap,
                    callContext.allowPromotionAndDemotion,
                )
            for (instantiation in instantiations) {
                // If the generic signature was instantiated, store it as an actual signature.
                if (!signatures.contains(instantiation)) {
                    signatures.add(SignatureNode(instantiation))
                }
            }
        }

        return signatures.resolve(callContext, conversionMap, operatorMap)
    }

    private fun instantiate(
        signature: Signature,
        operatorMap: OperatorMap,
        conversionMap: ConversionMap,
        allowPromotionAndDemotion: Boolean,
    ): List<Operator> {
        return genericOperators.values
            .map {
                it.instantiate(signature, operatorMap, conversionMap, allowPromotionAndDemotion)
            }
            .mapNotNull { it.operator }
    }
}
