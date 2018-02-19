{ Expression, UnimplementedExpression } = require './expression'
{ ValueSet } = require '../datatypes/datatypes'
{ build } = require './builder'
{ typeIsArray } = require '../util/util'
{ equals, equivalent } = require '../util/comparison'

module.exports.List = class List extends Expression
  constructor: (json) ->
    super
    @elements = (build json.element) ? []

  exec: (ctx) ->
    (item.execute(ctx) for item in @elements)

module.exports.Exists = class Exists extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx)?.length > 0

# Equal is completely handled by overloaded#Equal

# NotEqual is completely handled by overloaded#Equal

# Delegated to by overloaded#Union
module.exports.doUnion = (a, b) ->
  doDistinct(a.concat b)

# Delegated to by overloaded#Except
module.exports.doExcept = (a, b) ->
  (itm for itm in a when not doContains(b, itm))

# Delegated to by overloaded#Intersect
module.exports.doIntersect = (a, b) ->
  (itm for itm in a when doContains(b, itm))

# ELM-only, not a product of CQL
module.exports.Times = class Times extends UnimplementedExpression

# ELM-only, not a product of CQL
module.exports.Filter = class Filter extends UnimplementedExpression

module.exports.SingletonFrom = class SingletonFrom extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs ctx
    if arg? and arg.length > 1 then throw new Error 'IllegalArgument: \'SingletonFrom\' requires a 0 or 1 arg array'
    else if arg? and arg.length is 1 then return arg[0]
    else return null

module.exports.ToList = class ToList extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs ctx
    if arg?
      [arg]
    else
      []

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
    if index? then return index else return -1

# Indexer is completely handled by overloaded#Indexer

# Delegated to by overloaded#Contains and overloaded#In
module.exports.doContains = doContains = (container, item) ->
  return true for element in container when equivalent element, item
  return false

# Delegated to by overloaded#Includes and overloaded@IncludedIn
module.exports.doIncludes = doIncludes = (list, sublist) ->
  sublist.every (x) -> doContains(list, x)

# Delegated to by overloaded#ProperIncludes and overloaded@ProperIncludedIn
module.exports.doProperIncludes = (list, sublist) ->
  list.length > sublist.length and doIncludes(list, sublist)

# ELM-only, not a product of CQL
module.exports.ForEach = class ForEach extends UnimplementedExpression

module.exports.Flatten = class Flatten extends Expression
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

  exec: (ctx) ->
    doDistinct(@execArgs ctx)

doDistinct = (list) ->
  seen = []
  list.filter (item) ->
    isNew = seen.every (seenItem) -> !equals(item, seenItem)
    seen.push item if isNew
    isNew

# ELM-only, not a product of CQL
module.exports.Current = class Current extends UnimplementedExpression

module.exports.First = class First extends Expression
  constructor: (json) ->
    super
    @source = build json.source

  exec: (ctx) ->
    src = @source.exec ctx
    if src? and typeIsArray(src) and src.length > 0 then src[0] else null

module.exports.Last = class Last extends Expression
  constructor: (json) ->
    super
    @source = build json.source

  exec: (ctx) ->
    src = @source.exec ctx
    if src? and typeIsArray(src) and src.length > 0 then src[src.length-1] else null

# Length is completely handled by overloaded#Length
