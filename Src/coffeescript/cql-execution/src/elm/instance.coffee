{ Expression } = require './expression'
{ build } = require './builder'
{ Quantity } = require('./quantity')
{ Code, Concept } = require('../datatypes/datatypes')
class Element
  constructor: (json) ->
    @name = json.name
    @value = build json.value
  exec: (ctx) ->
    @value?.execute(ctx)


module.exports.Instance = class Instance extends Expression
  constructor: (json) ->
    super
    @classType = json.classType
    @element = ( new Element(child) for child in json.element)

  exec: (ctx) ->
    obj = {}
    for el in @element
      obj[el.name] = el.exec(ctx)
    # TODO: Support for other classes like Concept
    switch @classType
      when "{urn:hl7-org:elm-types:r1}Quantity" then new Quantity(obj)
      when "{urn:hl7-org:elm-types:r1}Code" then new Code(obj.code, obj.system, obj.version, obj.display)
      when "{urn:hl7-org:elm-types:r1}Concept" then new Concept(obj.codes, obj.display)
      else obj
