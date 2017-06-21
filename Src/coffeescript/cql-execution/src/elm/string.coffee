{ Expression } = require './expression'
{ build } = require './builder'

module.exports.Concatenate = class Concatenate extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    if (args.some (x) -> not x?) then null else args.reduce (x,y) -> x + y

module.exports.Combine = class Combine extends Expression
  constructor: (json) ->
    super
    @source = build json.source
    @separator = build json.separator

  exec: (ctx) ->
    source = @source.execute(ctx)
    separator = if @separator? then @separator.execute(ctx) else ''
    if (not source? or source.some (x) -> not x?) then null else source.join(separator)

module.exports.Split = class Split extends Expression
  constructor: (json) ->
    super
    @stringToSplit = build json.stringToSplit
    @separator = build json.separator

  exec: (ctx) ->
    stringToSplit = @stringToSplit.execute(ctx)
    separator = @separator.execute(ctx)
    if not (stringToSplit? and separator?) then null else stringToSplit.split(separator)

# Length is completely handled by overloaded#Length

module.exports.Upper = class Upper extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs ctx
    if arg? then arg.toUpperCase() else null

module.exports.Lower = class Lower extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs ctx
    if arg? then arg.toLowerCase() else null

# Indexer is completely handled by overloaded#Indexer

module.exports.PositionOf = class PositionOf extends Expression
  constructor: (json) ->
    super
    @pattern = build json.pattern
    @string = build json.string

  exec: (ctx) ->
    pattern = @pattern.execute(ctx)
    string = @string.execute(ctx)
    if not (pattern? and string?) then null else string.indexOf(pattern)

module.exports.Substring = class Substring extends Expression
  constructor: (json) ->
    super
    @stringToSub = build json.stringToSub
    @startIndex = build json.startIndex
    @length = build json['length']

  exec: (ctx) ->
    stringToSub = @stringToSub.execute(ctx)
    startIndex = @startIndex.execute(ctx)
    length = if @length? then @length.execute(ctx) else null
    # According to spec: If stringToSub or startIndex is null, or startIndex is out of range, the result is null.
    if not stringToSub? || not startIndex? || startIndex < 0 || startIndex >= stringToSub.length
      null
    else if length?
      stringToSub.substr(startIndex, length)
    else
      stringToSub.substr(startIndex)
