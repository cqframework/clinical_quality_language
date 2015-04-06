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

module.exports.ToStringFunctionRef = class ToStringFunctionRef extends FunctionRef
  constructor: (json) ->
    super

  exec: (ctx) ->
    ary = @execArgs ctx
    if ary.length > 0  and ary[0]? then ary[0].toString() else null

module.exports.ToBooleanFunctionRef = class ToBooleanFunctionRef extends FunctionRef
  constructor: (json) ->
    super

  exec: (ctx) ->
    ary = @execArgs ctx
    if ary.length > 0 and ary[0]?
      switch ary[0]
        when 'true' then true
        when 'false' then false
        else null
    else
      null

module.exports.ToIntegerFunctionRef = class ToIntegerFunctionRef extends FunctionRef
  constructor: (json) ->
    super

  exec: (ctx) ->
    ary = @execArgs ctx
    if ary.length > 0  and ary[0]? then parseInt(ary[0]) else null

module.exports.ToDecimalFunctionRef = class ToDecimalFunctionRef extends FunctionRef
  constructor: (json) ->
    super

  exec: (ctx) ->
    ary = @execArgs ctx
    if ary.length > 0 and ary[0]?
      if typeof ary[0] is 'number' then ary[0] else parseFloat(ary[0])
    else
      null

module.exports.ToDateTime = class ToDateTime extends FunctionRef
  constructor: (json) ->
    super

  exec: (ctx) ->
    ary = @execArgs ctx
    if ary.length > 0  and ary[0]? then DateTime.parse(ary[0]) else null

module.exports.Convert = class Convert extends UnimplementedExpression

module.exports.Is = class Is extends UnimplementedExpression

module.exports.IntervalTypeSpecifier = class IntervalTypeSpecifier extends UnimplementedExpression

module.exports.ListTypeSpecifier = class ListTypeSpecifier extends UnimplementedExpression

module.exports.NamedTypeSpecifier = class NamedTypeSpecifier extends UnimplementedExpression

module.exports.TupleTypeSpecifier = class TupleTypeSpecifier extends UnimplementedExpression
