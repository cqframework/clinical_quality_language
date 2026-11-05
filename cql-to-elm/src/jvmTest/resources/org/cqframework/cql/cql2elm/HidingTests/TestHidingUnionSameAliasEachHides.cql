library TestHidingUnionSameAliasEachHides

define "X" : 'X'
define "Y" : 'Y'
define IWantToBeHidden : 'E'

define "UnionSameAlias":
   ("X" IWantToBeHidden) union ("Y" IWantToBeHidden)
