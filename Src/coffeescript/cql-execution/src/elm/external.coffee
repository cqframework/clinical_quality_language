{ Expression } = require './expression'
{ build } = require './builder'
{ typeIsArray } = require '../util/util'

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
    codes = @codes
    if @codes && typeof @codes.exec == 'function'
      codes = @codes.execute(ctx)
    if codes
      records = records.filter (r) => @recordMatchesCodesOrVS(r, codes)
    # TODO: Added @dateProperty check due to previous fix in cql4browsers in cql_qdm_patient_api hash: ddbc57
    if @dateRange && @dateProperty
      range = @dateRange.execute(ctx)
      records = (r for r in records when range.includes(r.getDateOrInterval(@dateProperty)))

    records

  recordMatchesCodesOrVS: (record, codes) ->
    if typeIsArray codes
      codes.some (c) => c.hasMatch(record.getCode(@codeProperty))
    else
      codes.hasMatch(record.getCode(@codeProperty))