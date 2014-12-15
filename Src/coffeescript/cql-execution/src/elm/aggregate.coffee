{ Expression, UnimplementedExpression } = require './expression'

module.exports.Count = class Count extends UnimplementedExpression

module.exports.Sum = class Sum extends UnimplementedExpression

module.exports.Min = class Min extends UnimplementedExpression

module.exports.Max = class Max extends UnimplementedExpression

module.exports.Avg = class Avg extends UnimplementedExpression

module.exports.Median = class Median extends UnimplementedExpression

module.exports.Mode = class Mode extends UnimplementedExpression

module.exports.Variance = class Variance extends UnimplementedExpression

module.exports.PopulationVariance = class PopulationVariance extends UnimplementedExpression

module.exports.StdDev = class StdDev extends UnimplementedExpression

module.exports.PopulationStdDev = class PopulationStdDev extends UnimplementedExpression

module.exports.AllTrue = class AllTrue extends UnimplementedExpression

module.exports.AnyTrue = class AnyTrue extends UnimplementedExpression
