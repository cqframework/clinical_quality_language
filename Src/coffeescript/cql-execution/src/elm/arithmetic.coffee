{ Expression, UnimplementedExpression } = require './expression'
{ Exception } = require '../datatypes/exception'
{ DateTime } = require '../datatypes/datetime'
{ FunctionRef } = require './reusable'
{ typeIsArray , allTrue, anyTrue} = require '../util/util'

MAX_INT_VALUE = Math.pow(2,31)-1
MIN_INT_VALUE = Math.pow(-2,31)
MAX_FLOAT_VALUE =  ( Math.pow(10,37)-1 ) / Math.pow(10,8)
MIN_FLOAT_VALUE = (Math.pow(-10,37)+1) / Math.pow(10,8)
MIN_FLOAT_PRECISION_VALUE =  Math.pow(10,-8)
MIN_DATE = DateTime.parse("1900-01-01T00:00:00.000")
MAX_DATE = DateTime.parse("9999-12-31T23:59:59.999")



module.exports.OverFlowException = class OverFlowException extends Exception



module.exports.Add = class Add extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if (args.some (x) -> not x?) then null else args.reduce (x,y) -> x + y

module.exports.Subtract = class Subtract extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if (args.some (x) -> not x?) then null else args.reduce (x,y) -> x - y

module.exports.Multiply = class Multiply extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if (args.some (x) -> not x?) then null else args.reduce (x,y) -> x * y

module.exports.Divide = class Divide extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if (args.some (x) -> not x?) then null else args.reduce (x,y) -> x / y

module.exports.TruncatedDivide = class TruncatedDivide extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    Math.floor( @execArgs(ctx).reduce (x,y) -> x / y)

# TODO: Remove functionref when ELM does TruncatedDivide natively
module.exports.TruncatedDivideFunctionRef = class TruncatedDivideFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @trunc = new TruncatedDivide {
      "type" : "TruncatedDivide",
      "operand" : json.operand
    }

  exec: (ctx) ->
    @trunc.exec(ctx)    

module.exports.Modulo = class Modulo extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx).reduce (x,y) -> x % y


# TODO: Remove functionref when ELM does Modulo natively
module.exports.ModuloFunctionRef = class ModuloFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Modulo {
      "type" : "Modulo",
      "operand" : json.operand
    }

  exec: (ctx) ->
    @func.exec(ctx)    


module.exports.Ceiling = class Ceiling extends  Expression 
  constructor: (json) ->
    super

  exec: (ctx) ->
    Math.ceil @execArgs(ctx)

# TODO: Remove functionref when ELM does Modulo natively
module.exports.CeilingFunctionRef = class CeilingFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Ceiling {
      "type" : "Ceiling",
      "operand" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)   

module.exports.Floor = class Floor extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    Math.floor @execArgs(ctx)

# TODO: Remove functionref when ELM does Floor natively
module.exports.FloorFunctionRef = class FloorFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Floor {
      "type" : "Floor",
      "operand" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)   

module.exports.Truncate = class Truncate extends Floor

  # TODO: Remove functionref when ELM does Floor natively
module.exports.TruncateFunctionRef = class TruncateFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Truncate {
      "type" : "Truncate",
      "operand" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)   


module.exports.Abs = class Abs extends  Expression
  constructor: (json) ->
    super
  
  exec: (ctx) ->
    Math.abs @execArgs(ctx)

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
    @execArgs(ctx) * -1


  # TODO: Remove functionref when ELM does Floor natively
module.exports.NegateFunctionRef = class NegateFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Negate {
      "type" : "Negate",
      "operand" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)   
        


module.exports.Round = class Round extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if typeIsArray(args)
      [n,d] = args
      @round_number(n,d)
    else
      Math.round args

  round_number: (num, dec=0) ->
    return Math.round(num * Math.pow(10, dec)) / Math.pow(10, dec)


  # TODO: Remove functionref when ELM does Round natively
module.exports.RoundFunctionRef = class RoundFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Round {
      "type" : "Round",
      "operand" : json.operand
    }

  exec: (ctx) ->
    @func.exec(ctx)   

module.exports.Ln = class Ln extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    Math.log @execArgs(ctx)

  # TODO: Remove functionref when ELM does Round natively
module.exports.LnFunctionRef = class LnFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Ln {
      "type" : "Ln",
      "operand" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)   

module.exports.Log = class Log extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx).reduce (x,y) -> Math.log(x)/Math.log(y)

  # TODO: Remove functionref when ELM does Log natively
module.exports.LogFunctionRef = class LogFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Log {
      "type" : "Log",
      "operand" : json.operand
    }

  exec: (ctx) ->
    @func.exec(ctx)   
    

module.exports.Power = class Power extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx).reduce (x,y) -> Math.pow(x , y)

  # TODO: Remove functionref when ELM does Power natively
module.exports.PowerFunctionRef = class PowerFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Power {
      "type" : "Power",
      "operand" : json.operand
    }

  exec: (ctx) ->
    @func.exec(ctx)   
    

module.exports.MinValue = class MinValue extends Expression
  MIN_VALUES:  "Integer" : MIN_INT_VALUE,"Real" : MIN_FLOAT_VALUE,"DateTime" : MIN_DATE
  constructor: (json) ->
    super

  exec: (ctx) ->
    val = @execArgs(ctx)
    MIN_VALUES[val]

module.exports.MaxValue = class MaxValue extends Expression
   MAX_VALUES: "Integer" : MAX_INT_VALUE, "Real" : MAX_FLOAT_VALUE, "DateTime" : MAX_DATE
  constructor: (json) ->
    super

  exec: (ctx) ->
    val = @execArgs(ctx)
    MAX_VALUES[val]

module.exports.Successor = class Successor extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    val = @execArgs(ctx)
    if typeof val == "number"
      if parseInt(val) == val
        if val == MAX_INT_VALUE 
          throw  new OverFlowException("")
        else  
          val + 1
      else
        #not bothering with the max float test because javascript does not handle floats at the level 
        #very well  
        val + MIN_FLOAT_PRECISION_VALUE
    else if val instanceof DateTime 
      if val.sameAs(MAX_DATE) then throw new OverFlowException() else val.successor()
 

module.exports.Predecessor = class Predecessor extends  Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    val = @execArgs(ctx)
    if typeof val == "number"
      if parseInt(val) == val
        if val == MIN_INT_VALUE 
          throw  new OverFlowException("")
        else  
          val - 1
      else
      #not bothering with the min float test because javascript does not handle floats at the level 
      #very well
        val - MIN_FLOAT_PRECISION_VALUE
    else if val instanceof DateTime
      if val.sameAs(MIN_DATE) then throw new OverFlowException() else val.predecessor()

 