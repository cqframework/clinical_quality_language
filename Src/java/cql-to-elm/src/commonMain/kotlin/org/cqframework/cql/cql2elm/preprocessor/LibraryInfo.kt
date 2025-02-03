package org.cqframework.cql.cql2elm.preprocessor

import org.antlr.v4.kotlinruntime.misc.Interval
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.cqframework.cql.gen.cqlParser.ContextDefinitionContext
import org.cqframework.cql.gen.cqlParser.LibraryDefinitionContext

@Suppress("TooManyFunctions")
class LibraryInfo(
    val namespaceName: String? = null,
    val libraryName: String? = null,
    val version: String? = null,
    override val definition: LibraryDefinitionContext? = null
) : BaseInfo(definition) {

    var defaultUsingDefinition: UsingDefinitionInfo? = null

    private val usingDefinitions = LinkedHashMap<String, UsingDefinitionInfo>()
    private val includeDefinitions = LinkedHashMap<String, IncludeDefinitionInfo>()
    private val codesystemDefinitions = LinkedHashMap<String, CodesystemDefinitionInfo>()
    private val valuesetDefinitions = LinkedHashMap<String, ValuesetDefinitionInfo>()
    private val codeDefinitions = LinkedHashMap<String, CodeDefinitionInfo>()
    private val conceptDefinitions = LinkedHashMap<String, ConceptDefinitionInfo>()
    private val parameterDefinitions = LinkedHashMap<String, ParameterDefinitionInfo>()
    private val expressionDefinitions = LinkedHashMap<String, ExpressionDefinitionInfo>()
    private val functionDefinitions = LinkedHashMap<String, MutableList<FunctionDefinitionInfo>>()
    private val contextDefinitions = ArrayList<ContextDefinitionInfo>()
    private val definitions = HashMap<Interval, BaseInfo>()

    private fun addDefinition(definition: BaseInfo) {
        if (definition.definition != null) {
            val sourceInterval = definition.definition?.sourceInterval
            if (sourceInterval != null) {
                definitions[sourceInterval] = definition
            }
        }
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

    fun resolveModelReference(identifier: String): UsingDefinitionInfo? {
        return usingDefinitions[identifier]
    }

    val defaultModelName: String
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

    fun resolveLibraryReference(identifier: String): IncludeDefinitionInfo? {
        return includeDefinitions[identifier]
    }

    fun resolveLibraryName(identifier: String): String? {
        val includeDefinition = resolveLibraryReference(identifier)
        return includeDefinition?.localName
    }

    fun addParameterDefinition(parameterDefinition: ParameterDefinitionInfo) {
        parameterDefinitions[parameterDefinition.name] = parameterDefinition
        addDefinition(parameterDefinition)
    }

    fun resolveParameterReference(identifier: String): ParameterDefinitionInfo? {
        return parameterDefinitions[identifier]
    }

    fun resolveParameterName(identifier: String): String? {
        val parameterDefinition = resolveParameterReference(identifier)
        return parameterDefinition?.name
    }

    fun addCodesystemDefinition(codesystemDefinition: CodesystemDefinitionInfo) {
        codesystemDefinitions[codesystemDefinition.name] = codesystemDefinition
        addDefinition(codesystemDefinition)
    }

    fun resolveCodesystemReference(identifier: String): CodesystemDefinitionInfo? {
        return codesystemDefinitions[identifier]
    }

    fun addValuesetDefinition(valuesetDefinition: ValuesetDefinitionInfo) {
        valuesetDefinitions[valuesetDefinition.name] = valuesetDefinition
        addDefinition(valuesetDefinition)
    }

    fun resolveValuesetReference(identifier: String): ValuesetDefinitionInfo? {
        return valuesetDefinitions[identifier]
    }

    fun resolveValuesetName(identifier: String): String? {
        val valuesetDefinition = resolveValuesetReference(identifier)
        return valuesetDefinition?.name
    }

    fun addCodeDefinition(codeDefinition: CodeDefinitionInfo) {
        codeDefinitions[codeDefinition.name] = codeDefinition
        addDefinition(codeDefinition)
    }

    fun resolveCodeReference(identifier: String): CodeDefinitionInfo? {
        return codeDefinitions[identifier]
    }

    fun addConceptDefinition(conceptDefinition: ConceptDefinitionInfo) {
        conceptDefinitions[conceptDefinition.name] = conceptDefinition
        addDefinition(conceptDefinition)
    }

    fun resolveConceptReference(identifier: String): ConceptDefinitionInfo? {
        return conceptDefinitions[identifier]
    }

    fun addExpressionDefinition(letStatement: ExpressionDefinitionInfo) {
        expressionDefinitions[letStatement.name] = letStatement
        addDefinition(letStatement)
    }

    fun resolveExpressionReference(identifier: String): ExpressionDefinitionInfo? {
        return expressionDefinitions[identifier]
    }

    fun resolveExpressionName(identifier: String): String? {
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

    fun resolveFunctionReference(identifier: String): List<FunctionDefinitionInfo>? {
        return functionDefinitions[identifier]
    }

    fun resolveFunctionName(identifier: String): String? {
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
            if (ctx.sourceInterval == cd.definition.sourceInterval) {
                return cd
            }
        }
        return null
    }

    fun resolveDefinition(pt: ParseTree): BaseInfo? {
        return definitions[pt.sourceInterval]
    }
}
