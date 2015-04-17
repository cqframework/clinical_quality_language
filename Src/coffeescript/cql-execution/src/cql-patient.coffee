DT = require './datatypes/datatypes'
FHIR = require './fhir/models'
{ typeIsArray } = require './util/util'

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
    if profile is 'patient-qicore-qicore-patient' then [@] else @records[profile] ? []



FHIR.Patient::records = ->
  @_records = {}
  for r in @json.records ? []
    @_records[r.profile] ?= []
    @_records[r.profile].push new Record(r)
  @_records

FHIR.Patient::findRecords = (profile) ->
  if profile is 'patient-qicore-qicore-patient' then [@] else @_bundle?.findRecords(profile) ? []


FHIR.Bundle::findRecords = (profile) ->
  filtered = @entry().filter (e)->
    e.resource()?.meta()?.profile()?.indexOf(profile) > -1
  for e in filtered
    r = e.resource()
    r._bundle = this
    r

FHIR.Bundle::findRecord = (profile) ->
  @findRecords(profile)[0]

FHIR.Base::get = (field) ->
  @[field]?.call(@)

FHIR.Base::getDate = (field) ->
  val = @get field
  if val instanceof DT.DateTime
    val
  else if typeof val is "string"
    DT.DateTime.parse(val)

FHIR.Base::getInterval= (field) ->
  val = @get field
  if val instannceOf FHIR.Period
    @periodToInterval val

FHIR.Base::getDateOrInterval = (field) ->
  val = @get field
  if val instanceof FHIR.Period
    @periodToInterval(val)
  else if typeof val is "string"
    DT.DateTime.parse(val)
  else if val instanceof  DT.DateTime
    val

FHIR.Base::getCode = (field) ->
  val = @get field
  @toCode(val)

FHIR.Base::toCode = (val) ->
  if typeIsArray(val)
    for c in val
      @toCode(c)
  else if val instanceof FHIR.CodeableConcept
    @codableConceptToCodes  val
  else if val instanceof FHIR.Coding
    @codingToCode val


FHIR.Base::codableConceptToCodes =(cc) ->
  for c in cc.coding()
    @codingToCode c

FHIR.Base::codingToCode = (coding) ->
  new DT.Code(coding.code(), coding.system(), coding.version())

FHIR.Base::periodToInterval =(val) ->
  if val instanceof FHIR.Period
    start =  val.getDate("start")
    end =  val.getDate("end")
    new DT.Interval(start, end)


class PatientSource
  constructor: (@patients) ->
    @nextPatient()

  currentPatient: ->
    @current_patient

  nextPatient: ->
    @current = @patients.shift()
    @current_bundle = if @current then new FHIR.Bundle(@current)
    @current_patient = @current_bundle?.findRecord("patient-qicore-qicore-patient")


module.exports.Patient = Patient
module.exports.PatientSource = PatientSource
