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
      # A single List (ExpressionRef->List) as the argument is treated differently
      if @args.length == 1 && @args[0].constructor.name in ['List', 'ExpressionRef']
        list = arg.execute(ctx)
        if list?
          for item in list
            if item? then return item
      else
        result = arg.execute(ctx)
        if result? then return result
    null
