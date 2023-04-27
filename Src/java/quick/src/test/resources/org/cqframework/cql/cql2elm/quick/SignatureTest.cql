library SignatureTest

using USCore version '3.1.0'

include FHIRHelpers version '4.0.1'


define TestSlices:
  ["observation-bp"] BP
    where BP.SystolicBP.value < 140 'mm[Hg]'