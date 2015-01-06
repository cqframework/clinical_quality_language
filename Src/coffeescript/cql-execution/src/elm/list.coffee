{ Expression, UnimplementedExpression } = require './expression'
{ FunctionRef } = require './reusable'
{ ValueSet } = require '../datatypes/datatypes'
{ build } = require './builder'
{ typeIsArray } = require '../util/util'

module.exports.List = class List extends Expression
  constructor: (json) ->
    super
    @elements = (build json.element) ? []

  exec: (ctx) ->
    (item.exec(ctx) for item in @elements)

module.exports.Exists = class Exists extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx)?.length > 0

# Equal is completely handled by overloaded#Equal

# NotEqual is completely handled by overloaded#Equal

# TODO: Deconflict w/ definition in interval.coffee
module.exports.Union = class Union extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    # TODO: Support intervals
    @execArgs(ctx).reduce (x, y) -> x.concat y

# TODO: Spec has "Difference" defined, but should this be "Except"? (also deconflict w/ interval.coffee)
module.exports.Except = class Except extends UnimplementedExpression

# TODO: Deconflict w/ definition in interval.coffee
module.exports.Intersect = class Intersect extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    # TODO: Support intervals
    @execArgs(ctx).reduce (x, y) -> (itm for itm in x when itm in y)

# ELM-only, not a product of CQL
module.exports.Times = class Times extends UnimplementedExpression

# ELM-only, not a product of CQL
module.exports.Filter = class Filter extends UnimplementedExpression

module.exports.SingletonFrom = class SingletonFrom extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs ctx
    if arg.length > 1 then throw new Error 'IllegalArgument: \'SingletonFrom\' requires a 0 or 1 arg array'
    else if arg.length is 1 then return arg[0]
    else return null

module.exports.IndexOf = class IndexOf extends UnimplementedExpression

# TODO: Deconflict w/ definition in string.coffee
# module.exports.Indexer = class Indexer extends UnimplementedExpression

# TODO: Deconflict w/ definition in interval.coffee
module.exports.In = class In extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [item, container] = @execArgs(ctx)

    switch
      when typeIsArray container
        return item in container
      when container instanceof ValueSet
        return container.hasCode item

# TODO: Deconflict w/ definition in interval.coffee
module.exports.Contains = class Contains extends UnimplementedExpression

# TODO: Deconflict w/ definition in interval.coffee
module.exports.Includes = class Includes extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    args[0].includes args[1]

# TODO: Deconflict w/ definition in interval.coffee
module.exports.IncludedIn = class IncludedIn extends UnimplementedExpression

# TODO: Deconflict w/ definition in interval.coffee
module.exports.ProperIncludes = class ProperIncludes extends UnimplementedExpression

# TODO: Deconflict w/ definition in interval.coffee
module.exports.ProperIncludedIn = class ProperIncludedIn extends UnimplementedExpression

module.exports.Sort = class Sort
  constructor:(json) ->
    @by = build json?.by

  sort: (values) ->
    self = @
    if @by
      values.sort (a,b) ->
        order = 0
        for item in self.by
          order = item.exec(a,b)
          if order != 0 then break
        order

# ELM-only, not a product of CQL
module.exports.ForEach = class ForEach extends UnimplementedExpression

module.exports.Expand = class Expand extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs(ctx)
    if typeIsArray(arg) and (arg.every (x) -> typeIsArray x)
      arg.reduce ((x, y) -> x.concat(y)), []
    else
      arg

module.exports.Distinct = class Distinct extends Expression
  constructor: (json) ->
    super
    @source = build json.source

  exec: (ctx) ->
    container = {}
    container[itm] = itm for itm in @source.exec(ctx)
    value for key, value of container

# ELM-only, not a product of CQL
module.exports.Current = class Current extends UnimplementedExpression

# TODO: ELM supports 'orderBy' but there's no way to get there from CQL
module.exports.First = class First extends Expression
  constructor: (json) ->
    super
    @source = build json.source

  exec: (ctx) ->
    src = @source.exec ctx
    if src? and typeIsArray(src) and src.length > 0 then src[0] else null

# TODO: Remove functionref when ELM does First natively
module.exports.FirstFunctionRef = class FirstFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @first = new First {
      "type" : "First",
      "source": json.operand[0]
    }

  exec: (ctx) ->
    @first.exec ctx

# TODO: ELM supports 'orderBy' but there's no way to get there from CQL
module.exports.Last = class Last extends Expression
  constructor: (json) ->
    super
    @source = build json.source

  exec: (ctx) ->
    src = @source.exec ctx
    if src? and typeIsArray(src) and src.length > 0 then src[src.length-1] else null

# TODO: Remove functionref when ELM does Last natively
module.exports.LastFunctionRef = class LastFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @last = new Last {
      "type" : "Last",
      "source": json.operand[0]
    }

  exec: (ctx) ->
    @last.exec ctx
