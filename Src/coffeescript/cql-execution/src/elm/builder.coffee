E = require './expressions'
{ typeIsArray } = require '../util/util'

module.exports.build = build = (json) ->
  if not json? then return json

  if (typeIsArray json)
    return (build child for child in json)

  if json.type is "FunctionRef" then new E.FunctionRef(json)
  else if json.type is "Literal" then E.Literal.from(json)
  else if functionExists(json.type) then constructByName(json.type, json)
  else null

functionExists = (name) -> typeof E[name] is "function"

constructByName = (name, json) -> new E[name](json)
