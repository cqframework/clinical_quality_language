{ Expression } = require './expression'

module.exports.Null = class Null extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    null

module.exports.IsNull = class IsNull extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    not @execArgs(ctx)?

module.exports.Coalesce = class Coalesce extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    for arg in @args
      # Lists are treated differently
      if arg.elements?
        for item in arg.elements
          result = item.execute(ctx)
          if result? then return result
      else
        result = arg.execute(ctx)
        if result? then return result
    null
