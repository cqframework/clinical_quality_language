package org.cqframework.cql.elm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LibraryMapper {

    public static final LibraryMapper INSTANCE = Mappers.getMapper(LibraryMapper.class);

    default org.cqframework.cql.elm.execution.ExpressionDef map(org.hl7.elm.r1.ExpressionDef element) {
        if (element == null) {
            return null;
        }

        if (element instanceof org.hl7.elm.r1.FunctionDef) {
            return map((org.hl7.elm.r1.FunctionDef) element);
        } else {
            return mapToEvaluator(element);
        }
    }

    @ExcludeFromMapping
    org.cqframework.cql.elm.execution.ExpressionDef mapToEvaluator(
            org.hl7.elm.r1.ExpressionDef element);

    @Named("ExpressionToCodeSystemRef")
    default org.cqframework.cql.elm.execution.CodeSystemRef mapCodeSystemRef(org.hl7.elm.r1.Expression expression) {
        if (expression == null) {
            return null;
        }

        if (expression instanceof org.hl7.elm.r1.CodeSystemRef) {
            return map((org.hl7.elm.r1.CodeSystemRef) expression);
        }

        throw new IllegalArgumentException(
                "unable to map type: " + expression.getClass().getName() + " to CodeSystemRef");
    }

    @Named("ExpressionToValueSetRef")
    default org.cqframework.cql.elm.execution.ValueSetRef mapValueSetRef(org.hl7.elm.r1.Expression expression) {
        if (expression == null) {
            return null;
        }

        if (expression instanceof org.hl7.elm.r1.ValueSetRef) {
            return map((org.hl7.elm.r1.ValueSetRef) expression);
        }

        throw new IllegalArgumentException(
                "unable to map type: " + expression.getClass().getName() + " to ValueSetRef");
    }

    @Mapping(target = "annotation")
    default List<org.cqframework.cql.elm.execution.CqlToElmBase> map(
            List<org.hl7.cql_annotations.r1.CqlToElmBase> annotations) {
        if (annotations == null) {
            return null;
        }

        if (annotations.isEmpty()) {
            return Collections.emptyList();
        }

        List<org.cqframework.cql.elm.execution.CqlToElmBase> engineAnnotations = new ArrayList<>();
        for (org.hl7.cql_annotations.r1.CqlToElmBase a : annotations) {
            if (a instanceof org.hl7.cql_annotations.r1.CqlToElmError) {
                engineAnnotations.add(map((org.hl7.cql_annotations.r1.CqlToElmError) a));
            } else if (a instanceof org.hl7.cql_annotations.r1.CqlToElmInfo) {
                engineAnnotations.add(map((org.hl7.cql_annotations.r1.CqlToElmInfo) a));
            } else if (a instanceof org.hl7.cql_annotations.r1.Locator) {
                engineAnnotations.add(map((org.hl7.cql_annotations.r1.Locator) a));
            } else if (a instanceof org.hl7.cql_annotations.r1.Annotation) {
                engineAnnotations.add(map((org.hl7.cql_annotations.r1.Annotation) a));
            } else {
                throw new IllegalArgumentException(
                        String.format("Tried to map unknown Annotation type: %s", a.getClass().getSimpleName()));
            }
        }

        return engineAnnotations;
    }

    org.cqframework.cql.elm.execution.CqlToElmError map(org.hl7.cql_annotations.r1.CqlToElmError element);

    org.cqframework.cql.elm.execution.CqlToElmInfo map(org.hl7.cql_annotations.r1.CqlToElmInfo element);

    org.cqframework.cql.elm.execution.Locator map(org.hl7.cql_annotations.r1.Locator element);

    org.cqframework.cql.elm.execution.Annotation map(org.hl7.cql_annotations.r1.Annotation element);

    @Mapping(target = "valueset", qualifiedByName = { "ExpressionToValueSetRef" })
    org.cqframework.cql.elm.execution.InValueSet map(org.hl7.elm.r1.InValueSet element);

    @Mapping(target = "valueset", qualifiedByName = { "ExpressionToValueSetRef" })
    org.cqframework.cql.elm.execution.AnyInValueSet map(org.hl7.elm.r1.AnyInValueSet element);

    @Mapping(target = "codesystem", qualifiedByName = { "ExpressionToCodeSystemRef" })
    org.cqframework.cql.elm.execution.InCodeSystem map(org.hl7.elm.r1.InCodeSystem element);

    @Mapping(target = "codesystem", qualifiedByName = { "ExpressionToCodeSystemRef" })
    org.cqframework.cql.elm.execution.AnyInCodeSystem map(org.hl7.elm.r1.AnyInCodeSystem element);

    org.cqframework.cql.elm.execution.Null map(org.hl7.elm.r1.Null element);

    // Start of pre-code gen (See the MapperCodeGen class)
    org.cqframework.cql.elm.execution.ToDate map(org.hl7.elm.r1.ToDate element);

    org.cqframework.cql.elm.execution.Query map(org.hl7.elm.r1.Query element);

    org.cqframework.cql.elm.execution.Substring map(org.hl7.elm.r1.Substring element);

    org.cqframework.cql.elm.execution.ValueSetRef map(org.hl7.elm.r1.ValueSetRef element);

    org.cqframework.cql.elm.execution.ProperContains map(org.hl7.elm.r1.ProperContains element);

    org.cqframework.cql.elm.execution.Xor map(org.hl7.elm.r1.Xor element);

    org.cqframework.cql.elm.execution.OperandDef map(org.hl7.elm.r1.OperandDef element);

    org.cqframework.cql.elm.execution.With map(org.hl7.elm.r1.With element);

    org.cqframework.cql.elm.execution.ByExpression map(org.hl7.elm.r1.ByExpression element);

    org.cqframework.cql.elm.execution.ConvertsToString map(org.hl7.elm.r1.ConvertsToString element);

    org.cqframework.cql.elm.execution.CanConvert map(org.hl7.elm.r1.CanConvert element);

    org.cqframework.cql.elm.execution.IncludeDef map(org.hl7.elm.r1.IncludeDef element);

    org.cqframework.cql.elm.execution.ToDecimal map(org.hl7.elm.r1.ToDecimal element);

    org.cqframework.cql.elm.execution.ToQuantity map(org.hl7.elm.r1.ToQuantity element);

    org.cqframework.cql.elm.execution.Time map(org.hl7.elm.r1.Time element);

    org.cqframework.cql.elm.execution.Before map(org.hl7.elm.r1.Before element);

    org.cqframework.cql.elm.execution.LessOrEqual map(org.hl7.elm.r1.LessOrEqual element);

    org.cqframework.cql.elm.execution.Product map(org.hl7.elm.r1.Product element);

    org.cqframework.cql.elm.execution.Property map(org.hl7.elm.r1.Property element);

    org.cqframework.cql.elm.execution.IndexOf map(org.hl7.elm.r1.IndexOf element);

    org.cqframework.cql.elm.execution.Contains map(org.hl7.elm.r1.Contains element);

    org.cqframework.cql.elm.execution.Indexer map(org.hl7.elm.r1.Indexer element);

    org.cqframework.cql.elm.execution.Quantity map(org.hl7.elm.r1.Quantity element);

    org.cqframework.cql.elm.execution.Ceiling map(org.hl7.elm.r1.Ceiling element);

    org.cqframework.cql.elm.execution.Equal map(org.hl7.elm.r1.Equal element);

    org.cqframework.cql.elm.execution.Retrieve map(org.hl7.elm.r1.Retrieve element);

    org.cqframework.cql.elm.execution.Tuple map(org.hl7.elm.r1.Tuple element);

    org.cqframework.cql.elm.execution.Collapse map(org.hl7.elm.r1.Collapse element);

    org.cqframework.cql.elm.execution.Meets map(org.hl7.elm.r1.Meets element);

    org.cqframework.cql.elm.execution.Add map(org.hl7.elm.r1.Add element);

    org.cqframework.cql.elm.execution.ToString map(org.hl7.elm.r1.ToString element);

    org.cqframework.cql.elm.execution.TupleElementDefinition map(org.hl7.elm.r1.TupleElementDefinition element);

    org.cqframework.cql.elm.execution.Power map(org.hl7.elm.r1.Power element);

    org.cqframework.cql.elm.execution.Last map(org.hl7.elm.r1.Last element);

    org.cqframework.cql.elm.execution.PointFrom map(org.hl7.elm.r1.PointFrom element);

    org.cqframework.cql.elm.execution.Subsumes map(org.hl7.elm.r1.Subsumes element);

    org.cqframework.cql.elm.execution.Variance map(org.hl7.elm.r1.Variance element);

    org.cqframework.cql.elm.execution.ConvertsToDecimal map(org.hl7.elm.r1.ConvertsToDecimal element);

    org.cqframework.cql.elm.execution.FunctionRef map(org.hl7.elm.r1.FunctionRef element);

    org.cqframework.cql.elm.execution.ProperIncludes map(org.hl7.elm.r1.ProperIncludes element);

    org.cqframework.cql.elm.execution.ConvertsToDate map(org.hl7.elm.r1.ConvertsToDate element);

    org.cqframework.cql.elm.execution.Combine map(org.hl7.elm.r1.Combine element);

    org.cqframework.cql.elm.execution.Equivalent map(org.hl7.elm.r1.Equivalent element);

    org.cqframework.cql.elm.execution.Divide map(org.hl7.elm.r1.Divide element);

    org.cqframework.cql.elm.execution.ReplaceMatches map(org.hl7.elm.r1.ReplaceMatches element);

    org.cqframework.cql.elm.execution.Today map(org.hl7.elm.r1.Today element);

    org.cqframework.cql.elm.execution.Exists map(org.hl7.elm.r1.Exists element);

    org.cqframework.cql.elm.execution.SubsumedBy map(org.hl7.elm.r1.SubsumedBy element);

    org.cqframework.cql.elm.execution.Ratio map(org.hl7.elm.r1.Ratio element);

    org.cqframework.cql.elm.execution.End map(org.hl7.elm.r1.End element);

    org.cqframework.cql.elm.execution.ToLong map(org.hl7.elm.r1.ToLong element);

    org.cqframework.cql.elm.execution.Count map(org.hl7.elm.r1.Count element);

    org.cqframework.cql.elm.execution.CodeRef map(org.hl7.elm.r1.CodeRef element);

    org.cqframework.cql.elm.execution.Sum map(org.hl7.elm.r1.Sum element);

    org.cqframework.cql.elm.execution.Min map(org.hl7.elm.r1.Min element);

    org.cqframework.cql.elm.execution.Total map(org.hl7.elm.r1.Total element);

    org.cqframework.cql.elm.execution.IncludeElement map(org.hl7.elm.r1.IncludeElement element);

    org.cqframework.cql.elm.execution.Concept map(org.hl7.elm.r1.Concept element);

    org.cqframework.cql.elm.execution.DateTimeComponentFrom map(
            org.hl7.elm.r1.DateTimeComponentFrom element);

    org.cqframework.cql.elm.execution.Median map(org.hl7.elm.r1.Median element);

    org.cqframework.cql.elm.execution.Flatten map(org.hl7.elm.r1.Flatten element);

    org.cqframework.cql.elm.execution.Exp map(org.hl7.elm.r1.Exp element);

    org.cqframework.cql.elm.execution.NamedTypeSpecifier map(org.hl7.elm.r1.NamedTypeSpecifier element);

    org.cqframework.cql.elm.execution.Less map(org.hl7.elm.r1.Less element);

    org.cqframework.cql.elm.execution.IdentifierRef map(org.hl7.elm.r1.IdentifierRef element);

    org.cqframework.cql.elm.execution.ToConcept map(org.hl7.elm.r1.ToConcept element);

    org.cqframework.cql.elm.execution.PositionOf map(org.hl7.elm.r1.PositionOf element);

    org.cqframework.cql.elm.execution.TimezoneFrom map(org.hl7.elm.r1.TimezoneFrom element);

    org.cqframework.cql.elm.execution.Ln map(org.hl7.elm.r1.Ln element);

    org.cqframework.cql.elm.execution.CalculateAgeAt map(org.hl7.elm.r1.CalculateAgeAt element);

    org.cqframework.cql.elm.execution.Log map(org.hl7.elm.r1.Log element);

    org.cqframework.cql.elm.execution.Now map(org.hl7.elm.r1.Now element);

    org.cqframework.cql.elm.execution.ToDateTime map(org.hl7.elm.r1.ToDateTime element);

    org.cqframework.cql.elm.execution.QueryLetRef map(org.hl7.elm.r1.QueryLetRef element);

    org.cqframework.cql.elm.execution.First map(org.hl7.elm.r1.First element);

    org.cqframework.cql.elm.execution.Without map(org.hl7.elm.r1.Without element);

    org.cqframework.cql.elm.execution.LowBoundary map(org.hl7.elm.r1.LowBoundary element);

    org.cqframework.cql.elm.execution.ConvertQuantity map(org.hl7.elm.r1.ConvertQuantity element);

    org.cqframework.cql.elm.execution.CanConvertQuantity map(org.hl7.elm.r1.CanConvertQuantity element);

    org.cqframework.cql.elm.execution.MaxValue map(org.hl7.elm.r1.MaxValue element);

    org.cqframework.cql.elm.execution.Coalesce map(org.hl7.elm.r1.Coalesce element);

    org.cqframework.cql.elm.execution.ConceptRef map(org.hl7.elm.r1.ConceptRef element);

    org.cqframework.cql.elm.execution.LetClause map(org.hl7.elm.r1.LetClause element);

    org.cqframework.cql.elm.execution.Literal map(org.hl7.elm.r1.Literal element);

    org.cqframework.cql.elm.execution.OperandRef map(org.hl7.elm.r1.OperandRef element);

    org.cqframework.cql.elm.execution.Size map(org.hl7.elm.r1.Size element);

    org.cqframework.cql.elm.execution.Upper map(org.hl7.elm.r1.Upper element);

    org.cqframework.cql.elm.execution.ValueSetDef map(org.hl7.elm.r1.ValueSetDef element);

    org.cqframework.cql.elm.execution.Start map(org.hl7.elm.r1.Start element);

    org.cqframework.cql.elm.execution.CalculateAge map(org.hl7.elm.r1.CalculateAge element);

    org.cqframework.cql.elm.execution.ListTypeSpecifier map(org.hl7.elm.r1.ListTypeSpecifier element);

    org.cqframework.cql.elm.execution.Predecessor map(org.hl7.elm.r1.Predecessor element);

    org.cqframework.cql.elm.execution.Descendents map(org.hl7.elm.r1.Descendents element);

    org.cqframework.cql.elm.execution.Filter map(org.hl7.elm.r1.Filter element);

    org.cqframework.cql.elm.execution.Floor map(org.hl7.elm.r1.Floor element);

    org.cqframework.cql.elm.execution.SameAs map(org.hl7.elm.r1.SameAs element);

    org.cqframework.cql.elm.execution.Times map(org.hl7.elm.r1.Times element);

    org.cqframework.cql.elm.execution.Union map(org.hl7.elm.r1.Union element);

    org.cqframework.cql.elm.execution.Abs map(org.hl7.elm.r1.Abs element);

    org.cqframework.cql.elm.execution.MeetsAfter map(org.hl7.elm.r1.MeetsAfter element);

    org.cqframework.cql.elm.execution.ForEach map(org.hl7.elm.r1.ForEach element);

    org.cqframework.cql.elm.execution.List map(org.hl7.elm.r1.List element);

    org.cqframework.cql.elm.execution.ByDirection map(org.hl7.elm.r1.ByDirection element);

    org.cqframework.cql.elm.execution.After map(org.hl7.elm.r1.After element);

    org.cqframework.cql.elm.execution.SingletonFrom map(org.hl7.elm.r1.SingletonFrom element);

    org.cqframework.cql.elm.execution.Modulo map(org.hl7.elm.r1.Modulo element);

    org.cqframework.cql.elm.execution.UsingDef map(org.hl7.elm.r1.UsingDef element);

    org.cqframework.cql.elm.execution.ToBoolean map(org.hl7.elm.r1.ToBoolean element);

    org.cqframework.cql.elm.execution.Expand map(org.hl7.elm.r1.Expand element);

    org.cqframework.cql.elm.execution.Precision map(org.hl7.elm.r1.Precision element);

    org.cqframework.cql.elm.execution.ConvertsToRatio map(org.hl7.elm.r1.ConvertsToRatio element);

    org.cqframework.cql.elm.execution.Round map(org.hl7.elm.r1.Round element);

    org.cqframework.cql.elm.execution.DateFilterElement map(org.hl7.elm.r1.DateFilterElement element);

    org.cqframework.cql.elm.execution.SortClause map(org.hl7.elm.r1.SortClause element);

    org.cqframework.cql.elm.execution.ToChars map(org.hl7.elm.r1.ToChars element);

    org.cqframework.cql.elm.execution.ProperIncludedIn map(org.hl7.elm.r1.ProperIncludedIn element);

    org.cqframework.cql.elm.execution.Date map(org.hl7.elm.r1.Date element);

    org.cqframework.cql.elm.execution.Width map(org.hl7.elm.r1.Width element);

    org.cqframework.cql.elm.execution.TimezoneOffsetFrom map(org.hl7.elm.r1.TimezoneOffsetFrom element);

    org.cqframework.cql.elm.execution.Successor map(org.hl7.elm.r1.Successor element);

    org.cqframework.cql.elm.execution.HighBoundary map(org.hl7.elm.r1.HighBoundary element);

    org.cqframework.cql.elm.execution.Subtract map(org.hl7.elm.r1.Subtract element);

    org.cqframework.cql.elm.execution.CaseItem map(org.hl7.elm.r1.CaseItem element);

    org.cqframework.cql.elm.execution.Ends map(org.hl7.elm.r1.Ends element);

    org.cqframework.cql.elm.execution.CodeDef map(org.hl7.elm.r1.CodeDef element);

    org.cqframework.cql.elm.execution.OverlapsAfter map(org.hl7.elm.r1.OverlapsAfter element);

    org.cqframework.cql.elm.execution.OverlapsBefore map(org.hl7.elm.r1.OverlapsBefore element);

    org.cqframework.cql.elm.execution.GeometricMean map(org.hl7.elm.r1.GeometricMean element);

    org.cqframework.cql.elm.execution.Lower map(org.hl7.elm.r1.Lower element);

    org.cqframework.cql.elm.execution.ExpressionRef map(org.hl7.elm.r1.ExpressionRef element);

    org.cqframework.cql.elm.execution.TimeFrom map(org.hl7.elm.r1.TimeFrom element);

    org.cqframework.cql.elm.execution.Current map(org.hl7.elm.r1.Current element);

    org.cqframework.cql.elm.execution.Mode map(org.hl7.elm.r1.Mode element);

    org.cqframework.cql.elm.execution.StartsWith map(org.hl7.elm.r1.StartsWith element);

    org.cqframework.cql.elm.execution.AllTrue map(org.hl7.elm.r1.AllTrue element);

    org.cqframework.cql.elm.execution.AnyTrue map(org.hl7.elm.r1.AnyTrue element);

    org.cqframework.cql.elm.execution.Search map(org.hl7.elm.r1.Search element);

    org.cqframework.cql.elm.execution.Slice map(org.hl7.elm.r1.Slice element);

    org.cqframework.cql.elm.execution.ToRatio map(org.hl7.elm.r1.ToRatio element);

    org.cqframework.cql.elm.execution.TimeOfDay map(org.hl7.elm.r1.TimeOfDay element);

    org.cqframework.cql.elm.execution.ProperIn map(org.hl7.elm.r1.ProperIn element);

    org.cqframework.cql.elm.execution.And map(org.hl7.elm.r1.And element);

    org.cqframework.cql.elm.execution.ConvertsToInteger map(org.hl7.elm.r1.ConvertsToInteger element);

    org.cqframework.cql.elm.execution.AliasRef map(org.hl7.elm.r1.AliasRef element);

    org.cqframework.cql.elm.execution.CodeSystemDef map(org.hl7.elm.r1.CodeSystemDef element);

    org.cqframework.cql.elm.execution.Interval map(org.hl7.elm.r1.Interval element);

    org.cqframework.cql.elm.execution.OtherFilterElement map(org.hl7.elm.r1.OtherFilterElement element);

    org.cqframework.cql.elm.execution.LastPositionOf map(org.hl7.elm.r1.LastPositionOf element);

    org.cqframework.cql.elm.execution.IsTrue map(org.hl7.elm.r1.IsTrue element);

    org.cqframework.cql.elm.execution.ExpandValueSet map(org.hl7.elm.r1.ExpandValueSet element);

    org.cqframework.cql.elm.execution.Negate map(org.hl7.elm.r1.Negate element);

    org.cqframework.cql.elm.execution.IntervalTypeSpecifier map(org.hl7.elm.r1.IntervalTypeSpecifier element);

    org.cqframework.cql.elm.execution.Overlaps map(org.hl7.elm.r1.Overlaps element);

    org.cqframework.cql.elm.execution.Distinct map(org.hl7.elm.r1.Distinct element);

    org.cqframework.cql.elm.execution.CodeSystemRef map(org.hl7.elm.r1.CodeSystemRef element);

    org.cqframework.cql.elm.execution.In map(org.hl7.elm.r1.In element);

    org.cqframework.cql.elm.execution.ChoiceTypeSpecifier map(org.hl7.elm.r1.ChoiceTypeSpecifier element);

    org.cqframework.cql.elm.execution.EndsWith map(org.hl7.elm.r1.EndsWith element);

    org.cqframework.cql.elm.execution.ParameterDef map(org.hl7.elm.r1.ParameterDef element);

    org.cqframework.cql.elm.execution.Includes map(org.hl7.elm.r1.Includes element);

    org.cqframework.cql.elm.execution.ToTime map(org.hl7.elm.r1.ToTime element);

    org.cqframework.cql.elm.execution.Intersect map(org.hl7.elm.r1.Intersect element);

    org.cqframework.cql.elm.execution.SameOrBefore map(org.hl7.elm.r1.SameOrBefore element);

    org.cqframework.cql.elm.execution.PopulationStdDev map(org.hl7.elm.r1.PopulationStdDev element);

    org.cqframework.cql.elm.execution.Code map(org.hl7.elm.r1.Code element);

    org.cqframework.cql.elm.execution.Concatenate map(org.hl7.elm.r1.Concatenate element);

    org.cqframework.cql.elm.execution.Convert map(org.hl7.elm.r1.Convert element);

    org.cqframework.cql.elm.execution.MinValue map(org.hl7.elm.r1.MinValue element);

    org.cqframework.cql.elm.execution.SameOrAfter map(org.hl7.elm.r1.SameOrAfter element);

    org.cqframework.cql.elm.execution.Split map(org.hl7.elm.r1.Split element);

    org.cqframework.cql.elm.execution.Greater map(org.hl7.elm.r1.Greater element);

    org.cqframework.cql.elm.execution.ConvertsToLong map(org.hl7.elm.r1.ConvertsToLong element);

    org.cqframework.cql.elm.execution.Aggregate map(org.hl7.elm.r1.Aggregate element);

    org.cqframework.cql.elm.execution.If map(org.hl7.elm.r1.If element);

    org.cqframework.cql.elm.execution.Max map(org.hl7.elm.r1.Max element);

    org.cqframework.cql.elm.execution.IsFalse map(org.hl7.elm.r1.IsFalse element);

    org.cqframework.cql.elm.execution.ConvertsToTime map(org.hl7.elm.r1.ConvertsToTime element);

    org.cqframework.cql.elm.execution.NotEqual map(org.hl7.elm.r1.NotEqual element);

    org.cqframework.cql.elm.execution.Except map(org.hl7.elm.r1.Except element);

    org.cqframework.cql.elm.execution.ReturnClause map(org.hl7.elm.r1.ReturnClause element);

    org.cqframework.cql.elm.execution.Matches map(org.hl7.elm.r1.Matches element);

    org.cqframework.cql.elm.execution.Multiply map(org.hl7.elm.r1.Multiply element);

    org.cqframework.cql.elm.execution.ToList map(org.hl7.elm.r1.ToList element);

    org.cqframework.cql.elm.execution.IsNull map(org.hl7.elm.r1.IsNull element);

    org.cqframework.cql.elm.execution.Instance map(org.hl7.elm.r1.Instance element);

    org.cqframework.cql.elm.execution.TupleTypeSpecifier map(org.hl7.elm.r1.TupleTypeSpecifier element);

    org.cqframework.cql.elm.execution.PopulationVariance map(org.hl7.elm.r1.PopulationVariance element);

    org.cqframework.cql.elm.execution.Repeat map(org.hl7.elm.r1.Repeat element);

    org.cqframework.cql.elm.execution.Library map(org.hl7.elm.r1.Library element);

    org.cqframework.cql.elm.execution.Is map(org.hl7.elm.r1.Is element);

    org.cqframework.cql.elm.execution.Truncate map(org.hl7.elm.r1.Truncate element);

    org.cqframework.cql.elm.execution.Implies map(org.hl7.elm.r1.Implies element);

    org.cqframework.cql.elm.execution.CodeFilterElement map(org.hl7.elm.r1.CodeFilterElement element);

    org.cqframework.cql.elm.execution.ByColumn map(org.hl7.elm.r1.ByColumn element);

    org.cqframework.cql.elm.execution.As map(org.hl7.elm.r1.As element);

    org.cqframework.cql.elm.execution.GreaterOrEqual map(org.hl7.elm.r1.GreaterOrEqual element);

    org.cqframework.cql.elm.execution.Starts map(org.hl7.elm.r1.Starts element);

    org.cqframework.cql.elm.execution.Children map(org.hl7.elm.r1.Children element);

    org.cqframework.cql.elm.execution.Not map(org.hl7.elm.r1.Not element);

    org.cqframework.cql.elm.execution.DateFrom map(org.hl7.elm.r1.DateFrom element);

    org.cqframework.cql.elm.execution.ConvertsToBoolean map(org.hl7.elm.r1.ConvertsToBoolean element);

    org.cqframework.cql.elm.execution.MeetsBefore map(org.hl7.elm.r1.MeetsBefore element);

    org.cqframework.cql.elm.execution.ConvertsToDateTime map(org.hl7.elm.r1.ConvertsToDateTime element);

    org.cqframework.cql.elm.execution.Iteration map(org.hl7.elm.r1.Iteration element);

    org.cqframework.cql.elm.execution.Avg map(org.hl7.elm.r1.Avg element);

    org.cqframework.cql.elm.execution.DurationBetween map(org.hl7.elm.r1.DurationBetween element);

    org.cqframework.cql.elm.execution.ConvertsToQuantity map(org.hl7.elm.r1.ConvertsToQuantity element);

    org.cqframework.cql.elm.execution.StdDev map(org.hl7.elm.r1.StdDev element);

    org.cqframework.cql.elm.execution.Message map(org.hl7.elm.r1.Message element);

    org.cqframework.cql.elm.execution.ToInteger map(org.hl7.elm.r1.ToInteger element);

    org.cqframework.cql.elm.execution.IncludedIn map(org.hl7.elm.r1.IncludedIn element);

    org.cqframework.cql.elm.execution.FunctionDef map(org.hl7.elm.r1.FunctionDef element);

    org.cqframework.cql.elm.execution.Sort map(org.hl7.elm.r1.Sort element);

    org.cqframework.cql.elm.execution.SplitOnMatches map(org.hl7.elm.r1.SplitOnMatches element);

    org.cqframework.cql.elm.execution.Case map(org.hl7.elm.r1.Case element);

    org.cqframework.cql.elm.execution.ConceptDef map(org.hl7.elm.r1.ConceptDef element);

    org.cqframework.cql.elm.execution.Length map(org.hl7.elm.r1.Length element);

    org.cqframework.cql.elm.execution.DateTime map(org.hl7.elm.r1.DateTime element);

    org.cqframework.cql.elm.execution.ParameterRef map(org.hl7.elm.r1.ParameterRef element);

    org.cqframework.cql.elm.execution.Or map(org.hl7.elm.r1.Or element);

    org.cqframework.cql.elm.execution.TruncatedDivide map(org.hl7.elm.r1.TruncatedDivide element);

    org.cqframework.cql.elm.execution.DifferenceBetween map(org.hl7.elm.r1.DifferenceBetween element);

    default org.cqframework.cql.elm.execution.SortByItem map(org.hl7.elm.r1.SortByItem element) {
        if (element == null) {
            return null;
        }

        if (element instanceof org.hl7.elm.r1.ByDirection) {
            return map((org.hl7.elm.r1.ByDirection) element);
        } else if (element instanceof org.hl7.elm.r1.ByExpression) {
            return map((org.hl7.elm.r1.ByExpression) element);
        } else if (element instanceof org.hl7.elm.r1.ByColumn) {
            return map((org.hl7.elm.r1.ByColumn) element);
        }

        throw new IllegalArgumentException(
                "unknown class of org.hl7.elm.r1.SortByItem: " + element.getClass().getName());
    }

    default org.cqframework.cql.elm.execution.AggregateExpression map(org.hl7.elm.r1.AggregateExpression element) {
        if (element == null) {
            return null;
        }

        if (element instanceof org.hl7.elm.r1.Sum) {
            return map((org.hl7.elm.r1.Sum) element);
        } else if (element instanceof org.hl7.elm.r1.Min) {
            return map((org.hl7.elm.r1.Min) element);
        } else if (element instanceof org.hl7.elm.r1.Count) {
            return map((org.hl7.elm.r1.Count) element);
        } else if (element instanceof org.hl7.elm.r1.Mode) {
            return map((org.hl7.elm.r1.Mode) element);
        } else if (element instanceof org.hl7.elm.r1.AllTrue) {
            return map((org.hl7.elm.r1.AllTrue) element);
        } else if (element instanceof org.hl7.elm.r1.PopulationVariance) {
            return map((org.hl7.elm.r1.PopulationVariance) element);
        } else if (element instanceof org.hl7.elm.r1.Avg) {
            return map((org.hl7.elm.r1.Avg) element);
        } else if (element instanceof org.hl7.elm.r1.StdDev) {
            return map((org.hl7.elm.r1.StdDev) element);
        } else if (element instanceof org.hl7.elm.r1.Product) {
            return map((org.hl7.elm.r1.Product) element);
        } else if (element instanceof org.hl7.elm.r1.Max) {
            return map((org.hl7.elm.r1.Max) element);
        } else if (element instanceof org.hl7.elm.r1.Variance) {
            return map((org.hl7.elm.r1.Variance) element);
        } else if (element instanceof org.hl7.elm.r1.GeometricMean) {
            return map((org.hl7.elm.r1.GeometricMean) element);
        } else if (element instanceof org.hl7.elm.r1.Aggregate) {
            return map((org.hl7.elm.r1.Aggregate) element);
        } else if (element instanceof org.hl7.elm.r1.Median) {
            return map((org.hl7.elm.r1.Median) element);
        } else if (element instanceof org.hl7.elm.r1.PopulationStdDev) {
            return map((org.hl7.elm.r1.PopulationStdDev) element);
        } else if (element instanceof org.hl7.elm.r1.AnyTrue) {
            return map((org.hl7.elm.r1.AnyTrue) element);
        }

        throw new IllegalArgumentException(
                "unknown class of org.hl7.elm.r1.AggregateExpression: " + element.getClass().getName());
    }

    default org.cqframework.cql.elm.execution.UnaryExpression map(org.hl7.elm.r1.UnaryExpression element) {
        if (element == null) {
            return null;
        }

        if (element instanceof org.hl7.elm.r1.ConvertsToBoolean) {
            return map((org.hl7.elm.r1.ConvertsToBoolean) element);
        } else if (element instanceof org.hl7.elm.r1.ToString) {
            return map((org.hl7.elm.r1.ToString) element);
        } else if (element instanceof org.hl7.elm.r1.Exists) {
            return map((org.hl7.elm.r1.Exists) element);
        } else if (element instanceof org.hl7.elm.r1.CanConvert) {
            return map((org.hl7.elm.r1.CanConvert) element);
        } else if (element instanceof org.hl7.elm.r1.ToBoolean) {
            return map((org.hl7.elm.r1.ToBoolean) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToLong) {
            return map((org.hl7.elm.r1.ConvertsToLong) element);
        } else if (element instanceof org.hl7.elm.r1.Exp) {
            return map((org.hl7.elm.r1.Exp) element);
        } else if (element instanceof org.hl7.elm.r1.Flatten) {
            return map((org.hl7.elm.r1.Flatten) element);
        } else if (element instanceof org.hl7.elm.r1.Lower) {
            return map((org.hl7.elm.r1.Lower) element);
        } else if (element instanceof org.hl7.elm.r1.Precision) {
            return map((org.hl7.elm.r1.Precision) element);
        } else if (element instanceof org.hl7.elm.r1.Length) {
            return map((org.hl7.elm.r1.Length) element);
        } else if (element instanceof org.hl7.elm.r1.Successor) {
            return map((org.hl7.elm.r1.Successor) element);
        } else if (element instanceof org.hl7.elm.r1.Width) {
            return map((org.hl7.elm.r1.Width) element);
        } else if (element instanceof org.hl7.elm.r1.ToInteger) {
            return map((org.hl7.elm.r1.ToInteger) element);
        } else if (element instanceof org.hl7.elm.r1.ToChars) {
            return map((org.hl7.elm.r1.ToChars) element);
        } else if (element instanceof org.hl7.elm.r1.SingletonFrom) {
            return map((org.hl7.elm.r1.SingletonFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Size) {
            return map((org.hl7.elm.r1.Size) element);
        } else if (element instanceof org.hl7.elm.r1.As) {
            return map((org.hl7.elm.r1.As) element);
        } else if (element instanceof org.hl7.elm.r1.ToRatio) {
            return map((org.hl7.elm.r1.ToRatio) element);
        } else if (element instanceof org.hl7.elm.r1.Abs) {
            return map((org.hl7.elm.r1.Abs) element);
        } else if (element instanceof org.hl7.elm.r1.ToList) {
            return map((org.hl7.elm.r1.ToList) element);
        } else if (element instanceof org.hl7.elm.r1.CalculateAge) {
            return map((org.hl7.elm.r1.CalculateAge) element);
        } else if (element instanceof org.hl7.elm.r1.ToConcept) {
            return map((org.hl7.elm.r1.ToConcept) element);
        } else if (element instanceof org.hl7.elm.r1.DateFrom) {
            return map((org.hl7.elm.r1.DateFrom) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToInteger) {
            return map((org.hl7.elm.r1.ConvertsToInteger) element);
        } else if (element instanceof org.hl7.elm.r1.ToLong) {
            return map((org.hl7.elm.r1.ToLong) element);
        } else if (element instanceof org.hl7.elm.r1.TimezoneFrom) {
            return map((org.hl7.elm.r1.TimezoneFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Convert) {
            return map((org.hl7.elm.r1.Convert) element);
        } else if (element instanceof org.hl7.elm.r1.End) {
            return map((org.hl7.elm.r1.End) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToDate) {
            return map((org.hl7.elm.r1.ConvertsToDate) element);
        } else if (element instanceof org.hl7.elm.r1.Not) {
            return map((org.hl7.elm.r1.Not) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToDateTime) {
            return map((org.hl7.elm.r1.ConvertsToDateTime) element);
        } else if (element instanceof org.hl7.elm.r1.ToDecimal) {
            return map((org.hl7.elm.r1.ToDecimal) element);
        } else if (element instanceof org.hl7.elm.r1.Start) {
            return map((org.hl7.elm.r1.Start) element);
        } else if (element instanceof org.hl7.elm.r1.IsFalse) {
            return map((org.hl7.elm.r1.IsFalse) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToDecimal) {
            return map((org.hl7.elm.r1.ConvertsToDecimal) element);
        } else if (element instanceof org.hl7.elm.r1.IsNull) {
            return map((org.hl7.elm.r1.IsNull) element);
        } else if (element instanceof org.hl7.elm.r1.IsTrue) {
            return map((org.hl7.elm.r1.IsTrue) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToRatio) {
            return map((org.hl7.elm.r1.ConvertsToRatio) element);
        } else if (element instanceof org.hl7.elm.r1.Ceiling) {
            return map((org.hl7.elm.r1.Ceiling) element);
        } else if (element instanceof org.hl7.elm.r1.TimezoneOffsetFrom) {
            return map((org.hl7.elm.r1.TimezoneOffsetFrom) element);
        } else if (element instanceof org.hl7.elm.r1.DateTimeComponentFrom) {
            return map((org.hl7.elm.r1.DateTimeComponentFrom) element);
        } else if (element instanceof org.hl7.elm.r1.ToQuantity) {
            return map((org.hl7.elm.r1.ToQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Upper) {
            return map((org.hl7.elm.r1.Upper) element);
        } else if (element instanceof org.hl7.elm.r1.Is) {
            return map((org.hl7.elm.r1.Is) element);
        } else if (element instanceof org.hl7.elm.r1.ExpandValueSet) {
            return map((org.hl7.elm.r1.ExpandValueSet) element);
        } else if (element instanceof org.hl7.elm.r1.Truncate) {
            return map((org.hl7.elm.r1.Truncate) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToString) {
            return map((org.hl7.elm.r1.ConvertsToString) element);
        } else if (element instanceof org.hl7.elm.r1.ToTime) {
            return map((org.hl7.elm.r1.ToTime) element);
        } else if (element instanceof org.hl7.elm.r1.Ln) {
            return map((org.hl7.elm.r1.Ln) element);
        } else if (element instanceof org.hl7.elm.r1.ToDate) {
            return map((org.hl7.elm.r1.ToDate) element);
        } else if (element instanceof org.hl7.elm.r1.TimeFrom) {
            return map((org.hl7.elm.r1.TimeFrom) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToTime) {
            return map((org.hl7.elm.r1.ConvertsToTime) element);
        } else if (element instanceof org.hl7.elm.r1.Predecessor) {
            return map((org.hl7.elm.r1.Predecessor) element);
        } else if (element instanceof org.hl7.elm.r1.Negate) {
            return map((org.hl7.elm.r1.Negate) element);
        } else if (element instanceof org.hl7.elm.r1.PointFrom) {
            return map((org.hl7.elm.r1.PointFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Distinct) {
            return map((org.hl7.elm.r1.Distinct) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToQuantity) {
            return map((org.hl7.elm.r1.ConvertsToQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.ToDateTime) {
            return map((org.hl7.elm.r1.ToDateTime) element);
        } else if (element instanceof org.hl7.elm.r1.Floor) {
            return map((org.hl7.elm.r1.Floor) element);
        }

        throw new IllegalArgumentException(
                "unknown class of org.hl7.elm.r1.UnaryExpression: " + element.getClass().getName());
    }

    default org.cqframework.cql.elm.execution.RelationshipClause map(org.hl7.elm.r1.RelationshipClause element) {
        if (element == null) {
            return null;
        }

        if (element instanceof org.hl7.elm.r1.Without) {
            return map((org.hl7.elm.r1.Without) element);
        } else if (element instanceof org.hl7.elm.r1.With) {
            return map((org.hl7.elm.r1.With) element);
        }

        throw new IllegalArgumentException(
                "unknown class of org.hl7.elm.r1.RelationshipClause: " + element.getClass().getName());
    }

    default org.cqframework.cql.elm.execution.Expression map(org.hl7.elm.r1.Expression element) {
        if (element == null) {
            return null;
        }

        if (element instanceof org.hl7.elm.r1.First) {
            return map((org.hl7.elm.r1.First) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToBoolean) {
            return map((org.hl7.elm.r1.ConvertsToBoolean) element);
        } else if (element instanceof org.hl7.elm.r1.CanConvertQuantity) {
            return map((org.hl7.elm.r1.CanConvertQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Meets) {
            return map((org.hl7.elm.r1.Meets) element);
        } else if (element instanceof org.hl7.elm.r1.ToString) {
            return map((org.hl7.elm.r1.ToString) element);
        } else if (element instanceof org.hl7.elm.r1.MinValue) {
            return map((org.hl7.elm.r1.MinValue) element);
        } else if (element instanceof org.hl7.elm.r1.DifferenceBetween) {
            return map((org.hl7.elm.r1.DifferenceBetween) element);
        } else if (element instanceof org.hl7.elm.r1.Exists) {
            return map((org.hl7.elm.r1.Exists) element);
        } else if (element instanceof org.hl7.elm.r1.CanConvert) {
            return map((org.hl7.elm.r1.CanConvert) element);
        } else if (element instanceof org.hl7.elm.r1.IncludedIn) {
            return map((org.hl7.elm.r1.IncludedIn) element);
        } else if (element instanceof org.hl7.elm.r1.Or) {
            return map((org.hl7.elm.r1.Or) element);
        } else if (element instanceof org.hl7.elm.r1.Current) {
            return map((org.hl7.elm.r1.Current) element);
        } else if (element instanceof org.hl7.elm.r1.Substring) {
            return map((org.hl7.elm.r1.Substring) element);
        } else if (element instanceof org.hl7.elm.r1.ToBoolean) {
            return map((org.hl7.elm.r1.ToBoolean) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToLong) {
            return map((org.hl7.elm.r1.ConvertsToLong) element);
        } else if (element instanceof org.hl7.elm.r1.Slice) {
            return map((org.hl7.elm.r1.Slice) element);
        } else if (element instanceof org.hl7.elm.r1.LessOrEqual) {
            return map((org.hl7.elm.r1.LessOrEqual) element);
        } else if (element instanceof org.hl7.elm.r1.Exp) {
            return map((org.hl7.elm.r1.Exp) element);
        } else if (element instanceof org.hl7.elm.r1.OverlapsAfter) {
            return map((org.hl7.elm.r1.OverlapsAfter) element);
        } else if (element instanceof org.hl7.elm.r1.Repeat) {
            return map((org.hl7.elm.r1.Repeat) element);
        } else if (element instanceof org.hl7.elm.r1.CodeSystemRef) {
            return map((org.hl7.elm.r1.CodeSystemRef) element);
        } else if (element instanceof org.hl7.elm.r1.Flatten) {
            return map((org.hl7.elm.r1.Flatten) element);
        } else if (element instanceof org.hl7.elm.r1.Lower) {
            return map((org.hl7.elm.r1.Lower) element);
        } else if (element instanceof org.hl7.elm.r1.Precision) {
            return map((org.hl7.elm.r1.Precision) element);
        } else if (element instanceof org.hl7.elm.r1.IndexOf) {
            return map((org.hl7.elm.r1.IndexOf) element);
        } else if (element instanceof org.hl7.elm.r1.ProperContains) {
            return map((org.hl7.elm.r1.ProperContains) element);
        } else if (element instanceof org.hl7.elm.r1.Length) {
            return map((org.hl7.elm.r1.Length) element);
        } else if (element instanceof org.hl7.elm.r1.PositionOf) {
            return map((org.hl7.elm.r1.PositionOf) element);
        } else if (element instanceof org.hl7.elm.r1.Code) {
            return map((org.hl7.elm.r1.Code) element);
        } else if (element instanceof org.hl7.elm.r1.Successor) {
            return map((org.hl7.elm.r1.Successor) element);
        } else if (element instanceof org.hl7.elm.r1.SplitOnMatches) {
            return map((org.hl7.elm.r1.SplitOnMatches) element);
        } else if (element instanceof org.hl7.elm.r1.Retrieve) {
            return map((org.hl7.elm.r1.Retrieve) element);
        } else if (element instanceof org.hl7.elm.r1.Width) {
            return map((org.hl7.elm.r1.Width) element);
        } else if (element instanceof org.hl7.elm.r1.ToInteger) {
            return map((org.hl7.elm.r1.ToInteger) element);
        } else if (element instanceof org.hl7.elm.r1.SameAs) {
            return map((org.hl7.elm.r1.SameAs) element);
        } else if (element instanceof org.hl7.elm.r1.Descendents) {
            return map((org.hl7.elm.r1.Descendents) element);
        } else if (element instanceof org.hl7.elm.r1.ToChars) {
            return map((org.hl7.elm.r1.ToChars) element);
        } else if (element instanceof org.hl7.elm.r1.SingletonFrom) {
            return map((org.hl7.elm.r1.SingletonFrom) element);
        } else if (element instanceof org.hl7.elm.r1.InCodeSystem) {
            return map((org.hl7.elm.r1.InCodeSystem) element);
        } else if (element instanceof org.hl7.elm.r1.Sum) {
            return map((org.hl7.elm.r1.Sum) element);
        } else if (element instanceof org.hl7.elm.r1.Size) {
            return map((org.hl7.elm.r1.Size) element);
        } else if (element instanceof org.hl7.elm.r1.Interval) {
            return map((org.hl7.elm.r1.Interval) element);
        } else if (element instanceof org.hl7.elm.r1.EndsWith) {
            return map((org.hl7.elm.r1.EndsWith) element);
        } else if (element instanceof org.hl7.elm.r1.Tuple) {
            return map((org.hl7.elm.r1.Tuple) element);
        } else if (element instanceof org.hl7.elm.r1.As) {
            return map((org.hl7.elm.r1.As) element);
        } else if (element instanceof org.hl7.elm.r1.ValueSetRef) {
            return map((org.hl7.elm.r1.ValueSetRef) element);
        } else if (element instanceof org.hl7.elm.r1.Xor) {
            return map((org.hl7.elm.r1.Xor) element);
        } else if (element instanceof org.hl7.elm.r1.Combine) {
            return map((org.hl7.elm.r1.Combine) element);
        } else if (element instanceof org.hl7.elm.r1.Starts) {
            return map((org.hl7.elm.r1.Starts) element);
        } else if (element instanceof org.hl7.elm.r1.TimeOfDay) {
            return map((org.hl7.elm.r1.TimeOfDay) element);
        } else if (element instanceof org.hl7.elm.r1.Min) {
            return map((org.hl7.elm.r1.Min) element);
        } else if (element instanceof org.hl7.elm.r1.Power) {
            return map((org.hl7.elm.r1.Power) element);
        } else if (element instanceof org.hl7.elm.r1.AnyInValueSet) {
            return map((org.hl7.elm.r1.AnyInValueSet) element);
        } else if (element instanceof org.hl7.elm.r1.Count) {
            return map((org.hl7.elm.r1.Count) element);
        } else if (element instanceof org.hl7.elm.r1.Query) {
            return map((org.hl7.elm.r1.Query) element);
        } else if (element instanceof org.hl7.elm.r1.Sort) {
            return map((org.hl7.elm.r1.Sort) element);
        } else if (element instanceof org.hl7.elm.r1.Mode) {
            return map((org.hl7.elm.r1.Mode) element);
        } else if (element instanceof org.hl7.elm.r1.Equal) {
            return map((org.hl7.elm.r1.Equal) element);
        } else if (element instanceof org.hl7.elm.r1.Date) {
            return map((org.hl7.elm.r1.Date) element);
        } else if (element instanceof org.hl7.elm.r1.ForEach) {
            return map((org.hl7.elm.r1.ForEach) element);
        } else if (element instanceof org.hl7.elm.r1.Intersect) {
            return map((org.hl7.elm.r1.Intersect) element);
        } else if (element instanceof org.hl7.elm.r1.ToRatio) {
            return map((org.hl7.elm.r1.ToRatio) element);
        } else if (element instanceof org.hl7.elm.r1.Abs) {
            return map((org.hl7.elm.r1.Abs) element);
        } else if (element instanceof org.hl7.elm.r1.ConceptRef) {
            return map((org.hl7.elm.r1.ConceptRef) element);
        } else if (element instanceof org.hl7.elm.r1.IdentifierRef) {
            return map((org.hl7.elm.r1.IdentifierRef) element);
        } else if (element instanceof org.hl7.elm.r1.Subsumes) {
            return map((org.hl7.elm.r1.Subsumes) element);
        } else if (element instanceof org.hl7.elm.r1.ToList) {
            return map((org.hl7.elm.r1.ToList) element);
        } else if (element instanceof org.hl7.elm.r1.ParameterRef) {
            return map((org.hl7.elm.r1.ParameterRef) element);
        } else if (element instanceof org.hl7.elm.r1.GreaterOrEqual) {
            return map((org.hl7.elm.r1.GreaterOrEqual) element);
        } else if (element instanceof org.hl7.elm.r1.SubsumedBy) {
            return map((org.hl7.elm.r1.SubsumedBy) element);
        } else if (element instanceof org.hl7.elm.r1.Add) {
            return map((org.hl7.elm.r1.Add) element);
        } else if (element instanceof org.hl7.elm.r1.CalculateAge) {
            return map((org.hl7.elm.r1.CalculateAge) element);
        } else if (element instanceof org.hl7.elm.r1.ToConcept) {
            return map((org.hl7.elm.r1.ToConcept) element);
        } else if (element instanceof org.hl7.elm.r1.Multiply) {
            return map((org.hl7.elm.r1.Multiply) element);
        } else if (element instanceof org.hl7.elm.r1.DateFrom) {
            return map((org.hl7.elm.r1.DateFrom) element);
        } else if (element instanceof org.hl7.elm.r1.SameOrAfter) {
            return map((org.hl7.elm.r1.SameOrAfter) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToInteger) {
            return map((org.hl7.elm.r1.ConvertsToInteger) element);
        } else if (element instanceof org.hl7.elm.r1.ToLong) {
            return map((org.hl7.elm.r1.ToLong) element);
        } else if (element instanceof org.hl7.elm.r1.AllTrue) {
            return map((org.hl7.elm.r1.AllTrue) element);
        } else if (element instanceof org.hl7.elm.r1.Coalesce) {
            return map((org.hl7.elm.r1.Coalesce) element);
        } else if (element instanceof org.hl7.elm.r1.FunctionRef) {
            return map((org.hl7.elm.r1.FunctionRef) element);
        } else if (element instanceof org.hl7.elm.r1.Expand) {
            return map((org.hl7.elm.r1.Expand) element);
        } else if (element instanceof org.hl7.elm.r1.PopulationVariance) {
            return map((org.hl7.elm.r1.PopulationVariance) element);
        } else if (element instanceof org.hl7.elm.r1.Filter) {
            return map((org.hl7.elm.r1.Filter) element);
        } else if (element instanceof org.hl7.elm.r1.ProperIn) {
            return map((org.hl7.elm.r1.ProperIn) element);
        } else if (element instanceof org.hl7.elm.r1.StartsWith) {
            return map((org.hl7.elm.r1.StartsWith) element);
        } else if (element instanceof org.hl7.elm.r1.Null) {
            return map((org.hl7.elm.r1.Null) element);
        } else if (element instanceof org.hl7.elm.r1.LowBoundary) {
            return map((org.hl7.elm.r1.LowBoundary) element);
        } else if (element instanceof org.hl7.elm.r1.TimezoneFrom) {
            return map((org.hl7.elm.r1.TimezoneFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Convert) {
            return map((org.hl7.elm.r1.Convert) element);
        } else if (element instanceof org.hl7.elm.r1.Less) {
            return map((org.hl7.elm.r1.Less) element);
        } else if (element instanceof org.hl7.elm.r1.Search) {
            return map((org.hl7.elm.r1.Search) element);
        } else if (element instanceof org.hl7.elm.r1.End) {
            return map((org.hl7.elm.r1.End) element);
        } else if (element instanceof org.hl7.elm.r1.QueryLetRef) {
            return map((org.hl7.elm.r1.QueryLetRef) element);
        } else if (element instanceof org.hl7.elm.r1.Now) {
            return map((org.hl7.elm.r1.Now) element);
        } else if (element instanceof org.hl7.elm.r1.Concatenate) {
            return map((org.hl7.elm.r1.Concatenate) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToDate) {
            return map((org.hl7.elm.r1.ConvertsToDate) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertQuantity) {
            return map((org.hl7.elm.r1.ConvertQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Avg) {
            return map((org.hl7.elm.r1.Avg) element);
        } else if (element instanceof org.hl7.elm.r1.Not) {
            return map((org.hl7.elm.r1.Not) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToDateTime) {
            return map((org.hl7.elm.r1.ConvertsToDateTime) element);
        } else if (element instanceof org.hl7.elm.r1.Includes) {
            return map((org.hl7.elm.r1.Includes) element);
        } else if (element instanceof org.hl7.elm.r1.ToDecimal) {
            return map((org.hl7.elm.r1.ToDecimal) element);
        } else if (element instanceof org.hl7.elm.r1.Start) {
            return map((org.hl7.elm.r1.Start) element);
        } else if (element instanceof org.hl7.elm.r1.IsFalse) {
            return map((org.hl7.elm.r1.IsFalse) element);
        } else if (element instanceof org.hl7.elm.r1.Modulo) {
            return map((org.hl7.elm.r1.Modulo) element);
        } else if (element instanceof org.hl7.elm.r1.Collapse) {
            return map((org.hl7.elm.r1.Collapse) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToDecimal) {
            return map((org.hl7.elm.r1.ConvertsToDecimal) element);
        } else if (element instanceof org.hl7.elm.r1.Case) {
            return map((org.hl7.elm.r1.Case) element);
        } else if (element instanceof org.hl7.elm.r1.Instance) {
            return map((org.hl7.elm.r1.Instance) element);
        } else if (element instanceof org.hl7.elm.r1.IsNull) {
            return map((org.hl7.elm.r1.IsNull) element);
        } else if (element instanceof org.hl7.elm.r1.StdDev) {
            return map((org.hl7.elm.r1.StdDev) element);
        } else if (element instanceof org.hl7.elm.r1.IsTrue) {
            return map((org.hl7.elm.r1.IsTrue) element);
        } else if (element instanceof org.hl7.elm.r1.If) {
            return map((org.hl7.elm.r1.If) element);
        } else if (element instanceof org.hl7.elm.r1.Product) {
            return map((org.hl7.elm.r1.Product) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToRatio) {
            return map((org.hl7.elm.r1.ConvertsToRatio) element);
        } else if (element instanceof org.hl7.elm.r1.CodeRef) {
            return map((org.hl7.elm.r1.CodeRef) element);
        } else if (element instanceof org.hl7.elm.r1.ReplaceMatches) {
            return map((org.hl7.elm.r1.ReplaceMatches) element);
        } else if (element instanceof org.hl7.elm.r1.MeetsAfter) {
            return map((org.hl7.elm.r1.MeetsAfter) element);
        } else if (element instanceof org.hl7.elm.r1.Overlaps) {
            return map((org.hl7.elm.r1.Overlaps) element);
        } else if (element instanceof org.hl7.elm.r1.Ceiling) {
            return map((org.hl7.elm.r1.Ceiling) element);
        } else if (element instanceof org.hl7.elm.r1.AnyInCodeSystem) {
            return map((org.hl7.elm.r1.AnyInCodeSystem) element);
        } else if (element instanceof org.hl7.elm.r1.TimezoneOffsetFrom) {
            return map((org.hl7.elm.r1.TimezoneOffsetFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Indexer) {
            return map((org.hl7.elm.r1.Indexer) element);
        } else if (element instanceof org.hl7.elm.r1.Message) {
            return map((org.hl7.elm.r1.Message) element);
        } else if (element instanceof org.hl7.elm.r1.Union) {
            return map((org.hl7.elm.r1.Union) element);
        } else if (element instanceof org.hl7.elm.r1.NotEqual) {
            return map((org.hl7.elm.r1.NotEqual) element);
        } else if (element instanceof org.hl7.elm.r1.Children) {
            return map((org.hl7.elm.r1.Children) element);
        } else if (element instanceof org.hl7.elm.r1.Ends) {
            return map((org.hl7.elm.r1.Ends) element);
        } else if (element instanceof org.hl7.elm.r1.DateTimeComponentFrom) {
            return map((org.hl7.elm.r1.DateTimeComponentFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Iteration) {
            return map((org.hl7.elm.r1.Iteration) element);
        } else if (element instanceof org.hl7.elm.r1.Except) {
            return map((org.hl7.elm.r1.Except) element);
        } else if (element instanceof org.hl7.elm.r1.Ratio) {
            return map((org.hl7.elm.r1.Ratio) element);
        } else if (element instanceof org.hl7.elm.r1.Quantity) {
            return map((org.hl7.elm.r1.Quantity) element);
        } else if (element instanceof org.hl7.elm.r1.TruncatedDivide) {
            return map((org.hl7.elm.r1.TruncatedDivide) element);
        } else if (element instanceof org.hl7.elm.r1.ToQuantity) {
            return map((org.hl7.elm.r1.ToQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Upper) {
            return map((org.hl7.elm.r1.Upper) element);
        } else if (element instanceof org.hl7.elm.r1.SameOrBefore) {
            return map((org.hl7.elm.r1.SameOrBefore) element);
        } else if (element instanceof org.hl7.elm.r1.In) {
            return map((org.hl7.elm.r1.In) element);
        } else if (element instanceof org.hl7.elm.r1.MeetsBefore) {
            return map((org.hl7.elm.r1.MeetsBefore) element);
        } else if (element instanceof org.hl7.elm.r1.Log) {
            return map((org.hl7.elm.r1.Log) element);
        } else if (element instanceof org.hl7.elm.r1.Is) {
            return map((org.hl7.elm.r1.Is) element);
        } else if (element instanceof org.hl7.elm.r1.ExpandValueSet) {
            return map((org.hl7.elm.r1.ExpandValueSet) element);
        } else if (element instanceof org.hl7.elm.r1.Greater) {
            return map((org.hl7.elm.r1.Greater) element);
        } else if (element instanceof org.hl7.elm.r1.Truncate) {
            return map((org.hl7.elm.r1.Truncate) element);
        } else if (element instanceof org.hl7.elm.r1.ProperIncludes) {
            return map((org.hl7.elm.r1.ProperIncludes) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToString) {
            return map((org.hl7.elm.r1.ConvertsToString) element);
        } else if (element instanceof org.hl7.elm.r1.Divide) {
            return map((org.hl7.elm.r1.Divide) element);
        } else if (element instanceof org.hl7.elm.r1.OverlapsBefore) {
            return map((org.hl7.elm.r1.OverlapsBefore) element);
        } else if (element instanceof org.hl7.elm.r1.After) {
            return map((org.hl7.elm.r1.After) element);
        } else if (element instanceof org.hl7.elm.r1.ToTime) {
            return map((org.hl7.elm.r1.ToTime) element);
        } else if (element instanceof org.hl7.elm.r1.HighBoundary) {
            return map((org.hl7.elm.r1.HighBoundary) element);
        } else if (element instanceof org.hl7.elm.r1.Ln) {
            return map((org.hl7.elm.r1.Ln) element);
        } else if (element instanceof org.hl7.elm.r1.ToDate) {
            return map((org.hl7.elm.r1.ToDate) element);
        } else if (element instanceof org.hl7.elm.r1.TimeFrom) {
            return map((org.hl7.elm.r1.TimeFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Subtract) {
            return map((org.hl7.elm.r1.Subtract) element);
        } else if (element instanceof org.hl7.elm.r1.List) {
            return map((org.hl7.elm.r1.List) element);
        } else if (element instanceof org.hl7.elm.r1.Max) {
            return map((org.hl7.elm.r1.Max) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToTime) {
            return map((org.hl7.elm.r1.ConvertsToTime) element);
        } else if (element instanceof org.hl7.elm.r1.Predecessor) {
            return map((org.hl7.elm.r1.Predecessor) element);
        } else if (element instanceof org.hl7.elm.r1.Negate) {
            return map((org.hl7.elm.r1.Negate) element);
        } else if (element instanceof org.hl7.elm.r1.Before) {
            return map((org.hl7.elm.r1.Before) element);
        } else if (element instanceof org.hl7.elm.r1.Implies) {
            return map((org.hl7.elm.r1.Implies) element);
        } else if (element instanceof org.hl7.elm.r1.PointFrom) {
            return map((org.hl7.elm.r1.PointFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Variance) {
            return map((org.hl7.elm.r1.Variance) element);
        } else if (element instanceof org.hl7.elm.r1.Time) {
            return map((org.hl7.elm.r1.Time) element);
        } else if (element instanceof org.hl7.elm.r1.ExpressionRef) {
            return map((org.hl7.elm.r1.ExpressionRef) element);
        } else if (element instanceof org.hl7.elm.r1.Distinct) {
            return map((org.hl7.elm.r1.Distinct) element);
        } else if (element instanceof org.hl7.elm.r1.DurationBetween) {
            return map((org.hl7.elm.r1.DurationBetween) element);
        } else if (element instanceof org.hl7.elm.r1.Literal) {
            return map((org.hl7.elm.r1.Literal) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToQuantity) {
            return map((org.hl7.elm.r1.ConvertsToQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Concept) {
            return map((org.hl7.elm.r1.Concept) element);
        } else if (element instanceof org.hl7.elm.r1.GeometricMean) {
            return map((org.hl7.elm.r1.GeometricMean) element);
        } else if (element instanceof org.hl7.elm.r1.Split) {
            return map((org.hl7.elm.r1.Split) element);
        } else if (element instanceof org.hl7.elm.r1.Aggregate) {
            return map((org.hl7.elm.r1.Aggregate) element);
        } else if (element instanceof org.hl7.elm.r1.ToDateTime) {
            return map((org.hl7.elm.r1.ToDateTime) element);
        } else if (element instanceof org.hl7.elm.r1.Today) {
            return map((org.hl7.elm.r1.Today) element);
        } else if (element instanceof org.hl7.elm.r1.Median) {
            return map((org.hl7.elm.r1.Median) element);
        } else if (element instanceof org.hl7.elm.r1.Equivalent) {
            return map((org.hl7.elm.r1.Equivalent) element);
        } else if (element instanceof org.hl7.elm.r1.Round) {
            return map((org.hl7.elm.r1.Round) element);
        } else if (element instanceof org.hl7.elm.r1.DateTime) {
            return map((org.hl7.elm.r1.DateTime) element);
        } else if (element instanceof org.hl7.elm.r1.MaxValue) {
            return map((org.hl7.elm.r1.MaxValue) element);
        } else if (element instanceof org.hl7.elm.r1.InValueSet) {
            return map((org.hl7.elm.r1.InValueSet) element);
        } else if (element instanceof org.hl7.elm.r1.Floor) {
            return map((org.hl7.elm.r1.Floor) element);
        } else if (element instanceof org.hl7.elm.r1.PopulationStdDev) {
            return map((org.hl7.elm.r1.PopulationStdDev) element);
        } else if (element instanceof org.hl7.elm.r1.CalculateAgeAt) {
            return map((org.hl7.elm.r1.CalculateAgeAt) element);
        } else if (element instanceof org.hl7.elm.r1.AliasRef) {
            return map((org.hl7.elm.r1.AliasRef) element);
        } else if (element instanceof org.hl7.elm.r1.LastPositionOf) {
            return map((org.hl7.elm.r1.LastPositionOf) element);
        } else if (element instanceof org.hl7.elm.r1.Last) {
            return map((org.hl7.elm.r1.Last) element);
        } else if (element instanceof org.hl7.elm.r1.Matches) {
            return map((org.hl7.elm.r1.Matches) element);
        } else if (element instanceof org.hl7.elm.r1.Times) {
            return map((org.hl7.elm.r1.Times) element);
        } else if (element instanceof org.hl7.elm.r1.Total) {
            return map((org.hl7.elm.r1.Total) element);
        } else if (element instanceof org.hl7.elm.r1.Contains) {
            return map((org.hl7.elm.r1.Contains) element);
        } else if (element instanceof org.hl7.elm.r1.OperandRef) {
            return map((org.hl7.elm.r1.OperandRef) element);
        } else if (element instanceof org.hl7.elm.r1.And) {
            return map((org.hl7.elm.r1.And) element);
        } else if (element instanceof org.hl7.elm.r1.AnyTrue) {
            return map((org.hl7.elm.r1.AnyTrue) element);
        } else if (element instanceof org.hl7.elm.r1.ProperIncludedIn) {
            return map((org.hl7.elm.r1.ProperIncludedIn) element);
        } else if (element instanceof org.hl7.elm.r1.Property) {
            return map((org.hl7.elm.r1.Property) element);
        } else if (element instanceof org.hl7.elm.r1.AggregateExpression) {
            return map((org.hl7.elm.r1.AggregateExpression) element);
        } else if (element instanceof org.hl7.elm.r1.UnaryExpression) {
            return map((org.hl7.elm.r1.UnaryExpression) element);
        } else if (element instanceof org.hl7.elm.r1.NaryExpression) {
            return map((org.hl7.elm.r1.NaryExpression) element);
        } else if (element instanceof org.hl7.elm.r1.OperatorExpression) {
            return map((org.hl7.elm.r1.OperatorExpression) element);
        } else if (element instanceof org.hl7.elm.r1.TernaryExpression) {
            return map((org.hl7.elm.r1.TernaryExpression) element);
        } else if (element instanceof org.hl7.elm.r1.BinaryExpression) {
            return map((org.hl7.elm.r1.BinaryExpression) element);
        }

        throw new IllegalArgumentException(
                "unknown class of org.hl7.elm.r1.Expression: " + element.getClass().getName());
    }

    default org.cqframework.cql.elm.execution.NaryExpression map(org.hl7.elm.r1.NaryExpression element) {
        if (element == null) {
            return null;
        }

        if (element instanceof org.hl7.elm.r1.Intersect) {
            return map((org.hl7.elm.r1.Intersect) element);
        } else if (element instanceof org.hl7.elm.r1.Coalesce) {
            return map((org.hl7.elm.r1.Coalesce) element);
        } else if (element instanceof org.hl7.elm.r1.Concatenate) {
            return map((org.hl7.elm.r1.Concatenate) element);
        } else if (element instanceof org.hl7.elm.r1.Union) {
            return map((org.hl7.elm.r1.Union) element);
        } else if (element instanceof org.hl7.elm.r1.Except) {
            return map((org.hl7.elm.r1.Except) element);
        }

        throw new IllegalArgumentException(
                "unknown class of org.hl7.elm.r1.NaryExpression: " + element.getClass().getName());
    }

    default org.cqframework.cql.elm.execution.OperatorExpression map(org.hl7.elm.r1.OperatorExpression element) {
        if (element == null) {
            return null;
        }

        if (element instanceof org.hl7.elm.r1.First) {
            return map((org.hl7.elm.r1.First) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToBoolean) {
            return map((org.hl7.elm.r1.ConvertsToBoolean) element);
        } else if (element instanceof org.hl7.elm.r1.CanConvertQuantity) {
            return map((org.hl7.elm.r1.CanConvertQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Meets) {
            return map((org.hl7.elm.r1.Meets) element);
        } else if (element instanceof org.hl7.elm.r1.ToString) {
            return map((org.hl7.elm.r1.ToString) element);
        } else if (element instanceof org.hl7.elm.r1.DifferenceBetween) {
            return map((org.hl7.elm.r1.DifferenceBetween) element);
        } else if (element instanceof org.hl7.elm.r1.Exists) {
            return map((org.hl7.elm.r1.Exists) element);
        } else if (element instanceof org.hl7.elm.r1.CanConvert) {
            return map((org.hl7.elm.r1.CanConvert) element);
        } else if (element instanceof org.hl7.elm.r1.IncludedIn) {
            return map((org.hl7.elm.r1.IncludedIn) element);
        } else if (element instanceof org.hl7.elm.r1.Or) {
            return map((org.hl7.elm.r1.Or) element);
        } else if (element instanceof org.hl7.elm.r1.Substring) {
            return map((org.hl7.elm.r1.Substring) element);
        } else if (element instanceof org.hl7.elm.r1.ToBoolean) {
            return map((org.hl7.elm.r1.ToBoolean) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToLong) {
            return map((org.hl7.elm.r1.ConvertsToLong) element);
        } else if (element instanceof org.hl7.elm.r1.Slice) {
            return map((org.hl7.elm.r1.Slice) element);
        } else if (element instanceof org.hl7.elm.r1.LessOrEqual) {
            return map((org.hl7.elm.r1.LessOrEqual) element);
        } else if (element instanceof org.hl7.elm.r1.Exp) {
            return map((org.hl7.elm.r1.Exp) element);
        } else if (element instanceof org.hl7.elm.r1.OverlapsAfter) {
            return map((org.hl7.elm.r1.OverlapsAfter) element);
        } else if (element instanceof org.hl7.elm.r1.Flatten) {
            return map((org.hl7.elm.r1.Flatten) element);
        } else if (element instanceof org.hl7.elm.r1.Lower) {
            return map((org.hl7.elm.r1.Lower) element);
        } else if (element instanceof org.hl7.elm.r1.Precision) {
            return map((org.hl7.elm.r1.Precision) element);
        } else if (element instanceof org.hl7.elm.r1.IndexOf) {
            return map((org.hl7.elm.r1.IndexOf) element);
        } else if (element instanceof org.hl7.elm.r1.ProperContains) {
            return map((org.hl7.elm.r1.ProperContains) element);
        } else if (element instanceof org.hl7.elm.r1.Length) {
            return map((org.hl7.elm.r1.Length) element);
        } else if (element instanceof org.hl7.elm.r1.PositionOf) {
            return map((org.hl7.elm.r1.PositionOf) element);
        } else if (element instanceof org.hl7.elm.r1.Successor) {
            return map((org.hl7.elm.r1.Successor) element);
        } else if (element instanceof org.hl7.elm.r1.SplitOnMatches) {
            return map((org.hl7.elm.r1.SplitOnMatches) element);
        } else if (element instanceof org.hl7.elm.r1.Width) {
            return map((org.hl7.elm.r1.Width) element);
        } else if (element instanceof org.hl7.elm.r1.ToInteger) {
            return map((org.hl7.elm.r1.ToInteger) element);
        } else if (element instanceof org.hl7.elm.r1.SameAs) {
            return map((org.hl7.elm.r1.SameAs) element);
        } else if (element instanceof org.hl7.elm.r1.Descendents) {
            return map((org.hl7.elm.r1.Descendents) element);
        } else if (element instanceof org.hl7.elm.r1.ToChars) {
            return map((org.hl7.elm.r1.ToChars) element);
        } else if (element instanceof org.hl7.elm.r1.SingletonFrom) {
            return map((org.hl7.elm.r1.SingletonFrom) element);
        } else if (element instanceof org.hl7.elm.r1.InCodeSystem) {
            return map((org.hl7.elm.r1.InCodeSystem) element);
        } else if (element instanceof org.hl7.elm.r1.Size) {
            return map((org.hl7.elm.r1.Size) element);
        } else if (element instanceof org.hl7.elm.r1.EndsWith) {
            return map((org.hl7.elm.r1.EndsWith) element);
        } else if (element instanceof org.hl7.elm.r1.As) {
            return map((org.hl7.elm.r1.As) element);
        } else if (element instanceof org.hl7.elm.r1.Xor) {
            return map((org.hl7.elm.r1.Xor) element);
        } else if (element instanceof org.hl7.elm.r1.Combine) {
            return map((org.hl7.elm.r1.Combine) element);
        } else if (element instanceof org.hl7.elm.r1.Starts) {
            return map((org.hl7.elm.r1.Starts) element);
        } else if (element instanceof org.hl7.elm.r1.TimeOfDay) {
            return map((org.hl7.elm.r1.TimeOfDay) element);
        } else if (element instanceof org.hl7.elm.r1.Power) {
            return map((org.hl7.elm.r1.Power) element);
        } else if (element instanceof org.hl7.elm.r1.AnyInValueSet) {
            return map((org.hl7.elm.r1.AnyInValueSet) element);
        } else if (element instanceof org.hl7.elm.r1.Equal) {
            return map((org.hl7.elm.r1.Equal) element);
        } else if (element instanceof org.hl7.elm.r1.Date) {
            return map((org.hl7.elm.r1.Date) element);
        } else if (element instanceof org.hl7.elm.r1.Intersect) {
            return map((org.hl7.elm.r1.Intersect) element);
        } else if (element instanceof org.hl7.elm.r1.ToRatio) {
            return map((org.hl7.elm.r1.ToRatio) element);
        } else if (element instanceof org.hl7.elm.r1.Abs) {
            return map((org.hl7.elm.r1.Abs) element);
        } else if (element instanceof org.hl7.elm.r1.Subsumes) {
            return map((org.hl7.elm.r1.Subsumes) element);
        } else if (element instanceof org.hl7.elm.r1.ToList) {
            return map((org.hl7.elm.r1.ToList) element);
        } else if (element instanceof org.hl7.elm.r1.GreaterOrEqual) {
            return map((org.hl7.elm.r1.GreaterOrEqual) element);
        } else if (element instanceof org.hl7.elm.r1.SubsumedBy) {
            return map((org.hl7.elm.r1.SubsumedBy) element);
        } else if (element instanceof org.hl7.elm.r1.Add) {
            return map((org.hl7.elm.r1.Add) element);
        } else if (element instanceof org.hl7.elm.r1.CalculateAge) {
            return map((org.hl7.elm.r1.CalculateAge) element);
        } else if (element instanceof org.hl7.elm.r1.ToConcept) {
            return map((org.hl7.elm.r1.ToConcept) element);
        } else if (element instanceof org.hl7.elm.r1.Multiply) {
            return map((org.hl7.elm.r1.Multiply) element);
        } else if (element instanceof org.hl7.elm.r1.DateFrom) {
            return map((org.hl7.elm.r1.DateFrom) element);
        } else if (element instanceof org.hl7.elm.r1.SameOrAfter) {
            return map((org.hl7.elm.r1.SameOrAfter) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToInteger) {
            return map((org.hl7.elm.r1.ConvertsToInteger) element);
        } else if (element instanceof org.hl7.elm.r1.ToLong) {
            return map((org.hl7.elm.r1.ToLong) element);
        } else if (element instanceof org.hl7.elm.r1.Coalesce) {
            return map((org.hl7.elm.r1.Coalesce) element);
        } else if (element instanceof org.hl7.elm.r1.Expand) {
            return map((org.hl7.elm.r1.Expand) element);
        } else if (element instanceof org.hl7.elm.r1.ProperIn) {
            return map((org.hl7.elm.r1.ProperIn) element);
        } else if (element instanceof org.hl7.elm.r1.StartsWith) {
            return map((org.hl7.elm.r1.StartsWith) element);
        } else if (element instanceof org.hl7.elm.r1.LowBoundary) {
            return map((org.hl7.elm.r1.LowBoundary) element);
        } else if (element instanceof org.hl7.elm.r1.TimezoneFrom) {
            return map((org.hl7.elm.r1.TimezoneFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Convert) {
            return map((org.hl7.elm.r1.Convert) element);
        } else if (element instanceof org.hl7.elm.r1.Less) {
            return map((org.hl7.elm.r1.Less) element);
        } else if (element instanceof org.hl7.elm.r1.End) {
            return map((org.hl7.elm.r1.End) element);
        } else if (element instanceof org.hl7.elm.r1.Now) {
            return map((org.hl7.elm.r1.Now) element);
        } else if (element instanceof org.hl7.elm.r1.Concatenate) {
            return map((org.hl7.elm.r1.Concatenate) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToDate) {
            return map((org.hl7.elm.r1.ConvertsToDate) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertQuantity) {
            return map((org.hl7.elm.r1.ConvertQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Not) {
            return map((org.hl7.elm.r1.Not) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToDateTime) {
            return map((org.hl7.elm.r1.ConvertsToDateTime) element);
        } else if (element instanceof org.hl7.elm.r1.Includes) {
            return map((org.hl7.elm.r1.Includes) element);
        } else if (element instanceof org.hl7.elm.r1.ToDecimal) {
            return map((org.hl7.elm.r1.ToDecimal) element);
        } else if (element instanceof org.hl7.elm.r1.Start) {
            return map((org.hl7.elm.r1.Start) element);
        } else if (element instanceof org.hl7.elm.r1.IsFalse) {
            return map((org.hl7.elm.r1.IsFalse) element);
        } else if (element instanceof org.hl7.elm.r1.Modulo) {
            return map((org.hl7.elm.r1.Modulo) element);
        } else if (element instanceof org.hl7.elm.r1.Collapse) {
            return map((org.hl7.elm.r1.Collapse) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToDecimal) {
            return map((org.hl7.elm.r1.ConvertsToDecimal) element);
        } else if (element instanceof org.hl7.elm.r1.IsNull) {
            return map((org.hl7.elm.r1.IsNull) element);
        } else if (element instanceof org.hl7.elm.r1.IsTrue) {
            return map((org.hl7.elm.r1.IsTrue) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToRatio) {
            return map((org.hl7.elm.r1.ConvertsToRatio) element);
        } else if (element instanceof org.hl7.elm.r1.ReplaceMatches) {
            return map((org.hl7.elm.r1.ReplaceMatches) element);
        } else if (element instanceof org.hl7.elm.r1.MeetsAfter) {
            return map((org.hl7.elm.r1.MeetsAfter) element);
        } else if (element instanceof org.hl7.elm.r1.Overlaps) {
            return map((org.hl7.elm.r1.Overlaps) element);
        } else if (element instanceof org.hl7.elm.r1.Ceiling) {
            return map((org.hl7.elm.r1.Ceiling) element);
        } else if (element instanceof org.hl7.elm.r1.AnyInCodeSystem) {
            return map((org.hl7.elm.r1.AnyInCodeSystem) element);
        } else if (element instanceof org.hl7.elm.r1.TimezoneOffsetFrom) {
            return map((org.hl7.elm.r1.TimezoneOffsetFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Indexer) {
            return map((org.hl7.elm.r1.Indexer) element);
        } else if (element instanceof org.hl7.elm.r1.Message) {
            return map((org.hl7.elm.r1.Message) element);
        } else if (element instanceof org.hl7.elm.r1.Union) {
            return map((org.hl7.elm.r1.Union) element);
        } else if (element instanceof org.hl7.elm.r1.NotEqual) {
            return map((org.hl7.elm.r1.NotEqual) element);
        } else if (element instanceof org.hl7.elm.r1.Children) {
            return map((org.hl7.elm.r1.Children) element);
        } else if (element instanceof org.hl7.elm.r1.Ends) {
            return map((org.hl7.elm.r1.Ends) element);
        } else if (element instanceof org.hl7.elm.r1.DateTimeComponentFrom) {
            return map((org.hl7.elm.r1.DateTimeComponentFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Except) {
            return map((org.hl7.elm.r1.Except) element);
        } else if (element instanceof org.hl7.elm.r1.TruncatedDivide) {
            return map((org.hl7.elm.r1.TruncatedDivide) element);
        } else if (element instanceof org.hl7.elm.r1.ToQuantity) {
            return map((org.hl7.elm.r1.ToQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Upper) {
            return map((org.hl7.elm.r1.Upper) element);
        } else if (element instanceof org.hl7.elm.r1.SameOrBefore) {
            return map((org.hl7.elm.r1.SameOrBefore) element);
        } else if (element instanceof org.hl7.elm.r1.In) {
            return map((org.hl7.elm.r1.In) element);
        } else if (element instanceof org.hl7.elm.r1.MeetsBefore) {
            return map((org.hl7.elm.r1.MeetsBefore) element);
        } else if (element instanceof org.hl7.elm.r1.Log) {
            return map((org.hl7.elm.r1.Log) element);
        } else if (element instanceof org.hl7.elm.r1.Is) {
            return map((org.hl7.elm.r1.Is) element);
        } else if (element instanceof org.hl7.elm.r1.ExpandValueSet) {
            return map((org.hl7.elm.r1.ExpandValueSet) element);
        } else if (element instanceof org.hl7.elm.r1.Greater) {
            return map((org.hl7.elm.r1.Greater) element);
        } else if (element instanceof org.hl7.elm.r1.Truncate) {
            return map((org.hl7.elm.r1.Truncate) element);
        } else if (element instanceof org.hl7.elm.r1.ProperIncludes) {
            return map((org.hl7.elm.r1.ProperIncludes) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToString) {
            return map((org.hl7.elm.r1.ConvertsToString) element);
        } else if (element instanceof org.hl7.elm.r1.Divide) {
            return map((org.hl7.elm.r1.Divide) element);
        } else if (element instanceof org.hl7.elm.r1.OverlapsBefore) {
            return map((org.hl7.elm.r1.OverlapsBefore) element);
        } else if (element instanceof org.hl7.elm.r1.After) {
            return map((org.hl7.elm.r1.After) element);
        } else if (element instanceof org.hl7.elm.r1.ToTime) {
            return map((org.hl7.elm.r1.ToTime) element);
        } else if (element instanceof org.hl7.elm.r1.HighBoundary) {
            return map((org.hl7.elm.r1.HighBoundary) element);
        } else if (element instanceof org.hl7.elm.r1.Ln) {
            return map((org.hl7.elm.r1.Ln) element);
        } else if (element instanceof org.hl7.elm.r1.ToDate) {
            return map((org.hl7.elm.r1.ToDate) element);
        } else if (element instanceof org.hl7.elm.r1.TimeFrom) {
            return map((org.hl7.elm.r1.TimeFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Subtract) {
            return map((org.hl7.elm.r1.Subtract) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToTime) {
            return map((org.hl7.elm.r1.ConvertsToTime) element);
        } else if (element instanceof org.hl7.elm.r1.Predecessor) {
            return map((org.hl7.elm.r1.Predecessor) element);
        } else if (element instanceof org.hl7.elm.r1.Negate) {
            return map((org.hl7.elm.r1.Negate) element);
        } else if (element instanceof org.hl7.elm.r1.Before) {
            return map((org.hl7.elm.r1.Before) element);
        } else if (element instanceof org.hl7.elm.r1.Implies) {
            return map((org.hl7.elm.r1.Implies) element);
        } else if (element instanceof org.hl7.elm.r1.PointFrom) {
            return map((org.hl7.elm.r1.PointFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Time) {
            return map((org.hl7.elm.r1.Time) element);
        } else if (element instanceof org.hl7.elm.r1.Distinct) {
            return map((org.hl7.elm.r1.Distinct) element);
        } else if (element instanceof org.hl7.elm.r1.DurationBetween) {
            return map((org.hl7.elm.r1.DurationBetween) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToQuantity) {
            return map((org.hl7.elm.r1.ConvertsToQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Split) {
            return map((org.hl7.elm.r1.Split) element);
        } else if (element instanceof org.hl7.elm.r1.ToDateTime) {
            return map((org.hl7.elm.r1.ToDateTime) element);
        } else if (element instanceof org.hl7.elm.r1.Today) {
            return map((org.hl7.elm.r1.Today) element);
        } else if (element instanceof org.hl7.elm.r1.Equivalent) {
            return map((org.hl7.elm.r1.Equivalent) element);
        } else if (element instanceof org.hl7.elm.r1.Round) {
            return map((org.hl7.elm.r1.Round) element);
        } else if (element instanceof org.hl7.elm.r1.DateTime) {
            return map((org.hl7.elm.r1.DateTime) element);
        } else if (element instanceof org.hl7.elm.r1.InValueSet) {
            return map((org.hl7.elm.r1.InValueSet) element);
        } else if (element instanceof org.hl7.elm.r1.Floor) {
            return map((org.hl7.elm.r1.Floor) element);
        } else if (element instanceof org.hl7.elm.r1.CalculateAgeAt) {
            return map((org.hl7.elm.r1.CalculateAgeAt) element);
        } else if (element instanceof org.hl7.elm.r1.LastPositionOf) {
            return map((org.hl7.elm.r1.LastPositionOf) element);
        } else if (element instanceof org.hl7.elm.r1.Last) {
            return map((org.hl7.elm.r1.Last) element);
        } else if (element instanceof org.hl7.elm.r1.Matches) {
            return map((org.hl7.elm.r1.Matches) element);
        } else if (element instanceof org.hl7.elm.r1.Times) {
            return map((org.hl7.elm.r1.Times) element);
        } else if (element instanceof org.hl7.elm.r1.Contains) {
            return map((org.hl7.elm.r1.Contains) element);
        } else if (element instanceof org.hl7.elm.r1.And) {
            return map((org.hl7.elm.r1.And) element);
        } else if (element instanceof org.hl7.elm.r1.ProperIncludedIn) {
            return map((org.hl7.elm.r1.ProperIncludedIn) element);
        } else if (element instanceof org.hl7.elm.r1.UnaryExpression) {
            return map((org.hl7.elm.r1.UnaryExpression) element);
        } else if (element instanceof org.hl7.elm.r1.NaryExpression) {
            return map((org.hl7.elm.r1.NaryExpression) element);
        } else if (element instanceof org.hl7.elm.r1.TernaryExpression) {
            return map((org.hl7.elm.r1.TernaryExpression) element);
        } else if (element instanceof org.hl7.elm.r1.BinaryExpression) {
            return map((org.hl7.elm.r1.BinaryExpression) element);
        }

        throw new IllegalArgumentException(
                "unknown class of org.hl7.elm.r1.OperatorExpression: " + element.getClass().getName());
    }

    default org.cqframework.cql.elm.execution.TypeSpecifier map(org.hl7.elm.r1.TypeSpecifier element) {
        if (element == null) {
            return null;
        }

        if (element instanceof org.hl7.elm.r1.ParameterTypeSpecifier) {
            return map((org.hl7.elm.r1.ParameterTypeSpecifier) element);
        } else if (element instanceof org.hl7.elm.r1.IntervalTypeSpecifier) {
            return map((org.hl7.elm.r1.IntervalTypeSpecifier) element);
        } else if (element instanceof org.hl7.elm.r1.ListTypeSpecifier) {
            return map((org.hl7.elm.r1.ListTypeSpecifier) element);
        } else if (element instanceof org.hl7.elm.r1.ChoiceTypeSpecifier) {
            return map((org.hl7.elm.r1.ChoiceTypeSpecifier) element);
        } else if (element instanceof org.hl7.elm.r1.NamedTypeSpecifier) {
            return map((org.hl7.elm.r1.NamedTypeSpecifier) element);
        } else if (element instanceof org.hl7.elm.r1.TupleTypeSpecifier) {
            return map((org.hl7.elm.r1.TupleTypeSpecifier) element);
        }

        throw new IllegalArgumentException(
                "unknown class of org.hl7.elm.r1.TypeSpecifier: " + element.getClass().getName());
    }

    default org.cqframework.cql.elm.execution.TernaryExpression map(org.hl7.elm.r1.TernaryExpression element) {
        if (element == null) {
            return null;
        }

        if (element instanceof org.hl7.elm.r1.ReplaceMatches) {
            return map((org.hl7.elm.r1.ReplaceMatches) element);
        }

        throw new IllegalArgumentException(
                "unknown class of org.hl7.elm.r1.TernaryExpression: " + element.getClass().getName());
    }

    default org.cqframework.cql.elm.execution.BinaryExpression map(org.hl7.elm.r1.BinaryExpression element) {
        if (element == null) {
            return null;
        }

        if (element instanceof org.hl7.elm.r1.CanConvertQuantity) {
            return map((org.hl7.elm.r1.CanConvertQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Meets) {
            return map((org.hl7.elm.r1.Meets) element);
        } else if (element instanceof org.hl7.elm.r1.DifferenceBetween) {
            return map((org.hl7.elm.r1.DifferenceBetween) element);
        } else if (element instanceof org.hl7.elm.r1.IncludedIn) {
            return map((org.hl7.elm.r1.IncludedIn) element);
        } else if (element instanceof org.hl7.elm.r1.Or) {
            return map((org.hl7.elm.r1.Or) element);
        } else if (element instanceof org.hl7.elm.r1.LessOrEqual) {
            return map((org.hl7.elm.r1.LessOrEqual) element);
        } else if (element instanceof org.hl7.elm.r1.OverlapsAfter) {
            return map((org.hl7.elm.r1.OverlapsAfter) element);
        } else if (element instanceof org.hl7.elm.r1.ProperContains) {
            return map((org.hl7.elm.r1.ProperContains) element);
        } else if (element instanceof org.hl7.elm.r1.SameAs) {
            return map((org.hl7.elm.r1.SameAs) element);
        } else if (element instanceof org.hl7.elm.r1.EndsWith) {
            return map((org.hl7.elm.r1.EndsWith) element);
        } else if (element instanceof org.hl7.elm.r1.Xor) {
            return map((org.hl7.elm.r1.Xor) element);
        } else if (element instanceof org.hl7.elm.r1.Starts) {
            return map((org.hl7.elm.r1.Starts) element);
        } else if (element instanceof org.hl7.elm.r1.Power) {
            return map((org.hl7.elm.r1.Power) element);
        } else if (element instanceof org.hl7.elm.r1.Equal) {
            return map((org.hl7.elm.r1.Equal) element);
        } else if (element instanceof org.hl7.elm.r1.Subsumes) {
            return map((org.hl7.elm.r1.Subsumes) element);
        } else if (element instanceof org.hl7.elm.r1.GreaterOrEqual) {
            return map((org.hl7.elm.r1.GreaterOrEqual) element);
        } else if (element instanceof org.hl7.elm.r1.SubsumedBy) {
            return map((org.hl7.elm.r1.SubsumedBy) element);
        } else if (element instanceof org.hl7.elm.r1.Add) {
            return map((org.hl7.elm.r1.Add) element);
        } else if (element instanceof org.hl7.elm.r1.Multiply) {
            return map((org.hl7.elm.r1.Multiply) element);
        } else if (element instanceof org.hl7.elm.r1.SameOrAfter) {
            return map((org.hl7.elm.r1.SameOrAfter) element);
        } else if (element instanceof org.hl7.elm.r1.Expand) {
            return map((org.hl7.elm.r1.Expand) element);
        } else if (element instanceof org.hl7.elm.r1.ProperIn) {
            return map((org.hl7.elm.r1.ProperIn) element);
        } else if (element instanceof org.hl7.elm.r1.StartsWith) {
            return map((org.hl7.elm.r1.StartsWith) element);
        } else if (element instanceof org.hl7.elm.r1.LowBoundary) {
            return map((org.hl7.elm.r1.LowBoundary) element);
        } else if (element instanceof org.hl7.elm.r1.Less) {
            return map((org.hl7.elm.r1.Less) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertQuantity) {
            return map((org.hl7.elm.r1.ConvertQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Includes) {
            return map((org.hl7.elm.r1.Includes) element);
        } else if (element instanceof org.hl7.elm.r1.Modulo) {
            return map((org.hl7.elm.r1.Modulo) element);
        } else if (element instanceof org.hl7.elm.r1.Collapse) {
            return map((org.hl7.elm.r1.Collapse) element);
        } else if (element instanceof org.hl7.elm.r1.MeetsAfter) {
            return map((org.hl7.elm.r1.MeetsAfter) element);
        } else if (element instanceof org.hl7.elm.r1.Overlaps) {
            return map((org.hl7.elm.r1.Overlaps) element);
        } else if (element instanceof org.hl7.elm.r1.Indexer) {
            return map((org.hl7.elm.r1.Indexer) element);
        } else if (element instanceof org.hl7.elm.r1.NotEqual) {
            return map((org.hl7.elm.r1.NotEqual) element);
        } else if (element instanceof org.hl7.elm.r1.Ends) {
            return map((org.hl7.elm.r1.Ends) element);
        } else if (element instanceof org.hl7.elm.r1.TruncatedDivide) {
            return map((org.hl7.elm.r1.TruncatedDivide) element);
        } else if (element instanceof org.hl7.elm.r1.SameOrBefore) {
            return map((org.hl7.elm.r1.SameOrBefore) element);
        } else if (element instanceof org.hl7.elm.r1.In) {
            return map((org.hl7.elm.r1.In) element);
        } else if (element instanceof org.hl7.elm.r1.MeetsBefore) {
            return map((org.hl7.elm.r1.MeetsBefore) element);
        } else if (element instanceof org.hl7.elm.r1.Log) {
            return map((org.hl7.elm.r1.Log) element);
        } else if (element instanceof org.hl7.elm.r1.Greater) {
            return map((org.hl7.elm.r1.Greater) element);
        } else if (element instanceof org.hl7.elm.r1.ProperIncludes) {
            return map((org.hl7.elm.r1.ProperIncludes) element);
        } else if (element instanceof org.hl7.elm.r1.Divide) {
            return map((org.hl7.elm.r1.Divide) element);
        } else if (element instanceof org.hl7.elm.r1.OverlapsBefore) {
            return map((org.hl7.elm.r1.OverlapsBefore) element);
        } else if (element instanceof org.hl7.elm.r1.After) {
            return map((org.hl7.elm.r1.After) element);
        } else if (element instanceof org.hl7.elm.r1.HighBoundary) {
            return map((org.hl7.elm.r1.HighBoundary) element);
        } else if (element instanceof org.hl7.elm.r1.Subtract) {
            return map((org.hl7.elm.r1.Subtract) element);
        } else if (element instanceof org.hl7.elm.r1.Before) {
            return map((org.hl7.elm.r1.Before) element);
        } else if (element instanceof org.hl7.elm.r1.Implies) {
            return map((org.hl7.elm.r1.Implies) element);
        } else if (element instanceof org.hl7.elm.r1.DurationBetween) {
            return map((org.hl7.elm.r1.DurationBetween) element);
        } else if (element instanceof org.hl7.elm.r1.Equivalent) {
            return map((org.hl7.elm.r1.Equivalent) element);
        } else if (element instanceof org.hl7.elm.r1.CalculateAgeAt) {
            return map((org.hl7.elm.r1.CalculateAgeAt) element);
        } else if (element instanceof org.hl7.elm.r1.Matches) {
            return map((org.hl7.elm.r1.Matches) element);
        } else if (element instanceof org.hl7.elm.r1.Times) {
            return map((org.hl7.elm.r1.Times) element);
        } else if (element instanceof org.hl7.elm.r1.Contains) {
            return map((org.hl7.elm.r1.Contains) element);
        } else if (element instanceof org.hl7.elm.r1.And) {
            return map((org.hl7.elm.r1.And) element);
        } else if (element instanceof org.hl7.elm.r1.ProperIncludedIn) {
            return map((org.hl7.elm.r1.ProperIncludedIn) element);
        }

        throw new IllegalArgumentException(
                "unknown class of org.hl7.elm.r1.BinaryExpression: " + element.getClass().getName());
    }

    default org.cqframework.cql.elm.execution.Element map(org.hl7.elm.r1.Element element) {
        if (element == null) {
            return null;
        }

        if (element instanceof org.hl7.elm.r1.First) {
            return map((org.hl7.elm.r1.First) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToBoolean) {
            return map((org.hl7.elm.r1.ConvertsToBoolean) element);
        } else if (element instanceof org.hl7.elm.r1.CanConvertQuantity) {
            return map((org.hl7.elm.r1.CanConvertQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Meets) {
            return map((org.hl7.elm.r1.Meets) element);
        } else if (element instanceof org.hl7.elm.r1.ToString) {
            return map((org.hl7.elm.r1.ToString) element);
        } else if (element instanceof org.hl7.elm.r1.DateFilterElement) {
            return map((org.hl7.elm.r1.DateFilterElement) element);
        } else if (element instanceof org.hl7.elm.r1.MinValue) {
            return map((org.hl7.elm.r1.MinValue) element);
        } else if (element instanceof org.hl7.elm.r1.DifferenceBetween) {
            return map((org.hl7.elm.r1.DifferenceBetween) element);
        } else if (element instanceof org.hl7.elm.r1.Exists) {
            return map((org.hl7.elm.r1.Exists) element);
        } else if (element instanceof org.hl7.elm.r1.CanConvert) {
            return map((org.hl7.elm.r1.CanConvert) element);
        } else if (element instanceof org.hl7.elm.r1.ReturnClause) {
            return map((org.hl7.elm.r1.ReturnClause) element);
        } else if (element instanceof org.hl7.elm.r1.IncludedIn) {
            return map((org.hl7.elm.r1.IncludedIn) element);
        } else if (element instanceof org.hl7.elm.r1.Or) {
            return map((org.hl7.elm.r1.Or) element);
        } else if (element instanceof org.hl7.elm.r1.Current) {
            return map((org.hl7.elm.r1.Current) element);
        } else if (element instanceof org.hl7.elm.r1.Substring) {
            return map((org.hl7.elm.r1.Substring) element);
        } else if (element instanceof org.hl7.elm.r1.ToBoolean) {
            return map((org.hl7.elm.r1.ToBoolean) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToLong) {
            return map((org.hl7.elm.r1.ConvertsToLong) element);
        } else if (element instanceof org.hl7.elm.r1.Slice) {
            return map((org.hl7.elm.r1.Slice) element);
        } else if (element instanceof org.hl7.elm.r1.LessOrEqual) {
            return map((org.hl7.elm.r1.LessOrEqual) element);
        } else if (element instanceof org.hl7.elm.r1.Exp) {
            return map((org.hl7.elm.r1.Exp) element);
        } else if (element instanceof org.hl7.elm.r1.OverlapsAfter) {
            return map((org.hl7.elm.r1.OverlapsAfter) element);
        } else if (element instanceof org.hl7.elm.r1.Repeat) {
            return map((org.hl7.elm.r1.Repeat) element);
        } else if (element instanceof org.hl7.elm.r1.CodeSystemRef) {
            return map((org.hl7.elm.r1.CodeSystemRef) element);
        } else if (element instanceof org.hl7.elm.r1.Flatten) {
            return map((org.hl7.elm.r1.Flatten) element);
        } else if (element instanceof org.hl7.elm.r1.Lower) {
            return map((org.hl7.elm.r1.Lower) element);
        } else if (element instanceof org.hl7.elm.r1.Precision) {
            return map((org.hl7.elm.r1.Precision) element);
        } else if (element instanceof org.hl7.elm.r1.IndexOf) {
            return map((org.hl7.elm.r1.IndexOf) element);
        } else if (element instanceof org.hl7.elm.r1.ProperContains) {
            return map((org.hl7.elm.r1.ProperContains) element);
        } else if (element instanceof org.hl7.elm.r1.Length) {
            return map((org.hl7.elm.r1.Length) element);
        } else if (element instanceof org.hl7.elm.r1.OperandDef) {
            return map((org.hl7.elm.r1.OperandDef) element);
        } else if (element instanceof org.hl7.elm.r1.FunctionDef) {
            return map((org.hl7.elm.r1.FunctionDef) element);
        } else if (element instanceof org.hl7.elm.r1.PositionOf) {
            return map((org.hl7.elm.r1.PositionOf) element);
        } else if (element instanceof org.hl7.elm.r1.Code) {
            return map((org.hl7.elm.r1.Code) element);
        } else if (element instanceof org.hl7.elm.r1.UsingDef) {
            return map((org.hl7.elm.r1.UsingDef) element);
        } else if (element instanceof org.hl7.elm.r1.Successor) {
            return map((org.hl7.elm.r1.Successor) element);
        } else if (element instanceof org.hl7.elm.r1.SplitOnMatches) {
            return map((org.hl7.elm.r1.SplitOnMatches) element);
        } else if (element instanceof org.hl7.elm.r1.Retrieve) {
            return map((org.hl7.elm.r1.Retrieve) element);
        } else if (element instanceof org.hl7.elm.r1.CodeDef) {
            return map((org.hl7.elm.r1.CodeDef) element);
        } else if (element instanceof org.hl7.elm.r1.Width) {
            return map((org.hl7.elm.r1.Width) element);
        } else if (element instanceof org.hl7.elm.r1.ToInteger) {
            return map((org.hl7.elm.r1.ToInteger) element);
        } else if (element instanceof org.hl7.elm.r1.ParameterDef) {
            return map((org.hl7.elm.r1.ParameterDef) element);
        } else if (element instanceof org.hl7.elm.r1.SameAs) {
            return map((org.hl7.elm.r1.SameAs) element);
        } else if (element instanceof org.hl7.elm.r1.Descendents) {
            return map((org.hl7.elm.r1.Descendents) element);
        } else if (element instanceof org.hl7.elm.r1.ToChars) {
            return map((org.hl7.elm.r1.ToChars) element);
        } else if (element instanceof org.hl7.elm.r1.SingletonFrom) {
            return map((org.hl7.elm.r1.SingletonFrom) element);
        } else if (element instanceof org.hl7.elm.r1.SortClause) {
            return map((org.hl7.elm.r1.SortClause) element);
        } else if (element instanceof org.hl7.elm.r1.InCodeSystem) {
            return map((org.hl7.elm.r1.InCodeSystem) element);
        } else if (element instanceof org.hl7.elm.r1.Sum) {
            return map((org.hl7.elm.r1.Sum) element);
        } else if (element instanceof org.hl7.elm.r1.Size) {
            return map((org.hl7.elm.r1.Size) element);
        } else if (element instanceof org.hl7.elm.r1.Interval) {
            return map((org.hl7.elm.r1.Interval) element);
        } else if (element instanceof org.hl7.elm.r1.EndsWith) {
            return map((org.hl7.elm.r1.EndsWith) element);
        } else if (element instanceof org.hl7.elm.r1.Tuple) {
            return map((org.hl7.elm.r1.Tuple) element);
        } else if (element instanceof org.hl7.elm.r1.As) {
            return map((org.hl7.elm.r1.As) element);
        } else if (element instanceof org.hl7.elm.r1.ValueSetRef) {
            return map((org.hl7.elm.r1.ValueSetRef) element);
        } else if (element instanceof org.hl7.elm.r1.Xor) {
            return map((org.hl7.elm.r1.Xor) element);
        } else if (element instanceof org.hl7.elm.r1.Combine) {
            return map((org.hl7.elm.r1.Combine) element);
        } else if (element instanceof org.hl7.elm.r1.Starts) {
            return map((org.hl7.elm.r1.Starts) element);
        } else if (element instanceof org.hl7.elm.r1.TimeOfDay) {
            return map((org.hl7.elm.r1.TimeOfDay) element);
        } else if (element instanceof org.hl7.elm.r1.Min) {
            return map((org.hl7.elm.r1.Min) element);
        } else if (element instanceof org.hl7.elm.r1.AggregateClause) {
            return map((org.hl7.elm.r1.AggregateClause) element);
        } else if (element instanceof org.hl7.elm.r1.Power) {
            return map((org.hl7.elm.r1.Power) element);
        } else if (element instanceof org.hl7.elm.r1.ValueSetDef) {
            return map((org.hl7.elm.r1.ValueSetDef) element);
        } else if (element instanceof org.hl7.elm.r1.AnyInValueSet) {
            return map((org.hl7.elm.r1.AnyInValueSet) element);
        } else if (element instanceof org.hl7.elm.r1.Count) {
            return map((org.hl7.elm.r1.Count) element);
        } else if (element instanceof org.hl7.elm.r1.ExpressionRef) {
            return map((org.hl7.elm.r1.ExpressionRef) element);
        } else if (element instanceof org.hl7.elm.r1.Query) {
            return map((org.hl7.elm.r1.Query) element);
        } else if (element instanceof org.hl7.elm.r1.Sort) {
            return map((org.hl7.elm.r1.Sort) element);
        } else if (element instanceof org.hl7.elm.r1.Mode) {
            return map((org.hl7.elm.r1.Mode) element);
        } else if (element instanceof org.hl7.elm.r1.Equal) {
            return map((org.hl7.elm.r1.Equal) element);
        } else if (element instanceof org.hl7.elm.r1.TupleElementDefinition) {
            return map((org.hl7.elm.r1.TupleElementDefinition) element);
        } else if (element instanceof org.hl7.elm.r1.Date) {
            return map((org.hl7.elm.r1.Date) element);
        } else if (element instanceof org.hl7.elm.r1.ForEach) {
            return map((org.hl7.elm.r1.ForEach) element);
        } else if (element instanceof org.hl7.elm.r1.Intersect) {
            return map((org.hl7.elm.r1.Intersect) element);
        } else if (element instanceof org.hl7.elm.r1.OtherFilterElement) {
            return map((org.hl7.elm.r1.OtherFilterElement) element);
        } else if (element instanceof org.hl7.elm.r1.ToRatio) {
            return map((org.hl7.elm.r1.ToRatio) element);
        } else if (element instanceof org.hl7.elm.r1.Abs) {
            return map((org.hl7.elm.r1.Abs) element);
        } else if (element instanceof org.hl7.elm.r1.ParameterTypeSpecifier) {
            return map((org.hl7.elm.r1.ParameterTypeSpecifier) element);
        } else if (element instanceof org.hl7.elm.r1.ConceptRef) {
            return map((org.hl7.elm.r1.ConceptRef) element);
        } else if (element instanceof org.hl7.elm.r1.IdentifierRef) {
            return map((org.hl7.elm.r1.IdentifierRef) element);
        } else if (element instanceof org.hl7.elm.r1.Subsumes) {
            return map((org.hl7.elm.r1.Subsumes) element);
        } else if (element instanceof org.hl7.elm.r1.ToList) {
            return map((org.hl7.elm.r1.ToList) element);
        } else if (element instanceof org.hl7.elm.r1.LetClause) {
            return map((org.hl7.elm.r1.LetClause) element);
        } else if (element instanceof org.hl7.elm.r1.ParameterRef) {
            return map((org.hl7.elm.r1.ParameterRef) element);
        } else if (element instanceof org.hl7.elm.r1.GreaterOrEqual) {
            return map((org.hl7.elm.r1.GreaterOrEqual) element);
        } else if (element instanceof org.hl7.elm.r1.SubsumedBy) {
            return map((org.hl7.elm.r1.SubsumedBy) element);
        } else if (element instanceof org.hl7.elm.r1.Add) {
            return map((org.hl7.elm.r1.Add) element);
        } else if (element instanceof org.hl7.elm.r1.Library) {
            return map((org.hl7.elm.r1.Library) element);
        } else if (element instanceof org.hl7.elm.r1.CalculateAge) {
            return map((org.hl7.elm.r1.CalculateAge) element);
        } else if (element instanceof org.hl7.elm.r1.ContextDef) {
            return map((org.hl7.elm.r1.ContextDef) element);
        } else if (element instanceof org.hl7.elm.r1.IntervalTypeSpecifier) {
            return map((org.hl7.elm.r1.IntervalTypeSpecifier) element);
        } else if (element instanceof org.hl7.elm.r1.ToConcept) {
            return map((org.hl7.elm.r1.ToConcept) element);
        } else if (element instanceof org.hl7.elm.r1.ListTypeSpecifier) {
            return map((org.hl7.elm.r1.ListTypeSpecifier) element);
        } else if (element instanceof org.hl7.elm.r1.Multiply) {
            return map((org.hl7.elm.r1.Multiply) element);
        } else if (element instanceof org.hl7.elm.r1.DateFrom) {
            return map((org.hl7.elm.r1.DateFrom) element);
        } else if (element instanceof org.hl7.elm.r1.SameOrAfter) {
            return map((org.hl7.elm.r1.SameOrAfter) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToInteger) {
            return map((org.hl7.elm.r1.ConvertsToInteger) element);
        } else if (element instanceof org.hl7.elm.r1.ToLong) {
            return map((org.hl7.elm.r1.ToLong) element);
        } else if (element instanceof org.hl7.elm.r1.AllTrue) {
            return map((org.hl7.elm.r1.AllTrue) element);
        } else if (element instanceof org.hl7.elm.r1.ByDirection) {
            return map((org.hl7.elm.r1.ByDirection) element);
        } else if (element instanceof org.hl7.elm.r1.Coalesce) {
            return map((org.hl7.elm.r1.Coalesce) element);
        } else if (element instanceof org.hl7.elm.r1.FunctionRef) {
            return map((org.hl7.elm.r1.FunctionRef) element);
        } else if (element instanceof org.hl7.elm.r1.Expand) {
            return map((org.hl7.elm.r1.Expand) element);
        } else if (element instanceof org.hl7.elm.r1.PopulationVariance) {
            return map((org.hl7.elm.r1.PopulationVariance) element);
        } else if (element instanceof org.hl7.elm.r1.Filter) {
            return map((org.hl7.elm.r1.Filter) element);
        } else if (element instanceof org.hl7.elm.r1.ProperIn) {
            return map((org.hl7.elm.r1.ProperIn) element);
        } else if (element instanceof org.hl7.elm.r1.StartsWith) {
            return map((org.hl7.elm.r1.StartsWith) element);
        } else if (element instanceof org.hl7.elm.r1.IncludeElement) {
            return map((org.hl7.elm.r1.IncludeElement) element);
        } else if (element instanceof org.hl7.elm.r1.Null) {
            return map((org.hl7.elm.r1.Null) element);
        } else if (element instanceof org.hl7.elm.r1.LowBoundary) {
            return map((org.hl7.elm.r1.LowBoundary) element);
        } else if (element instanceof org.hl7.elm.r1.TimezoneFrom) {
            return map((org.hl7.elm.r1.TimezoneFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Convert) {
            return map((org.hl7.elm.r1.Convert) element);
        } else if (element instanceof org.hl7.elm.r1.Less) {
            return map((org.hl7.elm.r1.Less) element);
        } else if (element instanceof org.hl7.elm.r1.Search) {
            return map((org.hl7.elm.r1.Search) element);
        } else if (element instanceof org.hl7.elm.r1.End) {
            return map((org.hl7.elm.r1.End) element);
        } else if (element instanceof org.hl7.elm.r1.QueryLetRef) {
            return map((org.hl7.elm.r1.QueryLetRef) element);
        } else if (element instanceof org.hl7.elm.r1.Now) {
            return map((org.hl7.elm.r1.Now) element);
        } else if (element instanceof org.hl7.elm.r1.Concatenate) {
            return map((org.hl7.elm.r1.Concatenate) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToDate) {
            return map((org.hl7.elm.r1.ConvertsToDate) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertQuantity) {
            return map((org.hl7.elm.r1.ConvertQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Avg) {
            return map((org.hl7.elm.r1.Avg) element);
        } else if (element instanceof org.hl7.elm.r1.Not) {
            return map((org.hl7.elm.r1.Not) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToDateTime) {
            return map((org.hl7.elm.r1.ConvertsToDateTime) element);
        } else if (element instanceof org.hl7.elm.r1.Includes) {
            return map((org.hl7.elm.r1.Includes) element);
        } else if (element instanceof org.hl7.elm.r1.ToDecimal) {
            return map((org.hl7.elm.r1.ToDecimal) element);
        } else if (element instanceof org.hl7.elm.r1.Start) {
            return map((org.hl7.elm.r1.Start) element);
        } else if (element instanceof org.hl7.elm.r1.IsFalse) {
            return map((org.hl7.elm.r1.IsFalse) element);
        } else if (element instanceof org.hl7.elm.r1.Modulo) {
            return map((org.hl7.elm.r1.Modulo) element);
        } else if (element instanceof org.hl7.elm.r1.Collapse) {
            return map((org.hl7.elm.r1.Collapse) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToDecimal) {
            return map((org.hl7.elm.r1.ConvertsToDecimal) element);
        } else if (element instanceof org.hl7.elm.r1.Case) {
            return map((org.hl7.elm.r1.Case) element);
        } else if (element instanceof org.hl7.elm.r1.ConceptDef) {
            return map((org.hl7.elm.r1.ConceptDef) element);
        } else if (element instanceof org.hl7.elm.r1.Instance) {
            return map((org.hl7.elm.r1.Instance) element);
        } else if (element instanceof org.hl7.elm.r1.IsNull) {
            return map((org.hl7.elm.r1.IsNull) element);
        } else if (element instanceof org.hl7.elm.r1.StdDev) {
            return map((org.hl7.elm.r1.StdDev) element);
        } else if (element instanceof org.hl7.elm.r1.IsTrue) {
            return map((org.hl7.elm.r1.IsTrue) element);
        } else if (element instanceof org.hl7.elm.r1.If) {
            return map((org.hl7.elm.r1.If) element);
        } else if (element instanceof org.hl7.elm.r1.Product) {
            return map((org.hl7.elm.r1.Product) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToRatio) {
            return map((org.hl7.elm.r1.ConvertsToRatio) element);
        } else if (element instanceof org.hl7.elm.r1.CodeRef) {
            return map((org.hl7.elm.r1.CodeRef) element);
        } else if (element instanceof org.hl7.elm.r1.ReplaceMatches) {
            return map((org.hl7.elm.r1.ReplaceMatches) element);
        } else if (element instanceof org.hl7.elm.r1.ByExpression) {
            return map((org.hl7.elm.r1.ByExpression) element);
        } else if (element instanceof org.hl7.elm.r1.MeetsAfter) {
            return map((org.hl7.elm.r1.MeetsAfter) element);
        } else if (element instanceof org.hl7.elm.r1.Overlaps) {
            return map((org.hl7.elm.r1.Overlaps) element);
        } else if (element instanceof org.hl7.elm.r1.Ceiling) {
            return map((org.hl7.elm.r1.Ceiling) element);
        } else if (element instanceof org.hl7.elm.r1.AnyInCodeSystem) {
            return map((org.hl7.elm.r1.AnyInCodeSystem) element);
        } else if (element instanceof org.hl7.elm.r1.TimezoneOffsetFrom) {
            return map((org.hl7.elm.r1.TimezoneOffsetFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Indexer) {
            return map((org.hl7.elm.r1.Indexer) element);
        } else if (element instanceof org.hl7.elm.r1.Message) {
            return map((org.hl7.elm.r1.Message) element);
        } else if (element instanceof org.hl7.elm.r1.CaseItem) {
            return map((org.hl7.elm.r1.CaseItem) element);
        } else if (element instanceof org.hl7.elm.r1.Union) {
            return map((org.hl7.elm.r1.Union) element);
        } else if (element instanceof org.hl7.elm.r1.NotEqual) {
            return map((org.hl7.elm.r1.NotEqual) element);
        } else if (element instanceof org.hl7.elm.r1.Children) {
            return map((org.hl7.elm.r1.Children) element);
        } else if (element instanceof org.hl7.elm.r1.Ends) {
            return map((org.hl7.elm.r1.Ends) element);
        } else if (element instanceof org.hl7.elm.r1.DateTimeComponentFrom) {
            return map((org.hl7.elm.r1.DateTimeComponentFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Iteration) {
            return map((org.hl7.elm.r1.Iteration) element);
        } else if (element instanceof org.hl7.elm.r1.IncludeDef) {
            return map((org.hl7.elm.r1.IncludeDef) element);
        } else if (element instanceof org.hl7.elm.r1.Except) {
            return map((org.hl7.elm.r1.Except) element);
        } else if (element instanceof org.hl7.elm.r1.Ratio) {
            return map((org.hl7.elm.r1.Ratio) element);
        } else if (element instanceof org.hl7.elm.r1.Quantity) {
            return map((org.hl7.elm.r1.Quantity) element);
        } else if (element instanceof org.hl7.elm.r1.TruncatedDivide) {
            return map((org.hl7.elm.r1.TruncatedDivide) element);
        } else if (element instanceof org.hl7.elm.r1.ToQuantity) {
            return map((org.hl7.elm.r1.ToQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Upper) {
            return map((org.hl7.elm.r1.Upper) element);
        } else if (element instanceof org.hl7.elm.r1.SameOrBefore) {
            return map((org.hl7.elm.r1.SameOrBefore) element);
        } else if (element instanceof org.hl7.elm.r1.In) {
            return map((org.hl7.elm.r1.In) element);
        } else if (element instanceof org.hl7.elm.r1.MeetsBefore) {
            return map((org.hl7.elm.r1.MeetsBefore) element);
        } else if (element instanceof org.hl7.elm.r1.Log) {
            return map((org.hl7.elm.r1.Log) element);
        } else if (element instanceof org.hl7.elm.r1.Is) {
            return map((org.hl7.elm.r1.Is) element);
        } else if (element instanceof org.hl7.elm.r1.ExpandValueSet) {
            return map((org.hl7.elm.r1.ExpandValueSet) element);
        } else if (element instanceof org.hl7.elm.r1.Greater) {
            return map((org.hl7.elm.r1.Greater) element);
        } else if (element instanceof org.hl7.elm.r1.Truncate) {
            return map((org.hl7.elm.r1.Truncate) element);
        } else if (element instanceof org.hl7.elm.r1.ProperIncludes) {
            return map((org.hl7.elm.r1.ProperIncludes) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToString) {
            return map((org.hl7.elm.r1.ConvertsToString) element);
        } else if (element instanceof org.hl7.elm.r1.Divide) {
            return map((org.hl7.elm.r1.Divide) element);
        } else if (element instanceof org.hl7.elm.r1.OverlapsBefore) {
            return map((org.hl7.elm.r1.OverlapsBefore) element);
        } else if (element instanceof org.hl7.elm.r1.After) {
            return map((org.hl7.elm.r1.After) element);
        } else if (element instanceof org.hl7.elm.r1.ToTime) {
            return map((org.hl7.elm.r1.ToTime) element);
        } else if (element instanceof org.hl7.elm.r1.ChoiceTypeSpecifier) {
            return map((org.hl7.elm.r1.ChoiceTypeSpecifier) element);
        } else if (element instanceof org.hl7.elm.r1.HighBoundary) {
            return map((org.hl7.elm.r1.HighBoundary) element);
        } else if (element instanceof org.hl7.elm.r1.Ln) {
            return map((org.hl7.elm.r1.Ln) element);
        } else if (element instanceof org.hl7.elm.r1.ToDate) {
            return map((org.hl7.elm.r1.ToDate) element);
        } else if (element instanceof org.hl7.elm.r1.TimeFrom) {
            return map((org.hl7.elm.r1.TimeFrom) element);
        } else if (element instanceof org.hl7.elm.r1.Subtract) {
            return map((org.hl7.elm.r1.Subtract) element);
        } else if (element instanceof org.hl7.elm.r1.List) {
            return map((org.hl7.elm.r1.List) element);
        } else if (element instanceof org.hl7.elm.r1.Max) {
            return map((org.hl7.elm.r1.Max) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToTime) {
            return map((org.hl7.elm.r1.ConvertsToTime) element);
        } else if (element instanceof org.hl7.elm.r1.Predecessor) {
            return map((org.hl7.elm.r1.Predecessor) element);
        } else if (element instanceof org.hl7.elm.r1.CodeFilterElement) {
            return map((org.hl7.elm.r1.CodeFilterElement) element);
        } else if (element instanceof org.hl7.elm.r1.Negate) {
            return map((org.hl7.elm.r1.Negate) element);
        } else if (element instanceof org.hl7.elm.r1.Before) {
            return map((org.hl7.elm.r1.Before) element);
        } else if (element instanceof org.hl7.elm.r1.Implies) {
            return map((org.hl7.elm.r1.Implies) element);
        } else if (element instanceof org.hl7.elm.r1.PointFrom) {
            return map((org.hl7.elm.r1.PointFrom) element);
        } else if (element instanceof org.hl7.elm.r1.ByColumn) {
            return map((org.hl7.elm.r1.ByColumn) element);
        } else if (element instanceof org.hl7.elm.r1.Variance) {
            return map((org.hl7.elm.r1.Variance) element);
        } else if (element instanceof org.hl7.elm.r1.Time) {
            return map((org.hl7.elm.r1.Time) element);
        } else if (element instanceof org.hl7.elm.r1.Property) {
            return map((org.hl7.elm.r1.Property) element);
        } else if (element instanceof org.hl7.elm.r1.Distinct) {
            return map((org.hl7.elm.r1.Distinct) element);
        } else if (element instanceof org.hl7.elm.r1.DurationBetween) {
            return map((org.hl7.elm.r1.DurationBetween) element);
        } else if (element instanceof org.hl7.elm.r1.Literal) {
            return map((org.hl7.elm.r1.Literal) element);
        } else if (element instanceof org.hl7.elm.r1.CodeSystemDef) {
            return map((org.hl7.elm.r1.CodeSystemDef) element);
        } else if (element instanceof org.hl7.elm.r1.Without) {
            return map((org.hl7.elm.r1.Without) element);
        } else if (element instanceof org.hl7.elm.r1.ConvertsToQuantity) {
            return map((org.hl7.elm.r1.ConvertsToQuantity) element);
        } else if (element instanceof org.hl7.elm.r1.Concept) {
            return map((org.hl7.elm.r1.Concept) element);
        } else if (element instanceof org.hl7.elm.r1.With) {
            return map((org.hl7.elm.r1.With) element);
        } else if (element instanceof org.hl7.elm.r1.GeometricMean) {
            return map((org.hl7.elm.r1.GeometricMean) element);
        } else if (element instanceof org.hl7.elm.r1.Split) {
            return map((org.hl7.elm.r1.Split) element);
        } else if (element instanceof org.hl7.elm.r1.Aggregate) {
            return map((org.hl7.elm.r1.Aggregate) element);
        } else if (element instanceof org.hl7.elm.r1.ToDateTime) {
            return map((org.hl7.elm.r1.ToDateTime) element);
        } else if (element instanceof org.hl7.elm.r1.Today) {
            return map((org.hl7.elm.r1.Today) element);
        } else if (element instanceof org.hl7.elm.r1.Median) {
            return map((org.hl7.elm.r1.Median) element);
        } else if (element instanceof org.hl7.elm.r1.Equivalent) {
            return map((org.hl7.elm.r1.Equivalent) element);
        } else if (element instanceof org.hl7.elm.r1.Round) {
            return map((org.hl7.elm.r1.Round) element);
        } else if (element instanceof org.hl7.elm.r1.DateTime) {
            return map((org.hl7.elm.r1.DateTime) element);
        } else if (element instanceof org.hl7.elm.r1.MaxValue) {
            return map((org.hl7.elm.r1.MaxValue) element);
        } else if (element instanceof org.hl7.elm.r1.InValueSet) {
            return map((org.hl7.elm.r1.InValueSet) element);
        } else if (element instanceof org.hl7.elm.r1.NamedTypeSpecifier) {
            return map((org.hl7.elm.r1.NamedTypeSpecifier) element);
        } else if (element instanceof org.hl7.elm.r1.Floor) {
            return map((org.hl7.elm.r1.Floor) element);
        } else if (element instanceof org.hl7.elm.r1.PopulationStdDev) {
            return map((org.hl7.elm.r1.PopulationStdDev) element);
        } else if (element instanceof org.hl7.elm.r1.TupleTypeSpecifier) {
            return map((org.hl7.elm.r1.TupleTypeSpecifier) element);
        } else if (element instanceof org.hl7.elm.r1.CalculateAgeAt) {
            return map((org.hl7.elm.r1.CalculateAgeAt) element);
        } else if (element instanceof org.hl7.elm.r1.AliasRef) {
            return map((org.hl7.elm.r1.AliasRef) element);
        } else if (element instanceof org.hl7.elm.r1.LastPositionOf) {
            return map((org.hl7.elm.r1.LastPositionOf) element);
        } else if (element instanceof org.hl7.elm.r1.Last) {
            return map((org.hl7.elm.r1.Last) element);
        } else if (element instanceof org.hl7.elm.r1.Matches) {
            return map((org.hl7.elm.r1.Matches) element);
        } else if (element instanceof org.hl7.elm.r1.Times) {
            return map((org.hl7.elm.r1.Times) element);
        } else if (element instanceof org.hl7.elm.r1.Total) {
            return map((org.hl7.elm.r1.Total) element);
        } else if (element instanceof org.hl7.elm.r1.Contains) {
            return map((org.hl7.elm.r1.Contains) element);
        } else if (element instanceof org.hl7.elm.r1.OperandRef) {
            return map((org.hl7.elm.r1.OperandRef) element);
        } else if (element instanceof org.hl7.elm.r1.And) {
            return map((org.hl7.elm.r1.And) element);
        } else if (element instanceof org.hl7.elm.r1.AnyTrue) {
            return map((org.hl7.elm.r1.AnyTrue) element);
        } else if (element instanceof org.hl7.elm.r1.ProperIncludedIn) {
            return map((org.hl7.elm.r1.ProperIncludedIn) element);
        } else if (element instanceof org.hl7.elm.r1.ExpressionDef) {
            return map((org.hl7.elm.r1.ExpressionDef) element);
        } else if (element instanceof org.hl7.elm.r1.AliasedQuerySource) {
            return map((org.hl7.elm.r1.AliasedQuerySource) element);
        } else if (element instanceof org.hl7.elm.r1.SortByItem) {
            return map((org.hl7.elm.r1.SortByItem) element);
        } else if (element instanceof org.hl7.elm.r1.AggregateExpression) {
            return map((org.hl7.elm.r1.AggregateExpression) element);
        } else if (element instanceof org.hl7.elm.r1.UnaryExpression) {
            return map((org.hl7.elm.r1.UnaryExpression) element);
        } else if (element instanceof org.hl7.elm.r1.RelationshipClause) {
            return map((org.hl7.elm.r1.RelationshipClause) element);
        } else if (element instanceof org.hl7.elm.r1.Expression) {
            return map((org.hl7.elm.r1.Expression) element);
        } else if (element instanceof org.hl7.elm.r1.NaryExpression) {
            return map((org.hl7.elm.r1.NaryExpression) element);
        } else if (element instanceof org.hl7.elm.r1.OperatorExpression) {
            return map((org.hl7.elm.r1.OperatorExpression) element);
        } else if (element instanceof org.hl7.elm.r1.TypeSpecifier) {
            return map((org.hl7.elm.r1.TypeSpecifier) element);
        } else if (element instanceof org.hl7.elm.r1.TernaryExpression) {
            return map((org.hl7.elm.r1.TernaryExpression) element);
        } else if (element instanceof org.hl7.elm.r1.BinaryExpression) {
            return map((org.hl7.elm.r1.BinaryExpression) element);
        }

        throw new IllegalArgumentException("unknown class of org.hl7.elm.r1.Element: " + element.getClass().getName());
    }
}