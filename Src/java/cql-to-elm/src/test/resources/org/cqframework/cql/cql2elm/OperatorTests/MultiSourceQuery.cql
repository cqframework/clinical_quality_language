library multisource_example

// The below multi-source query should return a list of tuples like this:
// { Odd: 1, Even: 2 },
// { Odd: 1, Even: 4 },
// { Odd: 3, Even: 2 },
// { Odd: 3, Even: 4 }
//
// This is because a multi-source query returns a cartesion product (as described in the spec).
define oddsAndEvens:
  from (List{1, 3}) Odd, (List{2, 4}) Even

// Since the value of Odd in each tuple is an integer (not a list), it should be promoted
// to a list, or CQL-to-ELM should produce an error.  But... it goes through unscathed!
define shouldNotBeAllowed:
  (oddsAndEvens) OE where OE.Odd includes 3