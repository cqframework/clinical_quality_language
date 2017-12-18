{ typeIsArray } = require '../util/util'

module.exports.Code = class Code
  constructor: (@code, @system, @version, @display) ->

  hasMatch: (code) ->
    codesInList(toCodeList(code), [@])

module.exports.Concept = class Concept
  constructor: (@codes = [], @display) ->

  hasMatch: (code) ->
    codesInList(toCodeList(code), @codes)

module.exports.ValueSet = class ValueSet
  constructor: (@oid, @version, @codes = []) ->

  hasMatch: (code) ->
    codesInList(toCodeList(code), @codes)

toCodeList = (c) ->
  if not c?
    []
  else if typeIsArray c
    list = []
    for c2 in c
      list = list.concat(toCodeList(c2))
    list
  else if typeIsArray c.codes
    c.codes
  else if typeof c is 'string'
    [new Code(c)]
  else
    [c]


codesInList = (cl1, cl2) ->
  cl1.some (c1) -> (cl2.some (c2) -> codesMatch(c1, c2))

codesMatch = (code1, code2) ->
  return false if code1.code != code2.code
  return false if code1.system? and code2.system? and code1.system != code2.system
  return true

module.exports.CodeSystem = class CodeSystem
  constructor: (@id, @version) ->
