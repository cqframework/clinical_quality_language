{ Expression, UnimplementedExpression } = require './expression'
{ FunctionRef } = require './reusable'
{ ValueSet } = require '../datatypes/datatypes'
{ build } = require './builder'
{ equals, typeIsArray } = require '../util/util'

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

# Delegated to by overloaded#Union
module.exports.doUnion = (a, b) ->
  a.concat b

# Delegated to by overloaded#Except
module.exports.doExcept = (a, b) ->
  (itm for itm in a when not doIn(itm, b))

# Delegated to by overloaded#Intersect
module.exports.doIntersect = (a, b) ->
  (itm for itm in a when doIn(itm, b))

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

module.exports.IndexOf = class IndexOf extends Expression
  constructor: (json) ->
    super
    @source = build json.source
    @element = build json.element

  exec: (ctx) ->
    src = @source.exec ctx
    el = @element.exec ctx
    if not src? or not el? then return null
    (index = i; break) for itm, i in src when equals itm, el
    if index? then return index + 1 else return 0

# TODO: Remove functionref when ELM does IndexOf natively
module.exports.IndexOfFunctionRef = class IndexOfFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @indexOf = new IndexOf {
      "type" : "IndexOf",
      "source": json.operand[0]
      "element": json.operand[1]
    }

  exec: (ctx) ->
    @indexOf.exec ctx

# Indexer is completely handled by overloaded#Indexer

# Delegated to by overloaded#In and overloaded#Contains
module.exports.doIn = doIn = (item, container) ->
  return true for element in container when equals element, item
  return false

# Delegated to by overloaded#Includes and overloaded@IncludedIn
module.exports.doIncludes = doIncludes = (list, sublist) ->
  sublist.every (x) -> doIn(x, list)

# Delegated to by overloaded#ProperIncludes and overloaded@ProperIncludedIn
module.exports.doProperIncludes = (list, sublist) ->
  list.length > sublist.length and doIncludes(list, sublist)

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

# Length is completely handled by overloaded#Length
