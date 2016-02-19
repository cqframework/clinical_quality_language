{ Expression, UnimplementedExpression } = require './expression'
{ FunctionRef } = require './reusable'
{ DateTime } = require '../datatypes/datetime'

# TODO: Casting and Conversion needs unit tests!

module.exports.As = class As extends Expression
  constructor: (json) ->
    super
    @asType = json.asType
    @asTypeSpecifier = json.asTypeSpecifier
    @strict = json.strict ? false

  exec: (ctx) ->
    # TODO: Currently just returns the arg (which works for null, but probably not others)
    @execArgs(ctx)

module.exports.ToDateTime = class ToDateTime extends FunctionRef
  constructor: (json) ->
    super

  exec: (ctx) ->
    ary = @execArgs ctx
    if ary.length > 0  and ary[0]? then DateTime.parse(ary[0]) else null

module.exports.Convert = class Convert extends Expression
  constructor: (json) ->
    super
    @toType = json.toType
    
  exec: (ctx) ->
    @execArgs(ctx)

module.exports.Is = class Is extends UnimplementedExpression

module.exports.IntervalTypeSpecifier = class IntervalTypeSpecifier extends UnimplementedExpression

module.exports.ListTypeSpecifier = class ListTypeSpecifier extends UnimplementedExpression

module.exports.NamedTypeSpecifier = class NamedTypeSpecifier extends UnimplementedExpression

module.exports.TupleTypeSpecifier = class TupleTypeSpecifier extends UnimplementedExpression
