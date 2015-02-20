{ Expression } = require './expression'
{ build } = require './builder'
{ equals } = require '../util/comparison'

# TODO: Spec lists "Conditional", but it's "If" in the XSD
module.exports.If = class If extends Expression
  constructor: (json) ->
    super
    @condition = build json.condition
    @th = build json.then
    @els = build json.else

  exec: (ctx) ->
    if @condition.exec(ctx) then @th.exec(ctx) else @els.exec(ctx)

module.exports.CaseItem = CaseItem = class CaseItem
  constructor:(json) ->
    @when = build json.when
    @then = build json.then

module.exports.Case = class Case extends Expression

  constructor: (json) ->
    super
    @comparand = build json.comparand
    @caseItems = for ci in json.caseItem
                   new CaseItem(ci)
    @els = build json.else

  exec: (ctx) ->
    if @comparand then @exec_selected(ctx) else @exec_standard(ctx)

  exec_selected: (ctx) ->
    val = @comparand.exec(ctx)
    for ci in @caseItems
      if equals ci.when.exec(ctx), val
       return ci.then.exec(ctx)
    @els.exec(ctx)

  exec_standard: (ctx) ->
    for ci in @caseItems
      if ci.when.exec(ctx)
       return ci.then.exec(ctx)
    @els.exec(ctx)
