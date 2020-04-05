library Content.ReferencingMain

include Shared.Main
include Core.ReferencingMain
include Shared.ReferencingMain called SharedReferencingMain

define Test: 2 + 2;

define ReferencingTest: Test + Main.Test + ReferencingMain.Test + SharedReferencingMain.Test