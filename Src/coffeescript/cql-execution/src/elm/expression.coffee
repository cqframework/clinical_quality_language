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
      # Store the localId and result on the root context of this library
      execValue = @exec(ctx)
      ctx.rootContext().setLocalIdWithResult @localId, execValue
      execValue
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
