library QueryOptimizationTest

define function Double(X Integer): X * 2

define function Noisy(X Integer): Message(X, true, 'Noisy', 'Message', 'noisy')

define Xs: (expand { Interval[1, 10] }) i return all start of i

define Ys: (expand { Interval[1, 20] }) i return all start of i

define Empty: List<Integer> { }

define WithNulls: { 1, null, 3, null, 5 }

define Items:
  {
    Tuple { id: 1, tags: { 'a', 'b' } },
    Tuple { id: 2, tags: { 'b' } },
    Tuple { id: 3, tags: List<String> { } },
    Tuple { id: 4, tags: null as List<String> }
  }

define "With": Xs X with Ys Y such that Double(X) = Y

define "Row, related and pair conjuncts": Xs X with Ys Y such that X > 3 and Y < 15 and Double(X) = Y

define "Without": Xs X without Ys Y such that X mod 2 = 0 and Double(X) = Y + 1

define "Null conjuncts": WithNulls X with WithNulls Y such that X = Y and Y > 1

define "With empty": Xs X with Empty E such that X = E

define "Without empty": Xs X without Empty E such that X > 100

define "Correlated source": Items I with I.tags T such that T = 'b' and I.id > 1

define "Correlated without": Items I without I.tags T such that T = 'a'

define "Nested query": Xs X with Ys Y such that exists (Ys Z where Z = X + Y)

define "Conditional": Xs X with Ys Y such that (if X > 5 then Double(X) else X) = Y

define "Two relationships": Xs X with Ys Y such that Y = Double(X) without Ys Z such that Z = X + 15

define "Let": from Xs X let D: Double(X) with Ys Y such that Y = D return X

define "Shadowed alias": Xs X with Ys Y such that exists (Xs Y where Y = X)

define "Noisy flag": Message(true, true, 'Noisy', 'Message', 'noisy')

define "Message": Xs X with Ys Y such that Noisy(X) = Y

define "Message through expression": Xs X with Ys Y such that X = Y and "Noisy flag"

define "Message in cross product": from Xs A, Ys B where Noisy(A) = B return all A

define "Cross product": from Xs A, Ys B where A > 2 and B < 5 and Double(A) > B return all Tuple { a: A, b: B }

define "Three sources": from Xs A, Ys B, Xs C where A + B = C and B > 3 and C < 9 return all Tuple { a: A, b: B, c: C }

define "Invariant false": from Xs A, Ys B where 1 > 2 return all A

define "Invariant true": from Xs A, Ys B where true and A = B return all A

define "Cross product with empty": from Xs A, Empty B where A = B return all A

define "Default return": from Xs A, Ys B where A = B and A < 4

define "Distinct return": from Xs A, Ys B where A < 3 and B < 3 return A + B

define "Sorted": from Xs A, Ys B where A = B * 2 return all A sort desc

define "Non-list source": from (5) A, Ys B where A = B return all B
