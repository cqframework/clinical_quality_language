should = require 'should'
{ Patient } = require '../lib/cql-patient'
DT = require '../lib/cql-datatypes'

describe 'Record', ->
  @beforeEach ->
    patient = new Patient {
      "records": [{
        "identifier": { "id": "http://cqframework.org/1/1", "system": "http://cqframework.org" },
        "datatype": "ConditionOccurrence",
        "topic": "Condition",
        "modality": "Observation",
        "code": { "code": "1532007", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Viral pharyngitis (disorder)" },
        "effectiveTime": { "start": "1982-03-12", "end": "1982-03-26" },
        "observedAtTime": { "start": "1982-03-15T15:15" },
        "statementDateTime": "1982-03-15T15:20"
      }]
    }
    @record = patient.findRecords('ConditionOccurrence')[0]

  it 'should get simple record entries', ->
    @record.get('identifier').id.should.equal 'http://cqframework.org/1/1'
    @record.get('identifier').system.should.equal 'http://cqframework.org'
    @record.get('datatype').should.equal 'ConditionOccurrence'
    @record.get('topic').should.equal 'Condition'
    @record.get('modality').should.equal 'Observation'

  it 'should get codes', ->
    @record.getCode('code').should.eql new DT.Code('1532007', '2.16.840.1.113883.6.96', '2013-09')

  it 'should get dates', ->
    @record.getDate('statementDateTime').should.eql new DT.DateTime.parse('1982-03-15T15:20')

  it 'should get intervals', ->
    @record.getInterval('effectiveTime').should.eql new DT.Interval(DT.DateTime.parse('1982-03-12'), DT.DateTime.parse('1982-03-26'))
    @record.getInterval('observedAtTime').should.eql new DT.Interval(DT.DateTime.parse('1982-03-15T15:15'))

  it 'should get date or interval', ->
    @record.getDateOrInterval('statementDateTime').should.eql new DT.DateTime.parse('1982-03-15T15:20')
    @record.getDateOrInterval('effectiveTime').should.eql new DT.Interval(DT.DateTime.parse('1982-03-12'), DT.DateTime.parse('1982-03-26'))

describe 'Patient', ->
  @beforeEach ->
    @patient = new Patient {
      "id": 1,
      "name": "Bob Jones",
      "gender": "M",
      "birthdate" : "1974-07-12T11:15",
      "records": [
        {
          "identifier": { "id": "http://cqframework.org/1/1", "system": "http://cqframework.org" },
          "datatype": "EncounterPerformanceOccurrence",
          "topic": "Encounter",
          "modality": "Performance",
          "class": { "code": "185349003", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Encounter for \"check-up\" (procedure)" },
          "serviceType": { "code": "G0438", "system": "2.16.840.1.113883.6.285", "version": "2014", "display": "Annual wellness visit; includes a personalized prevention plan of service (pps), initial visit" },
          "performanceTime": { "start": "1978-07-15T10:00", "end": "1978-07-15T10:45" }
        }, {
          "identifier": { "id": "http://cqframework.org/1/2", "system": "http://cqframework.org" },
          "datatype": "ConditionOccurrence",
          "topic": "Condition",
          "modality": "Observation",
          "code": { "code": "1532007", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Viral pharyngitis (disorder)" },
          "effectiveTime": { "start": "1982-03-12", "end": "1982-03-26" },
          "observedAtTime": { "start": "1982-03-15T15:15" }
        }
      ]
    }

  it 'should contain patient attributes', ->
    @patient.id.should.equal 1
    @patient.name.should.equal 'Bob Jones'
    @patient.gender.should.equal 'M'
    @patient.birthdate.should.eql DT.DateTime.parse('1974-07-12T11:15')

  it 'should find records by datatype', ->
    encounters = @patient.findRecords('EncounterPerformanceOccurrence')
    encounters.length.should.equal 1
    encounters[0].get('identifier').id.should.equal 'http://cqframework.org/1/1'

    conditions = @patient.findRecords('ConditionOccurrence')
    conditions.length.should.equal 1
    conditions[0].get('identifier').id.should.equal 'http://cqframework.org/1/2'

  it 'should return empty array for unfound records', ->
    @patient.findRecords('foo').should.be.empty
