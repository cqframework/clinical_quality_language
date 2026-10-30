library FunctionMemoizationTest

parameter Offset Integer default 0

parameter Start DateTime default Now()

define function Slow(X Integer): X + Offset

define function SlowInterval(I Interval<Integer>): start of I + Offset

define function Shifted(X Integer): Now() + Quantity { value: ToDecimal(X), unit: 'day' }

define function Noisy(X Integer): Message(X, true, 'Noisy', 'Message', 'noisy')

define function CallsNoisy(X Integer): Noisy(X) + 1

define Xs: (expand { Interval[1, 10] }) i return all start of i

define Ys: (expand { Interval[1, 20] }) i return all start of i

// Slow(A) depends only on the outer alias, but runs once per (A, B) pair
define CrossJoin: from Xs A, Ys B where Slow(A) <= B return all A

// Never satisfied, so every B is checked for every A
define WithoutClause: Xs A without Ys B such that Slow(A) > B + 100

// The Interval argument is a new object on every iteration
define FreshInterval: from Xs A, Ys B where SlowInterval(Interval[A, A + 1]) = B return all A

define Timestamps: from Xs A, Ys B return all Shifted(A)

define Messages: from Xs A, Ys B where Noisy(A) = B return all A

define TransitiveMessages: from Xs A, Ys B where CallsNoisy(A) = B return all A

define OffsetValue: Offset

define StartValue: Start
