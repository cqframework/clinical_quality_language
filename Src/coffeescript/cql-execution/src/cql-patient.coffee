DT = require './cql-datatypes'

toDate = (str) ->
  if typeof str is 'string' then new Date(str)
  else null

class Record
  constructor: (@json) ->

  get: (field) ->
    @json[field]

  getDate: (field) ->
    val = @get field
    if val? then DT.DateTime.parse(val) else null

  getInterval: (field) ->
    val = @get field
    if val? and typeof val is 'object'
      start = if val.start? then DT.DateTime.parse val.start else null
      end = if val.end? then DT.DateTime.parse val.end else null
      new DT.Interval(start, end)

  getDateOrInterval: (field) ->
    val = @get field
    if val? and typeof val is 'object' then @getInterval field else @getDate field

  getCode: (field) ->
    val = @get field
    if val? and typeof val is 'object' then new DT.Code(val.code, val.system, val.version)

class Patient
  constructor: (json) ->
    @id = json.id
    @name = json.name
    @gender = json.gender
    @birthdate = if json.birthdate? then DT.DateTime.parse json.birthdate
    @records = {}
    for r in json.records ? []
      @records[r.datatype] ?= []
      @records[r.datatype].push new Record(r)

  findRecords: (datatype) ->
    @records[datatype] ? []


class PatientSource 
    constructor: (@patients) ->
      @index = 0
    currentPatient: ->
      if @patients[@index]
        new Patient(@patients[@index])
    
    nextPatient: ->
      @index++;
      @currentPatient()

module.exports.Patient = Patient
module.exports.PatientSource = PatientSource
