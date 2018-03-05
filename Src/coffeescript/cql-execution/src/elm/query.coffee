{ Expression, UnimplementedExpression } = require './expression'
{ Context } = require '../runtime/context'
{ build } = require './builder'
{ typeIsArray , allTrue} = require '../util/util'
{ equals } = require '../util/comparison'

module.exports.AliasedQuerySource = class AliasedQuerySource
  constructor: (json) ->
    @alias = json.alias
    @expression = build json.expression

module.exports.LetClause = class LetClause
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
    records = @expression.execute(ctx)
    @isList = typeIsArray(records)
    records = if @isList then records else [records]
    returns = for rec in records
      childCtx = ctx.childContext()
      childCtx.set @alias, rec
      @suchThat.execute(childCtx)
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

  exec: (ctx,a,b) ->
    if a == b
      0
    else if a < b
      @low_order
    else
      @high_order

module.exports.ByExpression = class ByExpression extends Expression
  constructor: (json) ->
    super
    @expression = build json.expression
    @direction = json.direction
    @low_order = if @direction == "asc" then -1 else 1
    @high_order = @low_order * -1

  exec: (ctx,a,b) ->
    sctx = ctx.childContext(a)
    a_val = @expression.execute(sctx)
    sctx = ctx.childContext(b)
    b_val = @expression.execute(sctx)

    if a_val == b_val
      0
    else if a_val < b_val
      @low_order
    else
      @high_order

module.exports.ByColumn = class ByColumn extends ByExpression
  constructor: (json) ->
    super
    @expression = build {
      "name" : json.path,
      "type" : "IdentifierRef"
    }

module.exports.ReturnClause = ReturnClause = class ReturnClause
  constructor:(json) ->
    @expression = build json.expression
    @distinct = json.distinct ? true

module.exports.SortClause = SortClause = class SortClause
  constructor:(json) ->
    @by = build json?.by

  sort: (ctx, values) ->
    if @by
      values.sort (a,b) =>
        order = 0
        for item in @by
          # Do not use execute here because the value of the sort order is not important.
          order = item.exec(ctx,a,b)
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
    @letClauses = (new LetClause(d) for d in (json.let ? []))
    @relationship = if json.relationship? then build json.relationship else []
    @where = build json.where
    @returnClause = if json.return? then new ReturnClause(json.return) else null
    @aliases = @sources.aliases()
    @sortClause = if json.sort? then new SortClause(json.sort) else null

  exec: (ctx) ->
    returnedValues = []
    @sources.forEach(ctx, (rctx) =>
      for def in @letClauses
        rctx.set def.identifier, def.expression.execute(rctx)

      relations = for rel in @relationship
        child_ctx = rctx.childContext()
        rel.execute(child_ctx)
      passed = allTrue(relations)
      passed = passed && if @where then @where.execute(rctx) else passed
      if passed
        if @returnClause?
          val = @returnClause.expression.execute(rctx)
          returnedValues.push val
        else
          if @aliases.length == 1
            returnedValues.push rctx.get(@aliases[0])
          else
            returnedValues.push rctx.context_values
    )

    distinct = if @returnClause? then @returnClause.distinct else true
    if distinct then returnedValues = toDistinctList(returnedValues)

    @sortClause?.sort(ctx, returnedValues)
    return if @sources.returnsList() then returnedValues else returnedValues[0]

module.exports.AliasRef = class AliasRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    ctx?.get(@name)

module.exports.QueryLetRef = class QueryLetRef extends AliasRef
  constructor: (json) ->
    super

# The following is not defined by ELM but is helpful for execution

class MultiSource
  constructor: (@sources) ->
    @alias = @sources[0].alias
    @expression = @sources[0].expression
    @isList = true
    if @sources.length > 1
      @rest = new MultiSource(@sources.slice(1))

  aliases: ->
    a = [@alias]
    if @rest
      a = a.concat @rest.aliases()
    a

  returnsList: ->
    @isList || (@rest && @rest.returnsList())

  forEach: (ctx, func) ->
    records = @expression.execute(ctx)
    @isList = typeIsArray(records)
    records = if @isList then records else [records]
    for rec in records
      rctx = new Context(ctx)
      rctx.set(@alias,rec)
      if @rest
        @rest.forEach(rctx,func)
      else
        func(rctx)
