{ Expression, UnimplementedExpression } = require './expression'

module.exports.Add = class Add extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx).reduce (x,y) -> x + y

module.exports.Subtract = class Subtract extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx).reduce (x,y) -> x - y

module.exports.Multiply = class Multiply extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx).reduce (x,y) -> x * y

module.exports.Divide = class Divide extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx).reduce (x,y) -> x / y

module.exports.TruncatedDivide = class TruncatedDivide extends UnimplementedExpression

module.exports.Modulo = class Modulo extends UnimplementedExpression

module.exports.Ceiling = class Ceiling extends UnimplementedExpression

module.exports.Floor = class Floor extends UnimplementedExpression

module.exports.Truncate = class Truncate extends UnimplementedExpression

module.exports.Abs = class Abs extends UnimplementedExpression

module.exports.Negate = class Negate extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx) * -1

module.exports.Round = class Round extends UnimplementedExpression

module.exports.Ln = class Ln extends UnimplementedExpression

module.exports.Log = class Log extends UnimplementedExpression

module.exports.Power = class Power extends UnimplementedExpression

module.exports.Successor = class Successor extends UnimplementedExpression

module.exports.Predecessor = class Predecessor extends UnimplementedExpression

module.exports.MinValue = class MinValue extends UnimplementedExpression

module.exports.MaxValue = class MaxValue extends UnimplementedExpression
