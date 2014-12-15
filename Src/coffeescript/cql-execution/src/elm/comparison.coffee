{ Expression, UnimplementedExpression } = require './expression'
{ Uncertainty } = require '../datatypes/datatypes'

# TODO: Deconflict w/ definition in interval.coffee
module.exports.Equal = class Equal extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx).map (x) -> Uncertainty.from x
    args[0].equals args[1]

# TODO: Deconflict w/ definition in interval.coffee
module.exports.NotEqual = class NotEqual extends UnimplementedExpression

module.exports.Less = class Less extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx).map (x) -> Uncertainty.from x
    args[0].lessThan args[1]

module.exports.LessOrEqual = class LessOrEqual extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx).map (x) -> Uncertainty.from x
    args[0].lessThanOrEquals args[1]

module.exports.Greater = class Greater extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx).map (x) -> Uncertainty.from x
    args[0].greaterThan args[1]

module.exports.GreaterOrEqual = class GreaterOrEqual extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx).map (x) -> Uncertainty.from x
    args[0].greaterThanOrEquals args[1]
