// CQL file with no library declaration, no using, no context.
// Exercises DateTime constructor calls which trigger a chunk-tracking error
// in the legacy translator after the first expression definition.
define First: DateTime(2014, 1, 1) + 1 day
define Second: DateTime(2014, 12, 31) after DateTime(2014, 1, 1)
define Third: DateTime(2014, 1, 1) before DateTime(2014, 12, 31)
