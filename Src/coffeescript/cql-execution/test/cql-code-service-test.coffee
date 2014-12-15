should = require 'should'
{ CodeService } = require '../lib/cql-code-service'
{ Code, ValueSet } = require '../lib/datatypes/datatypes'


describe 'CodeService', ->
  @beforeEach ->
    @svc = new CodeService({
      "1.2.3.4.5" : {
        "1" : [
          { "code": "ABC", "system": "5.4.3.2.1", "version": "1" },
          { "code": "DEF", "system": "5.4.3.2.1", "version": "2" },
          { "code": "GHI", "system": "5.4.3.4.5", "version": "3" }
        ],
        "2" : [
          { "code": "ABC", "system": "5.4.3.2.1", "version": "1" },
          { "code": "DEF", "system": "5.4.3.2.1", "version": "2" },
          { "code": "JKL", "system": "5.4.3.2.1", "version": "3" }
        ]
      },
      "6.7.8.9.0" : {
        "A" : [
          { "code": "MNO", "system": "2.4.6.8.0", "version": "3" },
          { "code": "PQR", "system": "2.4.6.8.0", "version": "2" },
          { "code": "STU", "system": "2.4.6.8.0", "version": "1" }
        ]
      }
    })
    @vsOne = new ValueSet('1.2.3.4.5', '1', [
        new Code('ABC', '5.4.3.2.1', '1'),
        new Code('DEF', '5.4.3.2.1', '2'),
        new Code('GHI', '5.4.3.4.5', '3'),
      ])
    @vsTwo = new ValueSet('1.2.3.4.5', '2', [
        new Code('ABC', '5.4.3.2.1', '1'),
        new Code('DEF', '5.4.3.2.1', '2'),
        new Code('JKL', '5.4.3.2.1', '3'),
      ])
    @vsThree = new ValueSet('6.7.8.9.0', 'A', [
        new Code('MNO', '2.4.6.8.0', '3'),
        new Code('PQR', '2.4.6.8.0', '2'),
        new Code('STU', '2.4.6.8.0', '1'),
      ])

  it 'should find value sets by OID', ->
    valueSets = @svc.findValueSetsByOid('1.2.3.4.5')
    valueSets.length.should.equal 2
    valueSets.should.containEql @vsOne
    valueSets.should.containEql @vsTwo

    valueSets = @svc.findValueSetsByOid('6.7.8.9.0')
    valueSets.length.should.equal 1
    valueSets.should.containEql @vsThree

  it 'should find a single value set by OID and version', ->
    @svc.findValueSet('1.2.3.4.5', '1').should.eql @vsOne

  it 'should find a single value set by OID (using latest version)', ->
    @svc.findValueSet('1.2.3.4.5').should.eql @vsTwo

  it 'should return empty array when searching for value sets by wrong OID', ->
    @svc.findValueSetsByOid('0.0.0.0.0').should.be.empty

  it 'should return null when looking for a single value set by wrong OID', ->
    should.not.exist @svc.findValueSet('0.0.0.0.0')

  it 'should return null when looking for a single value set by wrong version', ->
    should.not.exist @svc.findValueSet('1.2.3.4.5', '3')
