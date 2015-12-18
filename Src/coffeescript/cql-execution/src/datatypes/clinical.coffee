{ typeIsArray } = require '../util/util'

module.exports.Code = class Code
  constructor: (@code, @system, @version, @display) ->

module.exports.ValueSet = class ValueSet
  constructor: (@oid, @version, @codes = []) ->

  hasCode: (code, system, version) ->
    if typeIsArray code
      matches = for c in code
        @hasCode c
      return true in matches
    if code instanceof Object then [ code, system, version ] = [ code.code, code.system, code.version ]
    matches = (c for c in @codes when c.code is code)
    if system? then matches = (c for c in matches when c.system is system)
    if version? then matches = (c for c in matches when c.version is version)
    return matches.length > 0

  matchCode: (code,system,version) ->
    matches = (c for c in @codes when c.code is code)
    if system? then matches = (c for c in matches when c.system is system)
    if version? then matches = (c for c in matches when c.version is version)
    return matches.length > 0

# TODO: Concept (and support for constructing by literal or instance)
