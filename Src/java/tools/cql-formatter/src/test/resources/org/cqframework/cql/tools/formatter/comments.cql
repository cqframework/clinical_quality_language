// Single-line comment
// Nested // Single // Line comments
// Single-line
/*Multi-line one one line*/
/*
    Nice Multi-line
*/
/*
Not so nice Multi-line*/
define test1:
exists "A thing" /* Another not so good

        Location        Multi-line comment
        */
// single line



define "test 2":
// comment
[Condition] C
where C.onset is not null
and C.clinicalStatus = 'active'
