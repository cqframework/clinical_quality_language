require './core'

class Element

  constructor: (@json) ->

  xmlId: -> @json["xmlId"]

  meta: -> @json["meta"]

  extensions: -> 
    for e in @json["extensions"]
      new Extension(e)

  modifierExtensions: ->
    for e in @json["modifierExtensions"]
      new Extension(e)
  
  getExtension: (name) ->
    extensions.filter (x)-> x.url == name 
  
  getModifierExtension: (name) ->  
    modifierExtensions.filter (x)-> x.url == name 
