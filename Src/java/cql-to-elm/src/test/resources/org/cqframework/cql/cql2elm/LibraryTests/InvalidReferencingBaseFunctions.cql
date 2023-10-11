library InvalidReferencingBaseFunctions

include BaseFunctions called Base

define private function fInner(arg String): arg

define FunctionTestInnerPrivate: fInner('hello')

define FunctionTestOuterPrivate: Base.f1('hello')

