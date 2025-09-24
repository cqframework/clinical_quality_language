/*
@issue: [#596](https://github.com/cqframework/clinical_quality_language/issues/596)
 */
library Issue596

define NullBeforeInterval:
  (null as Integer) before Interval[1, 10]