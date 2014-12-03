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
    @identifier = json.identifier
    @name = json.name
    @gender = json.gender
    @birthDate = if json.birthDate? then DT.DateTime.parse json.birthDate
    @records = {}
    for r in json.records ? []
      @records[r.profile] ?= []
      @records[r.profile].push new Record(r)

  findRecords: (profile) ->
    if profile is 'cqf-patient' then [@] else @records[profile] ? []

class PatientSource
    constructor: (@patients) ->
      @current = @patients.shift();

    currentPatient: ->
      if @current?
        new Patient(@current)
      else null

    nextPatient: ->
      @current = @patients.shift();
      @currentPatient()


module.exports.Patient = Patient
module.exports.PatientSource = PatientSource
