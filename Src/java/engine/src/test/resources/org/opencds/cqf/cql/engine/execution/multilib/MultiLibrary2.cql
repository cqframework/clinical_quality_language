library MultiLibrary2 version '1.0.0'

parameter "Measurement Period" Interval<DateTime> default Interval[@2022-01-01, @2023-01-01)

define "Number": 2
define "Period": "Measurement Period"
define "Name": 'Dos'
define "MultiLibraryIdent2": 'MultiLibrary2'
define "MultiLibraryValue2": 'Two'
