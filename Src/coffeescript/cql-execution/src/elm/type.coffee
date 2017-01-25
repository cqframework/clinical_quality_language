{ Expression, UnimplementedExpression } = require './expression'
{ FunctionRef } = require './reusable'
{ DateTime } = require '../datatypes/datetime'
{ parseQuantity } = require './quantity'

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

module.exports.ToDecimal = class ToDecimal extends Expression
 constructor: (json) ->
  super

 exec: (ctx) ->
  arg = @execArgs(ctx)
  if arg? and typeof arg != 'undefined'
   parseFloat(arg.toString())

module.exports.Convert = class Convert extends Expression
  constructor: (json) ->
    super
    @toType = json.toType
    
  exec: (ctx) ->
    arg = @execArgs(ctx)
    if arg? and typeof arg != 'undefined'
      strArg = arg.toString()
      switch @toType
        when "{urn:hl7-org:elm-types:r1}Boolean"
          if strArg=="true"
            true
          else
            false
        when "{urn:hl7-org:elm-types:r1}Decimal" then parseFloat(strArg)
        when "{urn:hl7-org:elm-types:r1}Integer" then parseInt(strArg)
        when "{urn:hl7-org:elm-types:r1}String" then strArg
        when "{urn:hl7-org:elm-types:r1}Quantity" then parseQuantity(strArg)
        when "{urn:hl7-org:elm-types:r1}DateTime" then DateTime.parse(strArg)
        else
          arg
    else
      arg

module.exports.Is = class Is extends UnimplementedExpression

module.exports.IntervalTypeSpecifier = class IntervalTypeSpecifier extends UnimplementedExpression

module.exports.ListTypeSpecifier = class ListTypeSpecifier extends UnimplementedExpression

module.exports.NamedTypeSpecifier = class NamedTypeSpecifier extends UnimplementedExpression

module.exports.TupleTypeSpecifier = class TupleTypeSpecifier extends UnimplementedExpression
