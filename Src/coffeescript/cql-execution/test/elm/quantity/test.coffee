should = require 'should'
setup = require '../../setup'
{Quantity} = require '../../../lib/elm/quantity'

describe 'Quantity', ->
  it 'should allow creation of Quantity with valid ucum units', ->
    should.doesNotThrow ->
      new Quantity({unit: "mm", value: 42.424242})

  it 'should allow creation of Quantity with valid ucum units on multiple uses of same unit', ->
    should.doesNotThrow ->
      new Quantity({unit: "cm", value: 42.424242})
      new Quantity({unit: "cm", value: 43.434242})

  it 'should allow creation of Quantity with valid ucum converted time units', ->
    should.doesNotThrow ->
      new Quantity({unit: "years", value: 3})

  it 'should throw error when creating Quantity with invalid ucum units', ->
    should.throws ->
      new Quantity({unit: "quacks", value: 42.424242})

  it 'should throw error when creating Quantity with invalid ucum units on multiple uses of same unit', ->
    should.throws ->
      new Quantity({unit: "caches", value: 42.424242})
    should.throws ->
      new Quantity({unit: "caches", value: 44.4242242})

  it 'should allow creation of Quantity with no unit', ->
    should.doesNotThrow ->
      new Quantity({value: 9})

  it 'should allow creation of Quantity with empty string unit', ->
    should.doesNotThrow ->
      new Quantity({unit: "", value: 9})
