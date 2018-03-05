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
      result = arg.execute(ctx)
      # if a single arg that's a list, coalesce over the list
      if @args.length == 1 && Array.isArray(result)
        for item in result
          if item? then return item
      else
        if result? then return result
    null
