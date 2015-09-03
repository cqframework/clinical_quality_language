{ Expression } = require './expression'
{ build } = require './builder'

class Element 
  constructor: (json) ->
    @name = json.name
    @value = build json.value
  exec: (ctx) ->
    @value?.exec(ctx)


module.exports.Instance = class Instance extends Expression
  constructor: (json) ->
    super
    @classType = json.classType
    @element = ( new Element(child) for child in json.element)

  exec: (ctx) ->
    obj = {}
    for el in @element
      obj[el.name] = el.exec(ctx)
    obj
