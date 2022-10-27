library Deprecated version '1.0.0'

/**
* @deprecated
*/
define "Deprecated Function":
    2 + 2

define "Deprcated Warning":
    "Deprecated Function" // This should get a warning that you are using a deprecated function

/**
* @no-warning
*/
define "Suppressed Warning":
    "Deprecated Function" // This should NOT get a warning that you are using a deprecated function


