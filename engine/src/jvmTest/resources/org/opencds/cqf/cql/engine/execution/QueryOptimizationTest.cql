library QueryOptimizationTest

codesystem "S": 'http://example.org/s'

define function Key(X Integer): X

define function Check(X Integer, Y Integer): true

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
    Tuple { id: 4, tags: null as List<String> },
    Tuple { id: 5, tags: { 'b' } }
  }

define Names: { 'a', 'b', 'c', 'b' }

define Decimals: { 1.0, 2.50, 3.0, null }

define OtherDecimals: { 1.00, 2.5, 4.0 }

define Codes: { Code '1' from "S" display 'one', Code '1' from "S" display 'uno', Code '2' from "S" }

define Remainders: { 0, 1, 1, 2, null, 0 }

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

define "Equi strings": Names A with Names B such that A = B and A != 'c'

define "Equi decimal scale": Decimals D with OtherDecimals E such that D = E

define "Equi decimal without": Decimals D without OtherDecimals E such that E = D

define "Equi codes": Codes C with Codes D such that C = D

define "Equi codes by display": Codes C with Codes D such that C = D and C.display != D.display

define "Equi unhashable": Items I with Items J such that I.tags = J.tags and I.id != J.id

// Check(X, Y) is evaluated for every pair visited, so it counts the pairs
define "Equi nested key": Xs X with Ys Y such that Check(X, Y) and Key(Y) = Double(X)

define "Equi cross product checked": from Xs A, Ys B where Check(A, B) and B = Double(A) return all Tuple { a: A, b: B }

define "Equi cross product": from Xs A, Ys B where B = Double(A) return all Tuple { a: A, b: B }

define "Equi cross product duplicates": from Ys B, Remainders R where R = B mod 3 return all Tuple { b: B, r: R }

define "Equi three sources": from Xs A, Ys B, Xs C where B = A + 1 and C = B - 1 return all Tuple { a: A, b: B, c: C }
