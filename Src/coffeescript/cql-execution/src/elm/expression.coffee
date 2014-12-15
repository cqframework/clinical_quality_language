{ build } = require './builder'
{ typeIsArray } = require '../util/util'

module.exports.Expression = class Expression
  constructor: (json) ->
    if json.operand?
      op = build(json.operand)
      if typeIsArray(json.operand) then @args = op else @arg = op

  exec: (ctx) ->
    this

  execArgs: (ctx) ->
    switch
      when @args? then (arg.exec(ctx) for arg in @args)
      when @arg? then @arg.exec(ctx)
      else null

module.exports.UnimplementedExpression = class UnimplementedExpression extends Expression
  constructor: (@json) ->
    super

  exec: (ctx) ->
    throw new Error("Unimplemented Expression: #{@json.type}")
