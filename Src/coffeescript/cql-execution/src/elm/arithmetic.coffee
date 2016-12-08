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
    if (args?.some (x) -> not x?)
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
    if (args.some (x) -> not x?)
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
    if (args?.some (x) -> not x?)
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
   if (args?.some (x) -> not x?)
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
    Math.floor( @execArgs(ctx).reduce (x,y) -> x / y)

module.exports.Modulo = class Modulo extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx).reduce (x,y) -> x % y

module.exports.Ceiling = class Ceiling extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    Math.ceil @execArgs(ctx)

module.exports.Floor = class Floor extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    Math.floor @execArgs(ctx)

module.exports.Truncate = class Truncate extends Floor

module.exports.Abs = class Abs extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if args?.constructor.name == 'Quantity'
      Quantity.createQuantity( Math.abs(args.value), args.unit)
    else
      Math.abs args

module.exports.Negate = class Negate extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if args?.constructor.name == 'Quantity'
      Quantity.createQuantity(args.value * -1,args.unit)
    else
      args * -1

module.exports.Round = class Round extends  Expression
  constructor: (json) ->
    super
    @precision = build json.precision

  exec: (ctx) ->
    num = @execArgs(ctx)
    dec = if @precision? then @precision.exec(ctx) else 0
    Math.round(num * Math.pow(10, dec)) / Math.pow(10, dec)

module.exports.Ln = class Ln extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    Math.log @execArgs(ctx)

module.exports.Exp = class Exp extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    Math.exp @execArgs(ctx)

module.exports.Log = class Log extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx).reduce (x,y) -> Math.log(x)/Math.log(y)

module.exports.Power = class Power extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx).reduce (x,y) -> Math.pow(x , y)


module.exports.MinValue = class MinValue extends Expression
  MIN_VALUES: "Integer" : MathUtil.MIN_INT_VALUE, "Real" : MathUtil.MIN_FLOAT_VALUE, "DateTime" : MathUtil.MIN_DATE_VALUE
  constructor: (json) ->
    super

  exec: (ctx) ->
    val = @execArgs(ctx)
    MIN_VALUES[val]

module.exports.MaxValue = class MaxValue extends Expression
  MAX_VALUES: "Integer" : MathUtil.MAX_INT_VALUE, "Real" :MathUtil. MAX_FLOAT_VALUE, "DateTime" : MathUtil.MAX_DATE_VALUE
  constructor: (json) ->
    super

  exec: (ctx) ->
    val = @execArgs(ctx)
    MAX_VALUES[val]

module.exports.Successor = class Successor extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    MathUtil.successor @execArgs(ctx)

module.exports.Predecessor = class Predecessor extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    MathUtil.predecessor @execArgs(ctx)
