toDate = (str) ->
  if typeof str is 'string' then new Date(str)
  else null

class Patient
  constructor: (json) ->
    @id = json.id
    @name = json.name
    @gender = json.gender
    @birthdate = toDate(json.birthdate)
    @records = {}
    for r in json.records ? []
      @records[r.datatype] ?= []
      @records[r.datatype].push r

module.exports.Patient = Patient