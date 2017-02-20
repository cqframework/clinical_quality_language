{ Expression } = require './expression'
{ build } = require './builder'

module.exports.Concat = class Concat extends Expression
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
    source = @source.exec(ctx)
    separator = if @separator? then @separator.exec(ctx) else ''
    if (not source? or source.some (x) -> not x?) then null else source.join(separator)

module.exports.Split = class Split extends Expression
  constructor: (json) ->
    super
    @stringToSplit = build json.stringToSplit
    @separator = build json.separator

  exec: (ctx) ->
    stringToSplit = @stringToSplit.exec(ctx)
    separator = @separator.exec(ctx)
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
    pattern = @pattern.exec(ctx)
    string = @string.exec(ctx)
    if not (pattern? and string?) then null else string.indexOf(pattern)

module.exports.Substring = class Substring extends Expression
  constructor: (json) ->
    super
    @stringToSub = build json.stringToSub
    @startIndex = build json.startIndex
    @length = build json['length']

  exec: (ctx) ->
    stringToSub = @stringToSub.exec(ctx)
    startIndex = @startIndex.exec(ctx)
    length = if @length? then @length.exec(ctx) else null
    if not (stringToSub? and startIndex?)
      null
    else if startIndex < 0
      throw new Error "Start index must be at least zero"
    else if length? and length < 0
      throw new Error "Length must be at least zero"
    else if length?
      stringToSub.substr(startIndex, length)
    else
      stringToSub.substr(startIndex)
