// We could define same as generally:

A same as B
// is equivalent to
(A is null and B is null) or (A = B)

// Then Date/Time Equality could be defined as
A = B
// is equivalent to
year of A = year of B
	and month of A = month of B
	and day of A = day of B
	and hour of A = hour of B
	and minute of A = minute of B
	and second of A = second of B
	and millisecond of A = millisecond of B

A same as B
// is equivalent to
year of A same as year of B
	and month of A same as month of B
	and day of A same as day of B
	and hour of A same as hour of B
	and minute of A same as minute of B
	and second of A same as second of B
	and millisecond of A same as millisecond of B

// and for precision-based equality
A same day as B
// is equivalent to
year of A same as year of B
	and month of A same as month of B
	and day of A same as day of B

// Date/Time Equality
A = B
// is equivalent to
((year of A is null and year of B is null) or (year of A = year of B))
	and ((month of A is null and month of B is null) or (month of A = month of B))
	and ((day of A is null and day of B is null) or (day of A = day of B))
	and ((hour of A is null and hour of B is null) or (hour of A = hour of B))
	and ((minute of A is null and minute of B is null) or (minute of A = minute of B))
	and ((second of A is null and second of B is null) or (second of A = second of B))
	and ((millisecond of A is null and millisecond of B is null) or (millisecond of A = millisecond of B))

// Date/Time Relative Comparison
A > B
// is equivalent to
year of A > year of B
	or (year of A = year of B and month of A > month of B)
	or (year of A = year of B and month of A = month of B and day of A > day of B)
	or (year of A = year of B and month of A = month of B and day of A = day of B and hour of A > hour of B)
	or (year of A = year of B and month of A = month of B and day of A = day of B and hour of A = hour of B and minute of A > minute of B)
	or (year of A = year of B and month of A = month of B and day of A = day of B and hour of A = hour of B and minute of A = minute of B and second of A > second of B)
	or (year of A = year of B and month of A = month of B and day of A = day of B and hour of A = hour of B and minute of A = minute of B and second of A = second of B and millisecond of A > millisecond of B)

// Date/Time Equality Comparison
A >= B
// is equivalent to
A = B or A > B

// Date/Time Comparison w/ Relative Qualifier (at least/at most)
A at least same day as B
// is equivalent to
A same day as B or A > B

// Durations
days between A and B

// returns number of boundaries crossed

// extended to return an interval
days between Date(2014, 1, 15) and Date(2014, 2)
// returns interval[16, 44]

// define implicit conversion from a point to an interval
// allow interval comparison operators
// = (same as)
// > (after)
// >= (after or same as)
// < (before)
// <= (before or same as)
days between Date(2014, 1, 15) and Date(2014, 2) > 2
// results in
interval[17, 44] > [2, 2]
days between Date(2014, 1, 15) and Date(2014, 2) > 20
// results in 
interval[17, 44] > [20, 20] // potentially should return unknown

// Timing Phrase Translations
// same as
A same as B
A starts same as start B
A starts same day as start B

// at least/most same as
A starts at least same day as B
A starts at most same day as B

// before/after
A starts before start B
start of A < start of B
A starts after start B
start of A > start of B

// duration before/after
A starts 3 days before start B
days between start of A and start of B = -3
A starts 3 days after start B
days between start of A and start of B = 3

// at least/most duration before/after
A starts at least 3 days before start B
days between start of A and start of B > -3
A starts at most 3 days after start B
days between start of A and start of B < 3

// within duration
A starts within 3 days of start B
days between start of A and start of B in [-3, 3]

// during
A starts during B

// includes
A includes start B


[Encounter, Performance: "A"] A
  with [Condition: "B"] B
  with [Medication: "C"] C
  where A.effectiveTime overlaps after B.effectiveTime
  where C.effectiveTime overlaps after B.effectiveTime
  where A.effectiveTime overlaps after C.effectiveTime

let Medications =
	[Medication: "C"] C
		with [Condition: "B"] B
			where C.effectiveTime overlaps after B.effectiveTime

let Encounters =
	[Encounter, Performance: "A"] A
		with [Condition: "B"] B where A.effectiveTime overlaps after B.effectiveTime
		with Medications C where A.effectiveTime overlaps after C.effectiveTime



// Introduce the notion of an uncertainty range:
uncertainty[17, 44]

// Equality
uncertainty[17, 44] = 2 // returns false
uncertainty[17, 44] = 20 // returns unknown
uncertainty[17, 44] = 45 // returns false

// if the value being compared falls within the uncertainty range, the result is unknown, otherwise the result is false

uncertainty[17, 44] = uncertainty[17, 44] // returns unknown
uncertainty[17, 44] = uncertainty[2, 12] // returns false
uncertainty[17, 44] = uncertainty[2, 20] // returns unknown

// If the uncertainty ranges overlap in any way, the result is unknown, otherwise the result is false

// Comparison
uncertainty[17, 44] > 2 // returns true
uncertainty[17, 44] > 20 // returns unknown
uncertainty[17, 44] > 45 // returns false

// If the value being compared falls within the uncertainty range, the result is unknown, otherwise, the result is based on the comparison to the relevant boundary

uncertainty[17, 44] > uncertainty[17, 44] // returns unknown
uncertainty[17, 44] > uncertainty[2, 12] // returns true
uncertainty[17, 44] > uncertainty[2, 20] // returns unknown
uncertainty[17, 44] > uncertainty[20, 20] // returns unknown
uncertainty[17, 44] > uncertainty[20, 50] // returns unknown
uncertainty[17, 44] > uncertainty[45, 50] // returns false

// If the uncertainties overlap in any way, the result is unknown, otherwise, the result is based on the comparison to the relevant boundaries

// Arithmetic

uncertainty[17, 44] + 2 // returns uncertainty[19, 46]


