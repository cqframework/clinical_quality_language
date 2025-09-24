parameter x Tuple { x Boolean }
parameter a List<List<Boolean>>

define b: a[1][1]

define c: if b then 'yes' else 'no'
