library ParameterReferences version '1.0.0'
using System
parameter MyParam Integer default 42
context Patient

define UseParam: MyParam
define AddParam: MyParam + 1
