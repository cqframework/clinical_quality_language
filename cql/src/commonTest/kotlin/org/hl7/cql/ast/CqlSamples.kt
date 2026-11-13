package org.hl7.cql.ast

/** CQL that uses every kind of node, for tests that must see each one. */
object CqlSamples {

    /** A library with every kind of declaration. */
    val LIBRARY =
        """
            library Example.Measures version '1.0.0'

            using FHIR version '4.0.1'
            using QICore version '4.1.1' called QI

            include FHIRHelpers version '4.0.1' called FHIRHelpers
            include Common called C

            codesystem "LOINC": 'http://loinc.org' version '2.72'
            valueset "Visits": 'http://example.org/visits' version '1' codesystems { "LOINC", C."SNOMED" }
            private code "Glucose": '2345-7' from "LOINC" display 'Glucose'
            concept "Labs": { "Glucose", C."Other" } display 'Labs'
            parameter "Measurement Period" Interval<DateTime>
              default Interval[@2024-01-01T00:00:00, @2025-01-01T00:00:00)
            parameter Threshold Integer
            public parameter Flag default true

            context FHIR.Patient

            define "Visits In Period":
              [Encounter: type in "Visits"] E
                where E.period during "Measurement Period"

            define private Ordered:
              from "Visits In Period" V, [Condition] C
                let first: First(V.reason), count: Count(C.code)
                with [Procedure] P such that P.encounter = V.id
                without [Observation: code = "Glucose"] O such that O.encounter = V.id
                where C.subject = V.subject
                return all Tuple { visit: V, condition: C }
                sort by visit.id desc, condition.id

            define Total:
              ({ 1, 2, 3 }) X aggregate distinct R starting 0: R + X

            define fluent function Clean(value List<FHIR.Coding>, t Tuple { system String, code String }) returns Boolean:
              value.distinct().Count() > 0

            define function Lookup(code Choice<String, Integer>) returns String: external
            """
            .trimIndent()

    val EXPRESSIONS =
        listOf(
            // Operators
            "1 + 2",
            "1 - 2",
            "2 * 3",
            "4 / 2",
            "5 div 2",
            "5 mod 2",
            "2 ^ 3",
            "'foo' & 'bar'",
            "1 = 1",
            "1 != 2",
            "'abc' ~ 'abc'",
            "'abc' !~ 'def'",
            "1 < 2",
            "1 <= 2",
            "2 > 1",
            "2 >= 1",
            "true and false",
            "true or false",
            "true xor false",
            "true implies false",
            "not false",
            "exists { 1, 2 }",
            "{ 1, 2 } union { 3 }",
            "{ 1, 2 } | { 3 }",
            "{ 1, 2 } intersect { 2 }",
            "{ 1, 2 } except { 2 }",
            "1 in { 1, 2 }",
            "{ 'a' } contains 'a'",
            "X in day of Interval[@2020-01-01, @2020-02-01]",
            "Interval[1, 5] contains day of X",
            "1 + 2 * 3",
            "(1 + 2) * 3",
            "1 - (2 - 3)",
            "((1))",
            "-(1)",
            "-x",
            "- -1",
            "+1",
            "-x.y",
            "(-x).y",
            "-1 ^ 2",
            "not X is null",
            "(not X) is null",
            "not A = B",
            "exists X and Y",
            "exists (X and Y)",
            "A or B and C implies D",
            // Conditionals
            "if true then 1 else 0",
            "(if A then 1 else 2) + 3",
            "X and if A then B else C",
            "case when 1 = 1 then 'a' else 'b' end",
            "case X when 1 then 'a' when 2 then 'b' else 'c' end",
            "case when A then 1 else 2 end + 1",
            // Temporal
            "1 between 0 and 2",
            "X properly between A and B + 1",
            "duration in days between @2020-01-01T00:00:00 and @2020-01-05T00:00:00",
            "years between A and B",
            "difference in days between @2020-01-05T00:00:00 and @2020-01-01T00:00:00",
            "duration in days of Interval[1, 5]",
            "difference in days of Interval[1, 5]",
            "width of Interval[1, 5]",
            "successor of 1",
            "predecessor of 2",
            "singleton from { 1 }",
            "point from Interval[1, 5]",
            "minimum Integer",
            "maximum Integer",
            "convert 5 to String",
            "convert X to 'mg'",
            "convert X to days",
            "start of Interval[1, 5]",
            "end of Interval[1, 5]",
            "start of X + 1",
            "year from @2020-01-01T00:00:00",
            "timezoneoffset from @2020-01-01T00:00:00",
            "date from X",
            "A starts on or before start of B",
            "A starts before or on end of B",
            "A starts on or after start of B",
            "A ends after end of B",
            "A occurs 3 days or more before B",
            "A starts less than 1 year after start of B",
            "A before day of B",
            "A same as B",
            "A starts same day or before start of B",
            "A ends same month or after B",
            "A includes B",
            "A properly includes day of start B",
            "A during B",
            "A starts properly included in day of B",
            "A within 3 days of B",
            "A ends properly within 3 days of start B",
            "A meets B",
            "A meets before day of B",
            "A overlaps after B",
            "A starts B",
            "A ends day of B",
            // Lists
            "distinct { 1, 1, 2 }",
            "flatten { { 1 }, { 2 } }",
            "(distinct X) union Y",
            "expand Interval[@2020-01-01T00:00:00, @2020-01-03T00:00:00]",
            "expand X per day",
            "collapse X per 2 days",
            "(expand X) union Y",
            "{ 1, 2 }[0]",
            "(1 + 2)[1]",
            "{ }",
            "List<Integer>{ 1, 2, 3 }",
            "List<Choice<Integer, String>>{ 1, 'two' }",
            // Selectors and literals
            "Tuple { value: 1, flag: true }",
            "{ value: 1 }",
            "Tuple { : }",
            "System.Code{ code: '140', system: 'http://snomed.info/sct' }",
            "FHIR.Patient { : }",
            "Interval[1, 5]",
            "Interval(1, 5)",
            "Interval[1, 5)",
            "Code '140' from \"SNOMED\"",
            "Code '1\\'2' from Lib.\"SNOMED\" display 'Two'",
            "Concept { Code '1' from S, Code '2' from S } display 'Both'",
            "5 'mg'",
            "3 days",
            "1 year",
            "5 'mg':10 'mL'",
            "5 'mg/dL'",
            "10L",
            "-10L",
            "-9223372036854775808L",
            "2147483647",
            "-2147483648",
            "-1.5",
            "1.50",
            "'it\\'s'",
            "'a\\tb\\nc'",
            "'\\u0041\\\\'",
            "'a/b'",
            "\"a\\\"b\"",
            "`a\\`b`",
            "\"\"",
            "@2020",
            "@2020-01-01",
            "@2020-01-01T00:00:00.000Z",
            "@T10:30:00",
            "null",
            "true",
            "%MeasurementPeriod",
            "%default",
            "%'Quoted Constant'",
            "%\"Quoted Identifier\"",
            // Types
            "1 is Integer",
            "1 as Integer",
            "cast 1 as Integer",
            "X as List<FHIR.Coding>",
            "X is Interval<DateTime>",
            "X is Tuple { a Integer, b String }",
            "X is null",
            "X is not null",
            "X is true",
            "X is not false",
            // Invocation
            "X.y.z",
            "X.f(1, 2)",
            "F()",
            "F(X, Y)",
            "\$this",
            "X.where(\$this > 1)",
            "Lib.\"Some Definition\"",
            // Queries and retrieves
            "[Encounter]",
            "[Encounter: \"Visits\"]",
            "[Encounter: type in \"Visits\"]",
            "[Encounter: type ~ \"Visit\"]",
            "[Encounter: Lib.\"Visits\"]",
            "[Patient -> Encounter: type in \"Visits\"]",
            "[Encounter: reason.coding in \"Visits\"]",
            "[Observation] O return O",
            "from [Observation] O return O",
            "from A a, B b return a",
            "[Encounter] E where E.status = 'finished'",
            "[Encounter] E let x: 1, y: 2 where x < y",
            "[Encounter] E with [Condition] C such that C.id = E.id",
            "[Encounter] E without [Condition] C such that C.id = E.id return E",
            "[Encounter] E return distinct E.id",
            "[Encounter] E return all E.id",
            "[Encounter] E sort asc",
            "[Encounter] E sort descending",
            "[Encounter] E sort by id desc, period.start",
            "({ 1, 2 }) X aggregate R starting 0: R + X",
            "({ 1, 2 }) X aggregate distinct R starting (1 + 1): R + X",
            "({ 1, 2 }) X aggregate R starting 'a': R & X",
            "({ 1, 2 }) X aggregate R: R",
            "exists [Encounter] E where E.x",
            "exists ([Encounter] E where E.x) and Y",
            "Count([Encounter] E where E.x)",
            "([Encounter] E where E.x) union Y",
            "[Encounter] E where E.x in ([Condition] C where C.y return C.x)",
            "[Encounter] E where exists (E.x X where X.y) return E",
            "([Encounter]).id",
            // Prefix operators are read whole, so one can be another's operand
            "\$index",
            "\$total",
            "month from X",
            "week from X",
            "day from X",
            "hour from X",
            "minute from X",
            "second from X",
            "millisecond from X",
            "time from X",
            "timezone from X",
            "predecessor of X + 1",
            "A occurs 3 days or less before B",
            "A occurs 3 days before B",
            "A starts more than 1 year after start B",
            "A starts same day as end B",
            "[Encounter: F(X)]",
            "[Encounter] E sort ascending",
            "({ 1 }) X aggregate R starting 1 'mg': R",
            "not exists X",
            "not exists X and not exists Y",
            "exists not X",
            "- start of X",
            // A query without clauses ends at its alias
            "[Observation] a union [Procedure] b",
            // A query in a clause takes a following clause only if it could come next
            "Patient P let a: X x where x.y, b: Y y where y.z where a = b",
            "X x where Y y where y.z return x",
            "X x where (Y y return y.z) return x",
            "X x let a: Y y where y.z with Z z such that z.a return x",
        )
}
