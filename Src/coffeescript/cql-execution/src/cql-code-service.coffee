{ Code, ValueSet } = require './datatypes/datatypes'

class CodeService
  constructor: (valueSetsJson = {}) ->
    @valueSets = {}
    for oid of valueSetsJson
      @valueSets[oid] = {}
      for version of valueSetsJson[oid]
        codes = (new Code(code.code, code.system, code.version) for code in valueSetsJson[oid][version])
        @valueSets[oid][version] = new ValueSet(oid, version, codes)

  findValueSetsByOid: (oid) ->
    (valueSet for version, valueSet of @valueSets[oid])

  findValueSet: (oid, version) ->
    if version?
      @valueSets[oid]?[version]
    else
      results = @findValueSetsByOid(oid)
      if results.length is 0 then null else results.reduce (a, b) -> if a.version > b.version then a else b

module.exports.CodeService = CodeService
