/*
@issue: [#587](https://github.com/cqframework/clinical_quality_language/issues/587)
 */
library Issue587

define CollapseWithNullPer1: collapse null //this works
define CollapseWithNullPer2: collapse {null} //this doesnt work