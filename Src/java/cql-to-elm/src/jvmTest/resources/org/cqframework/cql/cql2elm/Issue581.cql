/*
@issue: [#581](https://github.com/cqframework/clinical_quality_language/issues/581)
 */
library Scratchpad

define function Quantity(value Decimal, unit String):
  if value is not null then
    Quantity { value: value, unit: unit }
  else
    null