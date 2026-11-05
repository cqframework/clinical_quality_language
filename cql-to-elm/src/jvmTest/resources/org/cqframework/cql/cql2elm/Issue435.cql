/*
@issue: [#435](https://github.com/cqframework/clinical_quality_language/issues/435)
 */
library Issue435

/*
These both now correctly result in ambiguous resolution among the various overloads of Equal for the system types
*/
define Test1: null ~ null
define Test2: null = null