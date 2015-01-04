// @Test: If

define exp = if var then 'true return' else 'false return'

// @Test: Case

define selected = 
  case var
   when 1 then 'one'
   when 2 then 'two'
   else 
    var
  end

define standard = 
  case
    when X > Y then 'X > Y'
    when X < Y then 'X < Y'
    else 'X == Y'
  end