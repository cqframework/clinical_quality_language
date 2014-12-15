{ Expression, UnimplementedExpression } = require './expression'

# TODO: None of these are listed in Logical section of Spec

module.exports.As = class As extends UnimplementedExpression

module.exports.Convert = class Convert extends UnimplementedExpression

module.exports.Is = class Is extends UnimplementedExpression

# TODO: The following aren't defined in the ELM Specification

module.exports.IntervalTypeSpecifier = class IntervalTypeSpecifier extends UnimplementedExpression

module.exports.ListTypeSpecifier = class ListTypeSpecifier extends UnimplementedExpression

module.exports.NamedTypeSpecifier = class NamedTypeSpecifier extends UnimplementedExpression

module.exports.TupleTypeSpecifier = class TupleTypeSpecifier extends UnimplementedExpression
