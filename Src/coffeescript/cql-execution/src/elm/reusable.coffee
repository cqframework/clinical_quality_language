{ Expression, UnimplementedExpression } = require './expression'
{ build } = require './builder'

module.exports.ExpressionDef = class ExpressionDef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @context = json.context
    @expression = build json.expression

  exec: (ctx) ->
    value = @expression?.exec(ctx)
    ctx.rootContext().set @name,value
    value

module.exports.ExpressionRef = class ExpressionRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    value = ctx.get(@name)
    if value instanceof Expression
      value = value.exec(ctx)
    value

module.exports.FunctionDef = class FunctionDef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @expression = build json.expression
    @parameters = json.parameter

  exec: (ctx) ->
    @

module.exports.FunctionRef = class FunctionRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    functionDef = ctx.get(@name)
    args = @execArgs(ctx)
    child_ctx = ctx.childContext()
    if args.length != functionDef.parameters.length
      throw new Error("incorrect number of arguments supplied")
    for p, i in functionDef.parameters
      child_ctx.set(p.name,args[i])
    functionDef.expression.exec(child_ctx)

module.exports.IdentifierRef = class IdentifierRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    ctx.get(@name)
