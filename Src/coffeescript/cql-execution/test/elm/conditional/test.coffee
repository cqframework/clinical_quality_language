should = require 'should'
setup = require '../../setup'
data = require './data'

describe 'If', ->
  @beforeEach ->
    setup @, data

  it "should return the correct value when the expression is true", ->
    @ctx.set("var",true)
    @exp.exec(@ctx).should.equal "true return"

  it "should return the correct value when the expression is false", ->
    @ctx.set("var",false)
    @exp.exec(@ctx).should.equal "false return"


describe  'Case', ->
  @beforeEach ->
    setup @, data

  it "should be able to execute a standard case statement", ->    
    vals =  [{"x" : 1, "y" : 2, "message" : "X < Y"},
      {"x" : 2, "y" : 1, "message" : "X > Y"},
      {"x" : 1, "y" : 1, "message" : "X == Y"}]
    for item in vals
      @ctx.set("X", item.x)
      @ctx.set("Y", item.y)
      @standard.exec(@ctx).should.equal item.message

  it "should be able to execute a selected case statement", ->    
    vals = [{"var" : 1, "message" : "one"},
      {"var" : 2, "message" : "two"},
      {"var" : 3, "message" : 3}]
    for item in vals
      @ctx.set("var", item.var)
      @selected.exec(@ctx).should.equal item.message