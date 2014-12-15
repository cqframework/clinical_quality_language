{ Expression, UnimplementedExpression } = require './expression'

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

module.exports.IfNull = class IfNull extends UnimplementedExpression

module.exports.Coalesce = class Coalesce extends UnimplementedExpression
