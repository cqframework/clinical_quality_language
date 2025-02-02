library Content.Main

include Core.Main called CoreMain
include Shared.Main called SharedMain

define Test: 2 + 2

define ReferencingTest: CoreMain.Test + SharedMain.Test + Test