E = require './expressions'
{ typeIsArray } = require '../util/util'

module.exports.build = build = (json) ->
  if not json? then return json

  if (typeIsArray json)
    return (build child for child in json)

  if json.type is "FunctionRef" then new buildFunctionRef(json)
  else if json.type is "Literal" then E.Literal.from(json)
  else if functionExists("E.#{json.type}") then constructByName("E.#{json.type}", json)
  else null

buildFunctionRef = (json) ->
  if functionExists("E.#{json.name}FunctionRef") then constructByName("E.#{json.name}FunctionRef", json)
  else new E.FunctionRef(json)

functionExists = (name) -> eval("typeof #{name}") is "function"

constructByName = (name, json) -> eval("new #{name}(json)")
