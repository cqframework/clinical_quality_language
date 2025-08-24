library TestSoMuchNestingHidingNormal

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
   oneLevel
   let twoLevel:
       First(oneLevel)
   return
       twoLevel
           threeLevel
           let fourLevel:
               Add(threeLevel, 1)
           return fourLevel