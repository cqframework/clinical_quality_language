{ Expression, UnimplementedExpression } = require './expression'

module.exports.As = class As extends Expression
  constructor: (json) ->
    @asType = json.asType
    @asTypeSpecifier = json.asTypeSpecifier
    @strict = json.strict ? false

  exec: (ctx) ->
    # TODO: Currently just returns the arg (which works for null, but probably not others)
    @execArgs(ctx)


module.exports.Convert = class Convert extends UnimplementedExpression

module.exports.Is = class Is extends UnimplementedExpression

module.exports.IntervalTypeSpecifier = class IntervalTypeSpecifier extends UnimplementedExpression

module.exports.ListTypeSpecifier = class ListTypeSpecifier extends UnimplementedExpression

module.exports.NamedTypeSpecifier = class NamedTypeSpecifier extends UnimplementedExpression

module.exports.TupleTypeSpecifier = class TupleTypeSpecifier extends UnimplementedExpression
