should = require 'should'
{ Code, ValueSet } = require '../../lib/datatypes/clinical'

describe 'Code', ->
  @beforeEach ->
    @code = new Code('ABC', '5.4.3.2.1', '1')

  it 'should properly represent the code, system, and version', ->
    @code.code.should.equal 'ABC'
    @code.system.should.equal '5.4.3.2.1'
    @code.version.should.equal '1'

describe 'ValueSet', ->
  @beforeEach ->
    @valueSet = new ValueSet('1.2.3.4.5', '1', [
        new Code('ABC', '5.4.3.2.1', '1'),
        new Code('DEF', '5.4.3.2.1', '2'),
        new Code('GHI', '5.4.3.4.5', '3'),
      ])

  it 'should properly represent the OID, version and codes', ->
    @valueSet.oid.should.equal '1.2.3.4.5'
    @valueSet.version.should.equal '1'
    @valueSet.codes.length.should.equal 3
    @valueSet.codes[0].should.eql new Code('ABC', '5.4.3.2.1', '1')
    @valueSet.codes[1].should.eql new Code('DEF', '5.4.3.2.1', '2')
    @valueSet.codes[2].should.eql new Code('GHI', '5.4.3.4.5', '3')

  it 'should find code by name', ->
    @valueSet.hasCode('DEF').should.be.true()

  it 'should find code by name and system', ->
    @valueSet.hasCode('DEF', '5.4.3.2.1').should.be.true()

  it 'should find code by name, system, and version', ->
    @valueSet.hasCode('DEF', '5.4.3.2.1', '2').should.be.true()

  it 'should find code by Code object', ->
    @valueSet.hasCode(new Code('DEF', '5.4.3.2.1', '2')).should.be.true()

  it 'should not find code with wrong name', ->
    @valueSet.hasCode('XYZ').should.be.false()

  it 'should not find code with wrong system', ->
    @valueSet.hasCode('DEF', '0.0.0.0.0').should.be.false()

  it 'should not find code with wrong version', ->
    @valueSet.hasCode('DEF', '5.4.3.2.1', '3').should.be.false()

  it 'should not find code with wrong Code object', ->
    @valueSet.hasCode(new Code('DEF', '5.4.3.2.1', '3')).should.be.false()
