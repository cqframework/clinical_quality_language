{ Expression } = require './expression'

module.exports.Literal = class Literal extends Expression
  @from: (json) ->
    switch(json.valueType)
      when "{urn:hl7-org:elm-types:r1}Boolean" then new BooleanLiteral(json)
      when "{urn:hl7-org:elm-types:r1}Integer" then new IntegerLiteral(json)
      when "{urn:hl7-org:elm-types:r1}Decimal" then new DecimalLiteral(json)
      when "{urn:hl7-org:elm-types:r1}String" then new StringLiteral(json)
      else new Literal(json)

  constructor: (json) ->
    super
    @valueType = json.valueType
    @value = json.value

  exec: (ctx) ->
    @value

# The following are not defined in ELM, but helpful for execution

module.exports.BooleanLiteral = class BooleanLiteral extends Literal
  constructor: (json) ->
    super
    @value = @value is 'true'

  exec: (ctx) ->
    @value

module.exports.IntegerLiteral = class IntegerLiteral extends Literal
  constructor: (json) ->
    super
    @value = parseInt(@value, 10)

  exec: (ctx) ->
    @value

module.exports.DecimalLiteral = class DecimalLiteral extends Literal
  constructor: (json) ->
    super
    @value = parseFloat(@value)

  exec: (ctx) ->
    @value

module.exports.StringLiteral = class StringLiteral extends Literal
  constructor: (json) ->
    super

  exec: (ctx) ->
    @value
