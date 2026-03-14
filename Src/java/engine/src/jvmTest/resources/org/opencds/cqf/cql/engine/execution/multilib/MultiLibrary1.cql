library MultiLibrary1 version '1.0.0'

parameter "Measurement Period" Interval<DateTime> default Interval[@2021-01-01, @2022-01-01)

define "Number": 1
define "Period": "Measurement Period"
define "Name": 'Uno'
define "MultiLibraryIdent1": 'MultiLibrary1'
define "MultiLibraryValue1": 'One'
