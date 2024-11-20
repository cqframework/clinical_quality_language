package org.cqframework.cql.cql2elm.preprocessor

import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.tree.ParseTree
import org.cqframework.cql.cql2elm.ResultWithPossibleError
import org.cqframework.cql.gen.cqlParser.ContextDefinitionContext
import org.cqframework.cql.gen.cqlParser.LibraryDefinitionContext
import org.hl7.elm.r1.OperandDef

@Suppress("TooManyFunctions")
class LibraryInfo : BaseInfo() {
    var namespaceName: String? = null
    @JvmField var libraryName: String? = null
    var version: String? = null
    var defaultUsingDefinition: UsingDefinitionInfo? = null
        private set

    private val usingDefinitions: MutableMap<String?, UsingDefinitionInfo>
    private val includeDefinitions: MutableMap<String?, IncludeDefinitionInfo>
    private val codesystemDefinitions: MutableMap<String?, CodesystemDefinitionInfo>
    private val valuesetDefinitions: MutableMap<String?, ValuesetDefinitionInfo>
    private val codeDefinitions: MutableMap<String?, CodeDefinitionInfo>
    private val conceptDefinitions: MutableMap<String?, ConceptDefinitionInfo>
    private val parameterDefinitions: MutableMap<String?, ParameterDefinitionInfo>
    private val expressionDefinitions: MutableMap<String?, ExpressionDefinitionInfo>
    private val functionDefinitions: MutableMap<String?, MutableList<FunctionDefinitionInfo>>
    private val contextDefinitions: MutableList<ContextDefinitionInfo>
    private val definitions: MutableMap<Interval, BaseInfo>

    init {
        usingDefinitions = LinkedHashMap()
        includeDefinitions = LinkedHashMap()
        codesystemDefinitions = LinkedHashMap()
        valuesetDefinitions = LinkedHashMap()
        codeDefinitions = LinkedHashMap()
        conceptDefinitions = LinkedHashMap()
        parameterDefinitions = LinkedHashMap()
        expressionDefinitions = LinkedHashMap()
        functionDefinitions = LinkedHashMap()
        contextDefinitions = ArrayList()
        definitions = HashMap()
    }

    fun withLibraryName(value: String?): LibraryInfo {
        libraryName = value
        return this
    }

    fun withVersion(value: String?): LibraryInfo {
        version = value
        return this
    }

    private fun addDefinition(definition: BaseInfo?) {
        if (definition != null && definition.definition != null) {
            val sourceInterval = definition.definition?.sourceInterval
            if (sourceInterval != null) {
                definitions[sourceInterval] = definition
            }
        }
    }

    override var definition: LibraryDefinitionContext? = null

    fun withDefinition(value: LibraryDefinitionContext?): LibraryInfo {
        this.definition = value
        return this
    }

    fun addUsingDefinition(usingDefinition: UsingDefinitionInfo) {
        // First using definition encountered is "preferred", meaning it will resolve as the default
        // model info
        if (defaultUsingDefinition == null) {
            defaultUsingDefinition = usingDefinition
        }
        usingDefinitions[usingDefinition.name] = usingDefinition
        addDefinition(usingDefinition)
    }

    fun resolveModelReference(identifier: String?): UsingDefinitionInfo? {
        return usingDefinitions[identifier]
    }

    val defaultModelName: String?
        get() {
            val usingDefinitionInfo =
                defaultUsingDefinition
                    ?: throw IllegalArgumentException(
                        "Could not determine a default model because no usings have been defined."
                    )
            return usingDefinitionInfo.name
        }

    fun addIncludeDefinition(includeDefinition: IncludeDefinitionInfo) {
        includeDefinitions[includeDefinition.localName] = includeDefinition
        addDefinition(includeDefinition)
    }

    fun resolveLibraryReference(identifier: String?): IncludeDefinitionInfo? {
        return includeDefinitions[identifier]
    }

    fun resolveLibraryName(identifier: String?): String? {
        val includeDefinition = resolveLibraryReference(identifier)
        return includeDefinition?.localName
    }

    fun addParameterDefinition(parameterDefinition: ParameterDefinitionInfo) {
        parameterDefinitions[parameterDefinition.name] = parameterDefinition
        addDefinition(parameterDefinition)
    }

    fun resolveParameterReference(identifier: String?): ParameterDefinitionInfo? {
        return parameterDefinitions[identifier]
    }

    fun resolveParameterName(identifier: String?): String? {
        val parameterDefinition = resolveParameterReference(identifier)
        return parameterDefinition?.name
    }

    fun addCodesystemDefinition(codesystemDefinition: CodesystemDefinitionInfo) {
        codesystemDefinitions[codesystemDefinition.name] = codesystemDefinition
        addDefinition(codesystemDefinition)
    }

    fun resolveCodesystemReference(identifier: String?): CodesystemDefinitionInfo? {
        return codesystemDefinitions[identifier]
    }

    fun addValuesetDefinition(valuesetDefinition: ValuesetDefinitionInfo) {
        valuesetDefinitions[valuesetDefinition.name] = valuesetDefinition
        addDefinition(valuesetDefinition)
    }

    fun resolveValuesetReference(identifier: String?): ValuesetDefinitionInfo? {
        return valuesetDefinitions[identifier]
    }

    fun resolveValuesetName(identifier: String?): String? {
        val valuesetDefinition = resolveValuesetReference(identifier)
        return valuesetDefinition?.name
    }

    fun addCodeDefinition(codeDefinition: CodeDefinitionInfo) {
        codeDefinitions[codeDefinition.name] = codeDefinition
        addDefinition(codeDefinition)
    }

    fun resolveCodeReference(identifier: String?): CodeDefinitionInfo? {
        return codeDefinitions[identifier]
    }

    fun addConceptDefinition(conceptDefinition: ConceptDefinitionInfo) {
        conceptDefinitions[conceptDefinition.name] = conceptDefinition
        addDefinition(conceptDefinition)
    }

    fun resolveConceptReference(identifier: String?): ConceptDefinitionInfo? {
        return conceptDefinitions[identifier]
    }

    fun addExpressionDefinition(letStatement: ExpressionDefinitionInfo) {
        expressionDefinitions[letStatement.name] = letStatement
        addDefinition(letStatement)
    }

    fun resolveExpressionReference(identifier: String?): ExpressionDefinitionInfo? {
        return expressionDefinitions[identifier]
    }

    fun resolveExpressionName(identifier: String?): String? {
        val letStatement = resolveExpressionReference(identifier)
        return letStatement?.name
    }

    fun addFunctionDefinition(functionDefinition: FunctionDefinitionInfo) {
        var infos = functionDefinitions[functionDefinition.name]
        if (infos == null) {
            infos = ArrayList()
            functionDefinitions[functionDefinition.name] = infos
        }
        infos.add(functionDefinition)
        addDefinition(functionDefinition)
    }

    fun resolveFunctionReference(identifier: String?): Iterable<FunctionDefinitionInfo>? {
        return functionDefinitions[identifier]
    }

    fun resolveFunctionName(identifier: String?): String? {
        val functionDefinitions = resolveFunctionReference(identifier)
        if (functionDefinitions != null) {
            for (functionInfo in functionDefinitions) {
                return functionInfo.name
            }
        }
        return null
    }

    fun addContextDefinition(contextDefinition: ContextDefinitionInfo) {
        contextDefinitions.add(contextDefinition)
        addDefinition(contextDefinition)
    }

    fun resolveContext(ctx: ContextDefinitionContext): ContextDefinitionInfo? {
        for (cd in contextDefinitions) {
            if (ctx.sourceInterval == cd.definition?.sourceInterval) {
                return cd
            }
        }
        return null
    }

    fun resolveDefinition(pt: ParseTree): BaseInfo? {
        return definitions[pt.sourceInterval]
    }

    companion object {
        @Suppress("UnusedPrivateMember")
        private fun isFunctionDefInfoAlreadyPresent(
            existingFunctionDefInfo: ResultWithPossibleError<FunctionDefinitionInfo>,
            functionDefinition: ResultWithPossibleError<FunctionDefinitionInfo>
        ): Boolean {
            // equals/hashCode only goes so far because we don't control the entire class hierarchy
            return matchesFunctionDefInfos(existingFunctionDefInfo, functionDefinition)
        }

        @Suppress("ReturnCount")
        private fun matchesFunctionDefInfos(
            existingInfo: ResultWithPossibleError<FunctionDefinitionInfo>?,
            newInfo: ResultWithPossibleError<FunctionDefinitionInfo>
        ): Boolean {
            if (existingInfo == null) {
                return false
            }
            if (existingInfo.hasError() || newInfo.hasError()) {
                return existingInfo.hasError() && newInfo.hasError()
            }
            val existingOperands =
                existingInfo.underlyingResultIfExists.preCompileOutput!!.functionDef.operand
            val newOperands =
                newInfo.underlyingResultIfExists.preCompileOutput!!.functionDef.operand
            if (existingOperands.size != newOperands.size) {
                return false
            }
            for (index in existingOperands.indices) {
                val existingOperand = existingOperands[index]
                val newOperand = newOperands[index]
                if (!matchesOperands(existingOperand, newOperand)) {
                    return false
                }
            }
            return true
        }

        private fun matchesOperands(existingOperand: OperandDef, newOperand: OperandDef): Boolean {
            return existingOperand.resultType == newOperand.resultType
        }
    }
}
