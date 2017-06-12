{ Expression } = require './expression'
{ build } = require './builder'

module.exports.Retrieve = class Retrieve extends Expression
  constructor: (json) ->
    super
    @datatype = json.dataType
    @templateId = json.templateId
    @codeProperty = json.codeProperty
    @codes = build json.codes
    @dateProperty = json.dateProperty
    @dateRange = build json.dateRange

  exec: (ctx) ->
    records = ctx.findRecords(@templateId ? @datatype)
    if @codes
      valueset = @codes.exec(ctx)
      records = (r for r in records when valueset.hasCode(r.getCode(@codeProperty)))
    if @dateRange
      range = @dateRange.exec(ctx)
      records = (r for r in records when range.includes(r.getDateOrInterval(@dateProperty)))

    records
