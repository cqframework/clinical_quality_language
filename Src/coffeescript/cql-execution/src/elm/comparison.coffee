{ Expression } = require './expression'
{ Uncertainty } = require '../datatypes/datatypes'
{ IncompatibleTypesException } = require './quantity'

# Equal is completely handled by overloaded#Equal

# NotEqual is completely handled by overloaded#Equal

module.exports.Less = class Less extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx).map (x) -> Uncertainty.from x
    try
      args[0].lessThan args[1]
    catch error
      if error instanceof IncompatibleTypesException
        return null
      else
        throw error

module.exports.LessOrEqual = class LessOrEqual extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx).map (x) -> Uncertainty.from x
    try
      args[0].lessThanOrEquals args[1]
    catch error
      if error instanceof IncompatibleTypesException
        return null
      else
        throw error

module.exports.Greater = class Greater extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx).map (x) -> Uncertainty.from x
    try
      args[0].greaterThan args[1]
    catch error
      if error instanceof IncompatibleTypesException
        return null
      else
        throw error

module.exports.GreaterOrEqual = class GreaterOrEqual extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx).map (x) -> Uncertainty.from x
    try
      args[0].greaterThanOrEquals args[1]
    catch error
      if error instanceof IncompatibleTypesException
        return null
      else
        throw error
