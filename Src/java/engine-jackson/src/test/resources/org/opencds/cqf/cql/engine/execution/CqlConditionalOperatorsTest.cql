library ConditionalOperator version '1'

define x: 5
define y: 10
define z: null

// if-then-else
define IfTrue1:
  if y > x then x else y

define IfFalse1:
  if y = x then y + x else y - x

define IfNull1:
  if y = z then x else y

// standard case
define StandardCase1:
  case
    when y > x then x
    when x > y then y
    else z
  end

define StandardCase2:
  case
    when x > y then x + y
    when x = y then x
    else y - x
  end

define StandardCase3:
  case
    when z ~ y then z + y
    when z ~ x then x
    else x + y
  end

// selected case
define SelectedCase1:
  case x
    when 5 then 12
    when 10 then y + x
    else y - x
  end

define SelectedCase2:
  case y
    when 5 then 12
    when 10 then y + x
    else y - x
  end

define SelectedCase3:
  case y + x
    when 5 then 12
    when 10 then y + x
    else y - x
  end
