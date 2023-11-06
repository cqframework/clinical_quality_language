library TestSoMuchNestingHidingComplex2

define "SoMuchNesting":
  ({
    {
        {1, 2, 3},
        {1, 2, 3 }
    },
    {
        {4, 5, 6},
        {4, 5, 6}
    },
    {
        {7, 8, 9},
        {7, 8, 9}
    }
  })
    "SoMuchNesting"
    let "SoMuchNesting":
        First("SoMuchNesting")
    return
        "SoMuchNesting"
            "SoMuchNesting"
            let "SoMuchNesting":
                Add("SoMuchNesting", 1)
            return "SoMuchNesting"
