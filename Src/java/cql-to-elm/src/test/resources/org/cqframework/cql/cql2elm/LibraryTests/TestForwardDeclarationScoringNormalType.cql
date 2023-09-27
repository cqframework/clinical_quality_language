library TestForwardDeclarationScoringNormalType

define ToString(value System.Integer): null

define ToString(value System.Decimal): null

define ToString(value System.Quantity):
   ToString(value.value)
