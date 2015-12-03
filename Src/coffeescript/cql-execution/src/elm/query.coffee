{ Expression, UnimplementedExpression } = require './expression'
{ Context } = require '../runtime/context'
{ build } = require './builder'
{ typeIsArray , allTrue} = require '../util/util'
{ equals } = require '../util/comparison'

module.exports.AliasedQuerySource = class AliasedQuerySource
  constructor: (json) ->
    @alias = json.alias
    @expression = build json.expression

module.exports.DefineClause = class DefineClause
  constructor: (json) ->
    @identifier = json.identifier
    @expression = build json.expression

module.exports.With = class With extends Expression
  constructor: (json) ->
    super
    @alias = json.alias
    @expression = build json.expression
    @suchThat = build json.suchThat
  exec: (ctx) ->
    records = @expression.exec(ctx)
    returns = for rec in records
      childCtx = ctx.childContext()
      childCtx.set @alias, rec
      @suchThat.exec(childCtx)
    returns.some (x) -> x

module.exports.Without = class Without extends With
  constructor: (json) ->
    super
  exec: (ctx) ->
    !super(ctx)

# ELM-only, not a product of CQL
module.exports.Sort = class Sort extends UnimplementedExpression

module.exports.ByDirection = class ByDirection extends Expression
  constructor: (json) ->
    super
    @direction = json.direction
    @low_order = if @direction == "asc" then -1 else 1
    @high_order = @low_order * -1

  exec: (a,b) ->
    if a == b
      0
    else if a < b
      @low_order
    else
      @high_order

# ELM-only, not a product of CQL
module.exports.ByColumn = class ByColumn extends UnimplementedExpression

module.exports.ByExpression = class ByExpression extends Expression
  constructor: (json) ->
    super
    @expression = build json.expression
    @direction = json.direction
    @low_order = if @direction == "asc" then -1 else 1
    @high_order = @low_order * -1

  exec: (a,b) ->
    ctx = new Context()
    ctx.context_values = a
    a_val = @expression.exec(ctx)
    ctx.context_values = b
    b_val = @expression.exec(ctx)

    if a_val == b_val
      0
    else if a_val < b_val
      @low_order
    else
      @high_order

module.exports.ReturnClause = ReturnClause = class ReturnClause
  constructor:(json) ->
    @expression = build json.expression
    @distinct = json.distinct ? true

module.exports.SortClause = SortClause = class SortClause
  constructor:(json) ->
    @by = build json?.by

  sort: (values) ->
    if @by
      values.sort (a,b) =>
        order = 0
        for item in @by
          order = item.exec(a,b)
          if order != 0 then break
        order

toDistinctList = (xList) ->
  yList = []
  for x in xList
    inYList = false
    inYList = true for y in yList when equals(x, y)
    unless inYList then yList.push x
  yList

module.exports.Query = class Query extends Expression
  constructor: (json) ->
    super
    @sources = new MultiSource((new AliasedQuerySource(s) for s in json.source))
    @defineClauses = (new DefineClause(d) for d in (json.define ? []))
    @relationship = if json.relationship? then build json.relationship else []
    @where = build json.where
    @returnClause = if json.return? then new ReturnClause(json.return) else null
    @aliases = @sources.aliases()
    @sortClause = if json.sort? then new SortClause(json.sort) else null

  exec: (ctx) ->
    returnedValues = []
    @sources.forEach(ctx, (rctx) =>
      for def in @defineClauses
        rctx.set def.identifier, def.expression.exec(rctx)

      relations = for rel in @relationship
        child_ctx = rctx.childContext()
        rel.exec(child_ctx)
      passed = allTrue(relations)
      passed = passed && if @where then @where.exec(rctx) else passed
      if passed
        if @returnClause?
          val = @returnClause.expression.exec(rctx)
          returnedValues.push val
        else
          if @aliases.length == 1
            returnedValues.push rctx.get(@aliases[0])
          else
            returnedValues.push rctx.context_values
    )

    distinct = if @returnClause? then @returnClause.distinct else true
    if distinct then returnedValues = toDistinctList(returnedValues)

    @sortClause?.sort(returnedValues)
    returnedValues

module.exports.AliasRef = class AliasRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    ctx?.get(@name)

module.exports.QueryDefineRef = class QueryDefineRef extends AliasRef
  constructor: (json) ->
    super

# The following is not defined by ELM but is helpful for execution

class MultiSource
  constructor: (@sources) ->
    @alias = @sources[0].alias
    @expression = @sources[0].expression

    if @sources.length > 1
      @rest = new MultiSource(@sources.slice(1))

  aliases: ->
    a = [@alias]
    if @rest
      a = a.concat @rest.aliases()
    a

  forEach: (ctx, func) ->
    @records?= @expression.exec(ctx) || []
    for rec in @records
      rctx = new Context(ctx)
      rctx.set(@alias,rec)
      if @rest
        @rest.forEach(rctx,func)
      else
        func(rctx)
