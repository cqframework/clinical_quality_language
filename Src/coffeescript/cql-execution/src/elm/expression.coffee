{ build } = require './builder'
{ typeIsArray } = require '../util/util'

module.exports.Expression = class Expression
  constructor: (json) ->
    if json.operand?
      op = build(json.operand)
      if typeIsArray(json.operand) then @args = op else @arg = op
    if json.localId?
      @localId = json.localId
  
  execute: (ctx) ->
    if @localId?
      ctx.localId_context[@localId] = @exec(ctx)
      ctx.localId_context[@localId]
    else 
      @exec(ctx)

  exec: (ctx) ->
    this

  execArgs: (ctx) ->
    switch
      when @args? then (arg.execute(ctx) for arg in @args)
      when @arg? then @arg.execute(ctx)
      else null

module.exports.UnimplementedExpression = class UnimplementedExpression extends Expression
  constructor: (@json) ->
    super

  exec: (ctx) ->
    throw new Error("Unimplemented Expression: #{@json.type}")
