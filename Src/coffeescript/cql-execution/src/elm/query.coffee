{ Expression, UnimplementedExpression } = require './expression'
{ Context } = require '../runtime/context'
{ build } = require './builder'
{ typeIsArray , allTrue} = require '../util/util'

module.exports.AliasedQuerySource = class AliasedQuerySource extends UnimplementedExpression

module.exports.DefineClause = class DefineClause extends UnimplementedExpression

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

module.exports.Sort = class Sort extends UnimplementedExpression

module.exports.SortClause = class SortClause extends UnimplementedExpression

module.exports.ByDirection = class ByDirection extends UnimplementedExpression

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

module.exports.ReturnClause = class ReturnClause extends UnimplementedExpression

class Sort
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

module.exports.Query = class Query extends Expression
  constructor: (json) ->
    super
    @sources = new MultiSource(json.source)
    @definitions = for d in json.define ? []
      identifier: d.identifier
      expression: build d.expression

    @relationship = build json.relationship
    @where = build json.where
    @return = build json.return?.expression
    @aliases = @sources.aliases()
    @sort = new Sort(json.sort)

  exec: (ctx) ->
    self = @
    returnedValues = []
    @sources.forEach(ctx, (rctx) ->
      for def in self.definitions
        rctx.set def.identifier, def.expression.exec(rctx)

      relations = for rel in self.relationship
        child_ctx = rctx.childContext()
        rel.exec(child_ctx)
      passed = allTrue(relations)
      passed = passed && if self.where then self.where.exec(rctx) else passed
      if passed
        if self.return
          val = self.return.exec(rctx)
          if returnedValues.indexOf(val) == -1
            returnedValues.push val
        else
          if self.aliases.length == 1
            returnedValues.push rctx.get(self.aliases[0])
          else
            returnedValues.push rctx.context_values
    )

    @sort?.sort(returnedValues)
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
    @sources = if typeIsArray(@sources) then @sources else [@sources]
    @alias = @sources[0].alias
    @expression = build @sources[0].expression

    if @sources.length > 1
      @rest = new MultiSource(@sources.slice(1))

  aliases: ->
    a = [@alias]
    if @rest
      a = a.concat @rest.aliases()
    a

  forEach: (ctx, func) ->
    @records?= @expression.exec(ctx)
    for rec in @records
      rctx = new Context(ctx)
      rctx.set(@alias,rec)
      if @rest
        @rest.forEach(rctx,func)
      else
        func(rctx)
