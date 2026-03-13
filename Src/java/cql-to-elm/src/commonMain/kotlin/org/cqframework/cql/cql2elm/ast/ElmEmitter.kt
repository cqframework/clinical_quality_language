package org.cqframework.cql.cql2elm.ast

import org.cqframework.cql.cql2elm.ast.ElmEmitter.UnsupportedNodeException
import org.cqframework.cql.cql2elm.frontend.SymbolTable
import org.cqframework.cql.cql2elm.frontend.TypeTable
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.QName
import org.hl7.cql.ast.AccessModifier as AstAccessModifier
import org.hl7.cql.ast.BooleanLiteral
import org.hl7.cql.ast.CodeLiteral
import org.hl7.cql.ast.ConceptLiteral
import org.hl7.cql.ast.ContextDefinition
import org.hl7.cql.ast.DateTimeLiteral
import org.hl7.cql.ast.DecimalLiteral
import org.hl7.cql.ast.Definition
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.InstanceLiteral
import org.hl7.cql.ast.IntLiteral
import org.hl7.cql.ast.IntervalLiteral
import org.hl7.cql.ast.ListLiteral
import org.hl7.cql.ast.Literal
import org.hl7.cql.ast.LiteralExpression
import org.hl7.cql.ast.LongLiteral
import org.hl7.cql.ast.NullLiteral
import org.hl7.cql.ast.ParameterDefinition
import org.hl7.cql.ast.QuantityLiteral
import org.hl7.cql.ast.RatioLiteral
import org.hl7.cql.ast.Statement
import org.hl7.cql.ast.StringLiteral
import org.hl7.cql.ast.TimeLiteral
import org.hl7.cql.ast.TupleLiteral
import org.hl7.cql.ast.UnsupportedStatement
import org.hl7.cql.ast.UsingDefinition
import org.hl7.elm.r1.AccessModifier as ElmAccessModifier
import org.hl7.elm.r1.Code
import org.hl7.elm.r1.CodeSystemRef
import org.hl7.elm.r1.Concept
import org.hl7.elm.r1.DateTime
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Instance
import org.hl7.elm.r1.InstanceElement
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.Literal as ElmLiteral
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.ParameterDef
import org.hl7.elm.r1.Quantity
import org.hl7.elm.r1.Ratio
import org.hl7.elm.r1.Time
import org.hl7.elm.r1.Tuple
import org.hl7.elm.r1.TupleElement
import org.hl7.elm.r1.UsingDef
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Converts the CQL AST into an equivalent ELM representation. The emitter focuses on structural
 * parity with the legacy parse-tree visitor and will grow to cover the full language surface area.
 */
@Suppress("UnusedPrivateProperty", "TooManyFunctions")
class ElmEmitter(
    private val symbolTable: SymbolTable = SymbolTable(),
    private val typeTable: TypeTable = TypeTable(),
) {
    private val typesNamespace = "urn:hl7-org:elm-types:r1"

    @Suppress("MemberVisibilityCanBePrivate") data class Result(val library: Library)

    class UnsupportedNodeException(message: String) : RuntimeException(message)

    fun emit(astLibrary: org.hl7.cql.ast.Library): Result {
        val elmLibrary = Library()
        elmLibrary.schemaIdentifier = defaultSchemaIdentifier()
        astLibrary.name?.let {
            elmLibrary.identifier =
                VersionedIdentifier().apply {
                    id = it.simpleName
                    if (it.parts.size > 1) {
                        system = it.parts.dropLast(1).joinToString(".")
                    }
                }
        }
        astLibrary.version?.let { version ->
            val identifier = elmLibrary.identifier ?: VersionedIdentifier()
            identifier.version = version.value
            elmLibrary.identifier = identifier
        }

        val usingDefs = emitUsings(astLibrary.definitions)
        if (usingDefs.isNotEmpty()) {
            elmLibrary.usings = Library.Usings().apply { def = usingDefs.toMutableList() }
        }

        val parameterDefs = emitParameters(astLibrary.definitions)
        if (parameterDefs.isNotEmpty()) {
            elmLibrary.parameters =
                Library.Parameters().apply { def = parameterDefs.toMutableList() }
        }

        val statementEmitter = StatementEmitter()
        statementEmitter.emit(astLibrary.statements)

        // Note: ContextDefs are not emitted here because the legacy translator only emits them
        // when models with real context types are loaded (e.g., FHIR Patient). For System-only
        // libraries, the context resolution fails silently in the legacy translator and no
        // ContextDef is added. Once model resolution is implemented, ContextDefs will be emitted.

        val expressionDefs = statementEmitter.expressions
        if (expressionDefs.isNotEmpty()) {
            elmLibrary.statements =
                Library.Statements().apply { def = expressionDefs.toMutableList() }
        }

        return Result(elmLibrary)
    }

    private fun defaultSchemaIdentifier(): VersionedIdentifier =
        VersionedIdentifier().apply {
            id = "urn:hl7-org:elm"
            version = "r1"
        }

    private fun emitUsings(definitions: List<Definition>): List<UsingDef> {
        return definitions.mapNotNull { definition ->
            when (definition) {
                is UsingDefinition -> emitUsing(definition)
                else -> null
            }
        }
    }

    private fun emitUsing(definition: UsingDefinition): UsingDef {
        val usingDef = UsingDef()
        val localId = definition.alias?.value ?: definition.modelIdentifier.simpleName
        usingDef.localIdentifier = localId
        usingDef.version = definition.version?.value
        val modelName = definition.modelIdentifier.simpleName
        usingDef.uri =
            when (modelName) {
                "System" -> typesNamespace
                else ->
                    throw UnsupportedNodeException(
                        "Model '$modelName' is not yet supported by the AST emitter."
                    )
            }
        return usingDef
    }

    private fun emitParameters(definitions: List<Definition>): List<ParameterDef> {
        return definitions.mapNotNull { definition ->
            when (definition) {
                is ParameterDefinition -> emitParameter(definition)
                else -> null
            }
        }
    }

    private fun emitParameter(definition: ParameterDefinition): ParameterDef {
        val paramDef = ParameterDef()
        paramDef.name = definition.name.value
        paramDef.accessLevel = ElmAccessModifier.PUBLIC
        definition.access?.let { access ->
            paramDef.accessLevel =
                when (access) {
                    AstAccessModifier.PUBLIC -> ElmAccessModifier.PUBLIC
                    AstAccessModifier.PRIVATE -> ElmAccessModifier.PRIVATE
                }
        }
        definition.default?.let { defaultExpr -> paramDef.default = emitExpression(defaultExpr) }
        // parameterType and parameterTypeSpecifier are set during type resolution
        // which requires model resolution - not yet supported
        return paramDef
    }

    private inner class StatementEmitter {
        val expressions = mutableListOf<ExpressionDef>()

        private var currentContext: String? = null

        fun emit(statements: List<Statement>) {
            statements.forEach { emit(it) }
        }

        private fun emit(statement: Statement) {
            when (statement) {
                is ContextDefinition -> handleContext(statement)
                is ExpressionDefinition -> expressions += emitExpressionDefinition(statement)
                is FunctionDefinition ->
                    throw UnsupportedNodeException("Function definitions are not supported yet.")
                is UnsupportedStatement ->
                    throw UnsupportedNodeException(
                        "Unsupported statement encountered: ${statement.grammarRule}"
                    )
            }
        }

        private fun handleContext(contextDefinition: ContextDefinition) {
            currentContext = contextDefinition.context.value
        }

        private fun emitExpressionDefinition(definition: ExpressionDefinition): ExpressionDef {
            val expressionDef =
                ExpressionDef().apply {
                    name = definition.name.value
                    accessLevel = ElmAccessModifier.PUBLIC
                    definition.access?.let { access ->
                        accessLevel =
                            when (access) {
                                AstAccessModifier.PUBLIC -> ElmAccessModifier.PUBLIC
                                AstAccessModifier.PRIVATE -> ElmAccessModifier.PRIVATE
                            }
                    }
                    currentContext?.let { context = it }
                    expression = emitExpression(definition.expression)
                }
            return expressionDef
        }
    }

    private fun emitExpression(expression: Expression): ElmExpression {
        return when (expression) {
            is LiteralExpression -> emitLiteral(expression.literal)
            is IdentifierExpression ->
                throw UnsupportedNodeException("Identifier expressions are not yet supported.")
            else ->
                throw UnsupportedNodeException(
                    "Expression '${expression::class.simpleName}' is not supported yet."
                )
        }
    }

    @Suppress("CyclomaticComplexMethod")
    private fun emitLiteral(literal: Literal): ElmExpression {
        return when (literal) {
            is StringLiteral ->
                ElmLiteral().withValueType(QName(typesNamespace, "String")).withValue(literal.value)
            is BooleanLiteral ->
                ElmLiteral()
                    .withValueType(QName(typesNamespace, "Boolean"))
                    .withValue(literal.value.toString())
            is IntLiteral ->
                ElmLiteral()
                    .withValueType(QName(typesNamespace, "Integer"))
                    .withValue(literal.value.toString())
            is LongLiteral ->
                ElmLiteral()
                    .withValueType(QName(typesNamespace, "Long"))
                    .withValue(literal.value.toString())
            is DecimalLiteral ->
                ElmLiteral()
                    .withValueType(QName(typesNamespace, "Decimal"))
                    .withValue(literal.value.toString())
            is NullLiteral -> Null()
            is QuantityLiteral -> emitQuantity(literal)
            is RatioLiteral -> emitRatio(literal)
            is DateTimeLiteral -> emitDateTime(literal)
            is TimeLiteral -> emitTime(literal)
            is IntervalLiteral -> emitInterval(literal)
            is ListLiteral -> emitList(literal)
            is TupleLiteral -> emitTuple(literal)
            is InstanceLiteral -> emitInstance(literal)
            is CodeLiteral -> emitCode(literal)
            is ConceptLiteral -> emitConcept(literal)
        }
    }

    private fun emitQuantity(literal: QuantityLiteral): Quantity {
        val quantity = Quantity()
        quantity.value = BigDecimal(literal.value)
        quantity.unit = literal.unit
        return quantity
    }

    private fun emitRatio(literal: RatioLiteral): Ratio {
        val ratio = Ratio()
        ratio.numerator = emitQuantity(literal.numerator)
        ratio.denominator = emitQuantity(literal.denominator)
        return ratio
    }

    @Suppress("MagicNumber", "MaxLineLength")
    private fun emitDateTime(literal: DateTimeLiteral): ElmExpression {
        val input = literal.text
        val dateTimePattern =
            Regex(
                "(\\d{4})(((-(\\d{2}))(((-(\\d{2}))((T)((\\d{2})(:(\\d{2})(:(\\d{2})(\\.(\\d+))?)?)?)?)?)|(T))?)|(T))?((Z)|(([+-])(\\d{2})(:(\\d{2}))))?"
            )
        val matcher =
            dateTimePattern.matchEntire(input)
                ?: throw UnsupportedNodeException("Invalid date/time literal: ${literal.text}")

        val result = DateTime()
        result.year = createIntLiteral(matcher.groups[1]!!.value.toInt())
        if (matcher.groups[5] != null) {
            result.month = createIntLiteral(matcher.groups[5]!!.value.toInt())
        }
        if (matcher.groups[9] != null) {
            result.day = createIntLiteral(matcher.groups[9]!!.value.toInt())
        }
        if (matcher.groups[13] != null) {
            result.hour = createIntLiteral(matcher.groups[13]!!.value.toInt())
        }
        if (matcher.groups[15] != null) {
            result.minute = createIntLiteral(matcher.groups[15]!!.value.toInt())
        }
        if (matcher.groups[17] != null) {
            result.second = createIntLiteral(matcher.groups[17]!!.value.toInt())
        }
        if (matcher.groups[19] != null) {
            result.millisecond = createIntLiteral(matcher.groups[19]!!.value.toInt())
        }
        if (matcher.groups[23] != null) {
            result.timezoneOffset = createDecimalLiteral(BigDecimal("0"))
        } else if (matcher.groups[25] != null) {
            val polarity = if (matcher.groups[25]!!.value == "+") 1 else -1
            val offsetHour = matcher.groups[26]!!.value.toInt()
            val offsetMin =
                if (matcher.groups[28] != null) matcher.groups[28]!!.value.toInt() else 0
            val offset = polarity.toDouble() * (offsetHour + offsetMin.toDouble() / 60)
            result.timezoneOffset = createDecimalLiteral(BigDecimal(offset.toString()))
        }
        return result
    }

    @Suppress("MagicNumber")
    private fun emitTime(literal: TimeLiteral): Time {
        val input = literal.text
        val timePattern = Regex("T(\\d{2})(:(\\d{2})(:(\\d{2})(\\.(\\d+))?)?)?")
        val matcher =
            timePattern.matchEntire(input)
                ?: throw UnsupportedNodeException("Invalid time literal: ${literal.text}")

        val result = Time()
        result.hour = createIntLiteral(matcher.groups[1]!!.value.toInt())
        if (matcher.groups[3] != null) {
            result.minute = createIntLiteral(matcher.groups[3]!!.value.toInt())
        }
        if (matcher.groups[5] != null) {
            result.second = createIntLiteral(matcher.groups[5]!!.value.toInt())
        }
        if (matcher.groups[7] != null) {
            result.millisecond = createIntLiteral(matcher.groups[7]!!.value.toInt())
        }
        return result
    }

    private fun emitInterval(literal: IntervalLiteral): Interval {
        val interval = Interval()
        interval.low = emitExpression(literal.lower)
        interval.high = emitExpression(literal.upper)
        interval.lowClosed = literal.lowerClosed
        interval.highClosed = literal.upperClosed
        return interval
    }

    private fun emitList(literal: ListLiteral): org.hl7.elm.r1.List {
        val list = org.hl7.elm.r1.List()
        if (literal.elements.isNotEmpty()) {
            list.element = literal.elements.map { emitExpression(it) }.toMutableList()
        }
        return list
    }

    private fun emitTuple(literal: TupleLiteral): Tuple {
        val tuple = Tuple()
        if (literal.elements.isNotEmpty()) {
            tuple.element =
                literal.elements
                    .map { elem ->
                        TupleElement().apply {
                            name = elem.name.value
                            value = emitExpression(elem.expression)
                        }
                    }
                    .toMutableList()
        }
        return tuple
    }

    private fun emitInstance(literal: InstanceLiteral): Instance {
        val instance = Instance()
        literal.type?.let { typeSpec ->
            instance.classType = QName(typesNamespace, typeSpec.name.simpleName)
        }
        if (literal.elements.isNotEmpty()) {
            instance.element =
                literal.elements
                    .map { elem ->
                        InstanceElement().apply {
                            name = elem.name.value
                            value = emitExpression(elem.expression)
                        }
                    }
                    .toMutableList()
        }
        return instance
    }

    private fun emitCode(literal: CodeLiteral): Code {
        val code = Code()
        code.code = literal.code
        code.display = literal.display
        code.system = CodeSystemRef().apply { name = literal.system.identifier.value }
        return code
    }

    private fun emitConcept(literal: ConceptLiteral): Concept {
        val concept = Concept()
        concept.display = literal.display
        if (literal.codes.isNotEmpty()) {
            concept.code = literal.codes.map { emitCode(it) }.toMutableList()
        }
        return concept
    }

    private fun createIntLiteral(value: Int): ElmLiteral {
        return ElmLiteral()
            .withValueType(QName(typesNamespace, "Integer"))
            .withValue(value.toString())
    }

    private fun createDecimalLiteral(value: BigDecimal): ElmLiteral {
        return ElmLiteral()
            .withValueType(QName(typesNamespace, "Decimal"))
            .withValue(value.toString())
    }
}
