{ Expression, UnimplementedExpression } = require './expression'
{ FunctionRef } = require './reusable'
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

# TODO: Remove functionref when ELM does Combine natively
module.exports.CombineFunctionRef = class CombineFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    cmbJson = {
      "type" : "Combine",
      "source" : json.operand[0]
    }
    if json.operand.length > 1 then cmbJson["separator"] = json.operand[1]
    @combine = new Combine(cmbJson)

  exec: (ctx) ->
    @combine.exec(ctx)

module.exports.Split = class Split extends Expression
  constructor: (json) ->
    super
    @stringToSplit = build json.stringToSplit
    @separator = build json.separator

  exec: (ctx) ->
    stringToSplit = @stringToSplit.exec(ctx)
    separator = @separator.exec(ctx)
    if not (stringToSplit? and separator?) then null else stringToSplit.split(separator)

# TODO: Remove functionref when ELM does Split natively
module.exports.SplitFunctionRef = class SplitFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @split = new Split {
      "type" : "Split",
      "stringToSplit" : json.operand[0],
      "separator" : json.operand[1]
    }

  exec: (ctx) ->
    @split.exec(ctx)

# TODO: Also support on arrays?
module.exports.Length = class Length extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs ctx
    if arg? then arg.length else null

# TODO: Remove functionref when ELM does Length natively
module.exports.LengthFunctionRef = class LengthFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @length = new Length {
      "type" : "Length",
      "operand" : json.operand[0]
    }

  exec: (ctx) ->
    @length.exec(ctx)

module.exports.Upper = class Upper extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs ctx
    if arg? then arg.toUpperCase() else null

# TODO: Remove functionref when ELM does Upper natively
module.exports.UpperFunctionRef = class UpperFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @upper = new Upper {
      "type" : "Upper",
      "operand" : json.operand[0]
    }

  exec: (ctx) ->
    @upper.exec(ctx)

module.exports.Lower = class Lower extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs ctx
    if arg? then arg.toLowerCase() else null

# TODO: Remove functionref when ELM does Lower natively
module.exports.LowerFunctionRef = class LowerFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @upper = new Lower {
      "type" : "Lower",
      "operand" : json.operand[0]
    }

  exec: (ctx) ->
    @upper.exec(ctx)

# Indexer is completely handled by overloaded#Indexer

module.exports.Pos = class Pos extends Expression
  constructor: (json) ->
    super
    @pattern = build json.pattern
    @string = build json.string

  exec: (ctx) ->
    pattern = @pattern.exec(ctx)
    string = @string.exec(ctx)
    if not (pattern? and string?) then null else 1 + string.indexOf(pattern)

# TODO: Remove functionref when ELM does Pos natively
module.exports.PosFunctionRef = class PosFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @pos = new Pos {
      "type" : "Pos",
      "pattern" : json.operand[0],
      "string" : json.operand[1]
    }

  exec: (ctx) ->
    @pos.exec(ctx)

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
    else if startIndex < 1
      throw new Error "Start index must be at least 1"
    else if length? and length < 0
      throw new Error "Length must be at least zero"
    else if length?
      stringToSub.substr(startIndex-1, length)
    else
      stringToSub.substr(startIndex-1)

# TODO: Remove functionref when ELM does Substring natively
module.exports.SubstringFunctionRef = class SubstringFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    subJson = {
      "type" : "Substring",
      "stringToSub" : json.operand[0],
      "startIndex" : json.operand[1]
    }
    if json.operand.length > 2 then subJson["length"] = json.operand[2]
    @substring = new Substring(subJson)

  exec: (ctx) ->
    @substring.exec(ctx)
