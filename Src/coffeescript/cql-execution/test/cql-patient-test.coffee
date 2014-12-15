should = require 'should'
{ Patient } = require '../lib/cql-patient'
DT = require '../lib/datatypes/datatypes'

describe 'Record', ->
  @beforeEach ->
    patient = new Patient {
      "records": [{
        "identifier": { "value": "http://cqframework.org/1/1", "system": "http://cqframework.org" },
        "profile": "cqf-encounter",
        "topic": "Encounter",
        "class": { "code": "185349003", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Encounter for \"check-up\" (procedure)" },
        "type": { "code": "G0438", "system": "2.16.840.1.113883.6.285", "version": "2014", "display": "Annual wellness visit; includes a personalized prevention plan of service (pps), initial visit" },
        "period": { "start": "1978-07-15T10:00", "end": "1978-07-15T10:45" }
      }, {
        "identifier": { "value": "http://cqframework.org/1/2", "system": "http://cqframework.org" },
        "profile": "cqf-condition",
        "topic": "Condition",
        "code": { "code": "1532007", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Viral pharyngitis (disorder)" },
        "onsetDateTime": "1982-03-12",
        "abatementDateTime": "1982-03-26",
        "issued": "1982-03-15T15:15:00"
      }]
    }
    [@encRecord, @cndRecord] = (v[0] for k,v of patient.records)

  it 'should get simple record entries', ->
    @encRecord.get('identifier').value.should.equal 'http://cqframework.org/1/1'
    @encRecord.get('identifier').system.should.equal 'http://cqframework.org'
    @encRecord.get('profile').should.equal 'cqf-encounter'
    @encRecord.get('topic').should.equal 'Encounter'
    @cndRecord.get('identifier').value.should.equal 'http://cqframework.org/1/2'
    @cndRecord.get('identifier').system.should.equal 'http://cqframework.org'
    @cndRecord.get('profile').should.equal 'cqf-condition'
    @cndRecord.get('topic').should.equal 'Condition'

  it 'should get codes', ->
    @encRecord.getCode('class').should.eql new DT.Code('185349003', '2.16.840.1.113883.6.96', '2013-09')
    @encRecord.getCode('type').should.eql new DT.Code('G0438', '2.16.840.1.113883.6.285', '2014')
    @cndRecord.getCode('code').should.eql new DT.Code('1532007', '2.16.840.1.113883.6.96', '2013-09')

  it 'should get dates', ->
    @cndRecord.getDate('onsetDateTime').should.eql new DT.DateTime.parse('1982-03-12')
    @cndRecord.getDate('abatementDateTime').should.eql new DT.DateTime.parse('1982-03-26')
    @cndRecord.getDate('issued').should.eql new DT.DateTime.parse('1982-03-15T15:15:00')

  it 'should get intervals', ->
    @encRecord.getInterval('period').should.eql new DT.Interval(DT.DateTime.parse('1978-07-15T10:00'), DT.DateTime.parse('1978-07-15T10:45'))

  it 'should get date or interval', ->
    @cndRecord.getDateOrInterval('issued').should.eql new DT.DateTime.parse('1982-03-15T15:15:00')
    @encRecord.getDateOrInterval('period').should.eql new DT.Interval(DT.DateTime.parse('1978-07-15T10:00'), DT.DateTime.parse('1978-07-15T10:45'))

describe 'Patient', ->
  @beforeEach ->
    @patient = new Patient {
      "identifier": { "value": "1" },
      "name": "Bob Jones",
      "gender": "M",
      "birthDate" : "1974-07-12T11:15",
      "records": [{
          "identifier": { "value": "http://cqframework.org/1/1", "system": "http://cqframework.org" },
          "profile": "cqf-encounter",
          "topic": "Encounter",
          "class": { "code": "185349003", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Encounter for \"check-up\" (procedure)" },
          "type": { "code": "G0438", "system": "2.16.840.1.113883.6.285", "version": "2014", "display": "Annual wellness visit; includes a personalized prevention plan of service (pps), initial visit" },
          "period": { "start": "1978-07-15T10:00", "end": "1978-07-15T10:45" }
        }, {
          "identifier": { "value": "http://cqframework.org/1/2", "system": "http://cqframework.org" },
          "profile": "cqf-condition",
          "topic": "Condition",
          "code": { "code": "1532007", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Viral pharyngitis (disorder)" },
          "onsetDateTime": "1982-03-12",
          "abatementDateTime": "1982-03-26",
          "issued": "1982-03-15T15:15:00"
        }
      ]
    }

  it 'should contain patient attributes', ->
    @patient.identifier.value.should.equal '1'
    @patient.name.should.equal 'Bob Jones'
    @patient.gender.should.equal 'M'
    @patient.birthDate.should.eql DT.DateTime.parse('1974-07-12T11:15')

  it 'should find records by profile', ->
    encounters = @patient.findRecords('cqf-encounter')
    encounters.length.should.equal 1
    encounters[0].get('identifier').value.should.equal 'http://cqframework.org/1/1'

    conditions = @patient.findRecords('cqf-condition')
    conditions.length.should.equal 1
    conditions[0].get('identifier').value.should.equal 'http://cqframework.org/1/2'

  it 'should return empty array for unfound records', ->
    @patient.findRecords('foo').should.be.empty
