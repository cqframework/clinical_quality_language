parameter x tuple { x Boolean }
parameter a list<list<Boolean>>

define b : a[1][1]

define c : if b then 'yes' else 'no'

