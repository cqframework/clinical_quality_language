/*
This test should produce an error, but with ANTLR 4.6+, the parser pulls in the list selector,
and then ignores the rest of the expression.
*/

define TestShouldFail: { 1, 2, 3 } A bunch of gobbledygook that ANTLR ignores