{ Expression } = require './expression'
{ FunctionRef } = require './reusable'

module.exports.Null = class Null extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    null

module.exports.IsNull = class IsNull extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx) == null

# TODO: Remove functionref when ELM does IsNull natively
module.exports.IsNullFunctionRef = class IsNullFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @isNull = new IsNull {
      "type" : "IsNull",
      "operand" : json.operand[0]
    }

  exec: (ctx) ->
    @isNull.exec(ctx)

module.exports.Coalesce = class Coalesce extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    for arg in @args
      result = arg.exec(ctx)
      if result? then return result
    null


# TODO: Remove functionref when ELM does Coalesce natively
module.exports.CoalesceFunctionRef = class CoalesceFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @coalesce = new Coalesce {
      "type" : "Coalesce",
      "operand" : json.operand
    }

  exec: (ctx) ->
    @coalesce.exec(ctx)
