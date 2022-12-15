library DateOrDateTimeInNullIntervalTest version '1'

define "Null Interval":
   Interval[null, null]

define "Date":
    @2018-01-01

define "DateTime":
    @2018-01-01T00:00:00.0

define "Date in Null Interval Test":
    "Date" during day of "Null Interval"

define "DateTime in Null Interval Test":
    "DateTime" during day of "Null Interval"
