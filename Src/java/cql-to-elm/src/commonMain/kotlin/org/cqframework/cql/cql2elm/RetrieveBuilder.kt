package org.cqframework.cql.cql2elm

import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.cqframework.cql.cql2elm.tracking.TrackBack
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.elm.IdObjectFactory
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.NamedType
import org.hl7.elm.r1.AnyInCodeSystem
import org.hl7.elm.r1.AnyInValueSet
import org.hl7.elm.r1.BinaryExpression
import org.hl7.elm.r1.Contains
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.In
import org.hl7.elm.r1.InCodeSystem
import org.hl7.elm.r1.InValueSet
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.ToConcept
import org.hl7.elm.r1.ToList

/**
 * Builds [Retrieve] ELM nodes from a parsed retrieve expression.
 *
 * Extracted from [Cql2ElmVisitor.visitRetrieve] so that the model/terminology/code-comparator
 * resolution logic lives apart from parse-tree walking.
 */
@Suppress(
    "LongParameterList",
    "LongMethod",
    "CyclomaticComplexMethod",
    "NestedBlockDepth",
    "ComplexCondition",
    "TooGenericExceptionCaught",
    "ReturnCount",
    "MaxLineLength",
)
class RetrieveBuilder(
    private val libraryBuilder: LibraryBuilder,
    private val of: IdObjectFactory,
    private val getTrackBack: (ParserRuleContext) -> TrackBack?,
) {
    /** Resolved metadata for a retrieve expression's optional context-identifier clause. */
    fun build(
        ctx: org.cqframework.cql.gen.cqlParser.RetrieveContext,
        useStrictRetrieveTyping: Boolean,
        namedType: NamedType?,
        classType: ClassType,
        codePath: String?,
        codeComparatorIn: String?,
        property: Property?,
        propertyType: DataType?,
        propertyException: Exception?,
        terminology: Expression?,
        contextExpression: Expression?,
    ): Retrieve {
        var codeComparator: String? = codeComparatorIn
        val retrieve: Retrieve =
            of.createRetrieve()
                .withDataType(libraryBuilder.dataTypeToQName(namedType as DataType?))
                .withTemplateId(classType.identifier)
                .withCodeProperty(codePath)
        if (contextExpression != null) {
            retrieve.context = contextExpression
        }
        if (terminology != null) {
            try {
                if (codeComparator == null) {
                    codeComparator = inferCodeComparator(terminology, propertyType)
                }
                if (property == null) {
                    throw (propertyException)!!
                }
                applyCodeComparator(
                    retrieve,
                    codeComparator,
                    property,
                    terminology,
                    ctx,
                    useStrictRetrieveTyping,
                )
                retrieve.codeComparator = codeComparator
                normalizeConceptListCodes(retrieve, ctx)
            } catch (e: Exception) {
                if (
                    ((libraryBuilder.isCompatibleWith("1.5") &&
                        !(terminology.resultType!!.isSubTypeOf(
                            libraryBuilder.resolveTypeName("System", "Vocabulary")!!
                        ))) ||
                        (!libraryBuilder.isCompatibleWith("1.5") &&
                            terminology.resultType !is ListType))
                ) {
                    retrieve.codes = libraryBuilder.resolveToList(terminology)
                } else {
                    retrieve.codes = terminology
                }
                retrieve.codeComparator = codeComparator
                libraryBuilder.recordParsingException(
                    CqlSemanticException(
                        "Could not resolve membership operator for terminology target of the retrieve.",
                        getTrackBack(ctx),
                        if (useStrictRetrieveTyping) CqlCompilerException.ErrorSeverity.Error
                        else CqlCompilerException.ErrorSeverity.Warning,
                        e,
                    )
                )
            }
        }
        return retrieve
    }

    private fun inferCodeComparator(terminology: Expression, propertyType: DataType?): String {
        if (terminology.resultType is ListType) return "in"
        if (!libraryBuilder.isCompatibleWith("1.5")) return "~"
        val vocab = libraryBuilder.resolveTypeName("System", "Vocabulary")!!
        return if (propertyType != null && propertyType.isSubTypeOf(vocab)) {
            if (terminology.resultType!!.isSubTypeOf(vocab)) "~" else "contains"
        } else {
            if (terminology.resultType!!.isSubTypeOf(vocab)) "in" else "~"
        }
    }

    private fun applyCodeComparator(
        retrieve: Retrieve,
        codeComparator: String?,
        property: Property,
        terminology: Expression,
        ctx: org.cqframework.cql.gen.cqlParser.RetrieveContext,
        useStrictRetrieveTyping: Boolean,
    ) {
        when (codeComparator) {
            "in" -> applyIn(retrieve, property, terminology, ctx, useStrictRetrieveTyping)
            "contains" ->
                applyContains(retrieve, property, terminology, ctx, useStrictRetrieveTyping)
            "~" -> applyEquivalent(retrieve, property, terminology)
            "=" -> applyEqual(retrieve, property, terminology)
            else ->
                libraryBuilder.recordParsingException(
                    CqlSemanticException(
                        "Unknown code comparator $codeComparator in retrieve",
                        getTrackBack(ctx.codeComparator()!!),
                        if (useStrictRetrieveTyping) CqlCompilerException.ErrorSeverity.Error
                        else CqlCompilerException.ErrorSeverity.Warning,
                    )
                )
        }
    }

    private fun applyIn(
        retrieve: Retrieve,
        property: Property,
        terminology: Expression,
        ctx: org.cqframework.cql.gen.cqlParser.RetrieveContext,
        useStrictRetrieveTyping: Boolean,
    ) {
        when (val inExpression: Expression = libraryBuilder.resolveIn(property, terminology)) {
            is In -> retrieve.codes = inExpression.operand[1]
            is InValueSet -> retrieve.codes = inExpression.valueset
            is InCodeSystem -> retrieve.codes = inExpression.codesystem
            is AnyInValueSet -> retrieve.codes = inExpression.valueset
            is AnyInCodeSystem -> retrieve.codes = inExpression.codesystem
            else ->
                libraryBuilder.recordParsingException(
                    CqlSemanticException(
                        "Unexpected membership operator ${inExpression::class.simpleName} in retrieve",
                        getTrackBack(ctx),
                        if (useStrictRetrieveTyping) CqlCompilerException.ErrorSeverity.Error
                        else CqlCompilerException.ErrorSeverity.Warning,
                    )
                )
        }
    }

    private fun applyContains(
        retrieve: Retrieve,
        property: Property,
        terminology: Expression,
        ctx: org.cqframework.cql.gen.cqlParser.RetrieveContext,
        useStrictRetrieveTyping: Boolean,
    ) {
        val contains: Expression = libraryBuilder.resolveContains(property, terminology)
        if (contains is Contains) {
            retrieve.codes = contains.operand[1]
        }
        libraryBuilder.recordParsingException(
            CqlSemanticException(
                "Terminology resolution using contains is not supported at this time. Use a where clause with an in operator instead.",
                getTrackBack(ctx),
                if (useStrictRetrieveTyping) CqlCompilerException.ErrorSeverity.Error
                else CqlCompilerException.ErrorSeverity.Warning,
            )
        )
    }

    private fun applyEquivalent(retrieve: Retrieve, property: Property, terminology: Expression) {
        val equivalent: BinaryExpression =
            of.createEquivalent().withOperand(listOf(property, terminology))
        libraryBuilder.resolveBinaryCall("System", "Equivalent", equivalent)
        retrieve.codes = promoteToList(equivalent.operand[1])
    }

    private fun applyEqual(retrieve: Retrieve, property: Property, terminology: Expression) {
        val equal: BinaryExpression = of.createEqual().withOperand(listOf(property, terminology))
        libraryBuilder.resolveBinaryCall("System", "Equal", equal)
        retrieve.codes = promoteToList(equal.operand[1])
    }

    private fun promoteToList(operand: Expression): Expression {
        val isList = operand.resultType is ListType
        val isVocab =
            libraryBuilder.isCompatibleWith("1.5") &&
                operand.resultType!!.isSubTypeOf(
                    libraryBuilder.resolveTypeName("System", "Vocabulary")!!
                )
        return if (!isList && !isVocab) libraryBuilder.resolveToList(operand) else operand
    }

    private fun normalizeConceptListCodes(
        retrieve: Retrieve,
        ctx: org.cqframework.cql.gen.cqlParser.RetrieveContext,
    ) {
        val codes = retrieve.codes ?: return
        val codesType = codes.resultType ?: return
        if (codesType !is ListType) return
        if (codesType.elementType != libraryBuilder.resolveTypeName("System", "Concept")) return
        if (codes is ToList) {
            if (codes.operand is ToConcept) {
                codes.operand = (codes.operand as ToConcept).operand
            } else {
                retrieve.codes =
                    libraryBuilder.buildProperty(
                        codes.operand,
                        "codes",
                        false,
                        codes.operand!!.resultType,
                    )
            }
        } else {
            libraryBuilder.recordParsingException(
                CqlSemanticException(
                    "Terminology target is a list of concepts, but expects a list of codes",
                    getTrackBack(ctx),
                    CqlCompilerException.ErrorSeverity.Warning,
                )
            )
        }
    }
}
