library LogicalOperator version '1'

//And
define TrueAndTrue : true and true
define TrueAndFalse : true and false
define TrueAndNull : true and null
define FalseAndTrue : false and true
define FalseAndFalse : false and false
define FalseAndNull : false and null
define NullAndTrue : null and true
define NullAndFalse : null and false
define NullAndNull : null and null

//Implies
/*define TrueImpliesTrue : true implies true
define TrueImpliesFalse : true implies false
define TrueImpliesNull : true implies null
define FalseImpliesTrue : false implies true
define FalseImpliesFalse : false implies false
define FalseImpliesNull : false implies null
define NullImpliesTrue : null implies true
define NullImpliesFalse : null implies false
define NullImpliesNull : null implies null*/

//Not
define NotTrue : not true
define NotFalse : not false
define NotNull : not null

//Or
define TrueOrTrue : true or true
define TrueOrFalse : true or false
define TrueOrNull : true or null
define FalseOrTrue : false or true
define FalseOrFalse : false or false
define FalseOrNull : false or null
define NullOrTrue : null or true
define NullOrFalse : null or false
define NullOrNull : null or null

//Xor
define TrueXorTrue : true xor true
define TrueXorFalse : true xor false
define TrueXorNull : true xor null
define FalseXorTrue : false xor true
define FalseXorFalse : false xor false
define FalseXorNull : false xor null
define NullXorTrue : null xor true
define NullXorFalse : null xor false
define NullXorNull : null xor null
