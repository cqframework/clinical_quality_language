{ Expression, UnimplementedExpression } = require './expression'

# TODO: Spec lists "Conditional", but it's "If" in the XSD
module.exports.If = class If extends UnimplementedExpression

module.exports.Case = class Case extends UnimplementedExpression

module.exports.CaseItem = class CaseItem extends UnimplementedExpression
