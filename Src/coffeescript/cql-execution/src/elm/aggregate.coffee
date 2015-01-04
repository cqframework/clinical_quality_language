{ Expression, UnimplementedExpression } = require './expression'
{ FunctionRef } = require './reusable'
{ typeIsArray , allTrue, anyTrue, compact, numerical_sort} = require '../util/util'
{ build } = require './builder'

class AggregateExpression extends Expression
  constructor:(json) ->
    super
    @source = build json.source

module.exports.Count = class Count extends AggregateExpression
  constructor:(json) ->
    super

  exec: (ctx) ->
    arg = @source.exec(ctx)
    if typeIsArray(arg)
      compact(arg).length

  # TODO: Remove functionref when ELM does Round natively
module.exports.CountFunctionRef = class CountFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Count {
      "type" : "Count",
      "source" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)       

module.exports.Sum = class Sum extends AggregateExpression
  constructor:(json) ->
    super

  exec: (ctx) ->
    arg = @source.exec(ctx)
    if typeIsArray(arg)
      filtered =  compact(arg)
      if filtered.length == 0 then null else filtered.reduce (x,y) -> x+y

      
  # TODO: Remove functionref when ELM does Sum natively
module.exports.SumFunctionRef = class SumFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Sum {
      "type" : "Sum",
      "source" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)       

module.exports.Min = class Min extends AggregateExpression
  constructor:(json) ->
    super

  exec: (ctx) ->
    arg = @source.exec(ctx)
    if typeIsArray(arg)
      filtered =  numerical_sort(compact(arg),"asc")
      filtered[0]
    


  # TODO: Remove functionref when ELM does Min natively
module.exports.MinFunctionRef = class MinFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Min {
      "type" : "Min",
      "source" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)       


module.exports.Max = class Max extends AggregateExpression
  constructor:(json) ->
    super

  exec: (ctx) ->
    arg = @source.exec(ctx)
    if typeIsArray(arg)
      filtered =  numerical_sort(compact(arg),"desc")
      filtered[0]


  # TODO: Remove functionref when ELM does Min natively
module.exports.MaxFunctionRef = class MaxFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Max {
      "type" : "Max",
      "source" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)       


module.exports.Avg = class Avg extends  AggregateExpression
  constructor:(json) ->
    super

  exec: (ctx) ->
    arg = @source.exec(ctx)
    if typeIsArray(arg)
      filtered = compact(arg)
      return null if filtered.length == 0
      sum = filtered.reduce (x,y) -> x+y
      sum / filtered.length 


  # TODO: Remove functionref when ELM does Avg natively
module.exports.AvgFunctionRef = class AvgFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Avg {
      "type" : "Avg",
      "source" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)       

module.exports.Median = class Median extends AggregateExpression
  constructor:(json) ->
    super

  exec: (ctx) ->
    arg = @source.exec(ctx)
    if typeIsArray(arg)
      filtered =  numerical_sort(compact(arg),"asc")
      if filtered.length == 0
        null
      else if (filtered.length % 2 == 1)
        filtered[(filtered.length - 1) / 2]
      else
        (filtered[(filtered.length / 2) - 1] +
         filtered[(filtered.length / 2)]) / 2

  # TODO: Remove functionref when ELM does Median natively
module.exports.MedianFunctionRef = class MedianFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Median {
      "type" : "Median",
      "source" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)       

module.exports.Mode = class Mode extends AggregateExpression
  constructor:(json) ->
    super

  exec: (ctx) ->
    arg = @source.exec(ctx)
    if typeIsArray(arg)
      filtered = compact(arg)
      mode = @mode(filtered)
      if mode.length == 1 then  mode[0] else numerical_sort(mode)

  mode: (arr) ->
    # returns an array with the modes of arr, i.e. the
    # elements that appear most often in arr
    counts = {}
    for elem in arr
      counts[elem] ||= 0
      counts[elem] += 1
    max = 0
    for key, cnt of counts
      max = cnt if cnt > max
    (key for key, cnt of counts when cnt == max)


  # TODO: Remove functionref when ELM does Mode natively
module.exports.ModeFunctionRef = class ModeFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Mode {
      "type" : "Mode",
      "source" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)       

module.exports.StdDev = class StdDev extends AggregateExpression

  constructor:(json) ->
    super
    @type = "standard_deviation"

  exec: (ctx) ->
    args = @source.exec(ctx)
    if typeIsArray(args) 
      val = compact(args)
      if val.length > 0 then @calculate(val)  else null
  
  calculate: (list) ->
    val = @stats(list)
    if val then val[@type]

  stats:(list) ->
    sum = list.reduce (x,y) -> x+y
    mean = sum / list.length
    sumOfSquares = 0
    
    for sq in list
      sumOfSquares += Math.pow((sq - mean),2)

    std_var = (1/list.length) * sumOfSquares
    pop_var = (1/(list.length-1)) * sumOfSquares
    std_dev = Math.sqrt std_var
    pop_dev = Math.sqrt pop_var
    {standard_variance: std_var, population_variance: pop_var, standard_deviation: std_dev, population_deviation: pop_dev}



  # TODO: Remove functionref when ELM does StdDev natively
module.exports.StdDevFunctionRef = class StdDevFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new StdDev {
      "type" : "StdDev",
      "source" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)       

module.exports.PopulationStdDev = class PopulationStdDev extends StdDev
  constructor:(json) ->
    super
    @type = "population_deviation"

  # TODO: Remove functionref when ELM does PopulationStdDev natively
module.exports.PopulationStdDevFunctionRef = class PopulationStdDevFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new PopulationStdDev {
      "type" : "PopulationStdDev",
      "source" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)       

module.exports.Variance = class Variance extends  StdDev
  constructor:(json) ->
    super
    @type = "standard_variance"

  # TODO: Remove functionref when ELM does Variance natively
module.exports.VarianceFunctionRef = class VarianceFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new Variance {
      "type" : "Variance",
      "source" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)       

module.exports.PopulationVariance = class PopulationVariance extends  StdDev
  constructor:(json) ->
    super
    @type = "population_variance"

  # TODO: Remove functionref when ELM does StdDev natively
module.exports.PopulationVarianceFunctionRef = class PopulationVarianceFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new PopulationVariance {
      "type" : "PopulationVariance",
      "source" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)       

module.exports.AllTrue = class AllTrue extends AggregateExpression
  constructor:(json) ->
    super

  exec: (ctx) ->
    args =@source.exec(ctx)
    allTrue(args)

  # TODO: Remove functionref when ELM does AllTrue natively
module.exports.AllTrueFunctionRef = class AllTrueFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new AllTrue {
      "type" : "AllTrue",
      "source" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)       

module.exports.AnyTrue = class AnyTrue extends AggregateExpression
  constructor:(json) ->
    super

  exec: (ctx) ->
    args = @source.exec(ctx)
    anyTrue(args)

  # TODO: Remove functionref when ELM does AnyTrue natively
module.exports.AnyTrueFunctionRef = class AnyTrueFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @func = new AnyTrue {
      "type" : "AnyTrue",
      "source" : json.operand[0]
    }

  exec: (ctx) ->
    @func.exec(ctx)       
