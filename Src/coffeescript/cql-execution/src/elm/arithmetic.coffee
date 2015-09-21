{ Expression } = require './expression'
{ typeIsArray , allTrue, anyTrue} = require '../util/util'
{ build } = require './builder'
MathUtil = require '../util/math'
Quantity = require('./quantity')

module.exports.Add = class Add extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if (not args? || args.some (x) -> not x?)
      null
    else
      args?.reduce (x,y) ->
        if x.constructor.name == 'Quantity'  or x.constructor.name == 'DateTime'
          Quantity.doAddition(x,y)
        else
          x + y

module.exports.Subtract = class Subtract extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if (not args? || args.some (x) -> not x?)
      null
    else
      args.reduce (x,y) ->
        if x.constructor.name == 'Quantity' or x.constructor.name == 'DateTime'
          Quantity.doSubtraction(x,y)
        else
          x - y

module.exports.Multiply = class Multiply extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if (not args? || args.some (x) -> not x?)
      null
    else
      args?.reduce (x,y) ->
        if x.constructor.name == 'Quantity' or y.constructor.name == 'Quantity'
          Quantity.doMultiplication(x,y)
        else
          x * y

module.exports.Divide = class Divide extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if (not args? || args.some (x) -> not x?)
      null
    else
      args?.reduce (x,y) ->
        if x.constructor.name == 'Quantity'
          Quantity.doDivision(x,y)
        else
          x / y

module.exports.TruncatedDivide = class TruncatedDivide extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if (not args? || args.some (x) -> not x?)
      null
    else
      Math.floor( args.reduce (x,y) -> x / y)

module.exports.Modulo = class Modulo extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if (not args? || args.some (x) -> not x?)
      null
    else
      args.reduce (x,y) -> x % y

module.exports.Ceiling = class Ceiling extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs(ctx)
    if (not arg?)
      null
    else
      Math.ceil arg

module.exports.Floor = class Floor extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs(ctx)
    if (not arg?)
      null
    else
      Math.floor arg

module.exports.Truncate = class Truncate extends Floor

module.exports.Abs = class Abs extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs(ctx)
    if (not arg?)
      null
    else if arg.constructor.name == 'Quantity'
      Quantity.createQuantity( Math.abs(arg.value), arg.unit)
    else
      Math.abs arg

  # TODO: Remove functionref when ELM does Floor natively
module.exports.AbsFunctionRef = class AbsFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Abs {
      "type" : "Abs",
      "operand" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)

module.exports.Negate = class Negate extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs(ctx)
    if (not arg?)
      null
    else if arg.constructor.name == 'Quantity'
      Quantity.createQuantity(arg.value * -1, arg.unit)
    else
      args * -1


module.exports.Round = class Round extends  Expression
  constructor: (json) ->
    super
    @precision = build json.precision

  exec: (ctx) ->
    arg = @execArgs(ctx)
    if (not arg?)
      null
    else
      dec = if @precision? then @precision.execute(ctx) else 0
      Math.round(arg * Math.pow(10, dec)) / Math.pow(10, dec)

module.exports.Ln = class Ln extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs(ctx)
    if (not arg?)
      null
    else
      Math.log arg

module.exports.Exp = class Exp extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs(ctx)
    if (not arg?)
      null
    else
      Math.exp arg

module.exports.Log = class Log extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if (not args? || args.some (x) -> not x?)
      null
    else
      args.reduce (x,y) -> Math.log(x)/Math.log(y)

module.exports.Power = class Power extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if (not args? || args.some (x) -> not x?)
      null
    else
      args.reduce (x,y) -> Math.pow(x , y)


module.exports.MinValue = class MinValue extends Expression
  MIN_VALUES: "Integer" : MathUtil.MIN_INT_VALUE, "Real" : MathUtil.MIN_FLOAT_VALUE, "DateTime" : MathUtil.MIN_DATE_VALUE
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs(ctx)
    if (not arg?)
      null
    else
      MIN_VALUES[arg]

module.exports.MaxValue = class MaxValue extends Expression
  MAX_VALUES: "Integer" : MathUtil.MAX_INT_VALUE, "Real" :MathUtil. MAX_FLOAT_VALUE, "DateTime" : MathUtil.MAX_DATE_VALUE
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs(ctx)
    if (not arg?)
      null
    else
      MAX_VALUES[arg]

module.exports.Successor = class Successor extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs(ctx)
    if (not arg?)
      null
    else
      MathUtil.successor arg

module.exports.Predecessor = class Predecessor extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs(ctx)
    if (not arg?)
      null
    else
      MathUtil.predecessor arg
