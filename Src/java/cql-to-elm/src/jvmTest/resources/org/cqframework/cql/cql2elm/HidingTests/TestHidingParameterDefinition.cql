library TestHidingParameterDefinition

parameter "Measurement Period" Interval<DateTime>

define function HidingParameterDefinition(testOperand Integer):
    ({1, 2, 3}) "Measurement Period"
        return "Measurement Period"
