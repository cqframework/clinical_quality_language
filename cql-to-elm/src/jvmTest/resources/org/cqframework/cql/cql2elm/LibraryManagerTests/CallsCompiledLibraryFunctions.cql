library CallsCompiledLibraryFunctions

include LibraryWithFunction
include LibraryWithOverloadedFunctions

define "Eight": LibraryWithFunction.Double(4)
define "Described": LibraryWithOverloadedFunctions.Describe('b')
