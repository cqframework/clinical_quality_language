grammar cql;

/*
 * Parser Rules
 */

logic:
	usingDefinition*
	contextDefinition?
	includeDefinition*
	parameterDefinition*
	valuesetDefinition*
	statement+;

/*
 * Definitions
 */

usingDefinition: 'using' IDENTIFIER ('version' STRING)?;

contextDefinition: 'context' IDENTIFIER;

includeDefinition: 'include' IDENTIFIER ('version' STRING)?;

parameterDefinition: 'parameter' IDENTIFIER (':' typeSpecifier)? ('default' expression)?;

valuesetDefinition: 'valueset' STRING '=' expression;

/*
 * Type Specifiers
 */

typeSpecifier:
    atomicTypeSpecifier |
    listTypeSpecifier |
    intervalTypeSpecifier |
    tupleTypeSpecifier;

atomicTypeSpecifier: IDENTIFIER; // TODO: specify atomic type names as part of the grammar?

listTypeSpecifier: 'list' '<' typeSpecifier '>';

intervalTypeSpecifier: 'interval' '<' typeSpecifier '>';

tupleTypeSpecifier: 'tuple' '{' tupleElementDefinition (',' tupleElementDefinition)* '}';

tupleElementDefinition: IDENTIFIER ':' typeSpecifier;

/*
 * Statements
 */

statement:
    letStatement |
    operatorDefinition |
    retrieveDefinition;

letStatement: 'let' IDENTIFIER '=' query;

operatorDefinition:
    'define' 'operator' IDENTIFIER '(' (operandDefinition (',' operandDefinition)*)? ')' operatorBody;

operandDefinition: IDENTIFIER ':' typeSpecifier;

operatorBody:
    '{' returnStatement '}';

returnStatement:
    'return' expression;

retrieveDefinition:
    'define' 'retrieve' existenceModifier? '[' topicType (',' activityType)? (':' valuesetPathIdentifier 'in' valuesetIdentifier)? (',' duringPathIdentifier 'during' duringIdentifier)? ']' operatorBody;

valuesetPathIdentifier: IDENTIFIER;

valuesetIdentifier: IDENTIFIER;

duringPathIdentifier: IDENTIFIER;

duringIdentifier: IDENTIFIER;

/*
 * Expressions
 */

query:
    querySource |
    aliasedQuerySource queryInclusionClause* ('where' expression)? |
    'union' '(' query (',' query)+ ')' |
    'intersect' '(' query (',' query)+ ')' |
    query 'except' query |
    expression;

querySource:
    retrieve | IDENTIFIER;

aliasedQuerySource:
    querySource alias;

alias: IDENTIFIER;

queryInclusionClause:
    'with' aliasedQuerySource 'where' expression |
    'combine' aliasedQuerySource 'where' expression
;

retrieve: existenceModifier? '[' topicType (',' activityType)? (':' (IDENTIFIER 'in')? valueset)? (',' IDENTIFIER? 'during' expression)? ']';

existenceModifier: 'no' | 'unknown';

topicType: IDENTIFIER;

activityType: IDENTIFIER;

valueset: STRING | IDENTIFIER;

expression:
    term |
    expression '.' IDENTIFIER |
    expression '[' expression ']' |
    expression '(' (expression (',' expression)*)? ')' |
    expression 'is' 'not'? 'null' |
    expression ('is' | 'as') typeSpecifier |
    'convert' expression 'to' typeSpecifier |
    ('+' | '-') expression |
    ('not' | 'exists') expression |
    ('start' | 'end') 'of' expression |
    ('date' | 'time' | 'year' | 'month' | 'day' | 'hour' | 'minute' | 'second' | 'millisecond') 'of' expression |
    expression 'between' expression 'and' expression |
    ('years' | 'months' | 'days' | 'hours' | 'minutes' | 'seconds' | 'milliseconds') 'between' expression 'and' expression |
    expression '^' expression |
    expression ('*' | '/' | 'div' | 'mod') expression |
    expression ('+' | '-') expression |
    expression ('<=' | '<' | '>' | '>=') expression |
    expression intervalOperatorPhrase expression |
    expression ('=' | '<>') expression |
    expression 'and' expression |
    expression ('or' | 'xor' | 'in' | 'contains' | 'like') expression |
    'if' expression 'then' expression 'else' expression |
	'case' expression? caseExpressionItem+ 'else' expression 'end' |
	'coalesce' '(' expression (',' expression)+ ')' |
	'with' expression alias? 'return' expression |
	('collapse' | 'expand') expression |
	'foreach' IDENTIFIER 'in' query 'return' expression |
	'sort' query 'by' sortByItem (',' sortByItem)*;

caseExpressionItem:
    'when' expression 'then' expression;

sortByItem:
    qualifiedIdentifier ('asc' | 'desc');

qualifiedIdentifier:
    IDENTIFIER ('.' IDENTIFIER)*;

intervalOperatorPhrase:
    ('starts' | 'ends')? 'concurrent with' ('start' | 'end')? |
    'properly'? 'includes' ('start' | 'end')? |
    ('starts' | 'ends')? 'properly'? 'during' |
    ('starts' | 'ends')? quantityOffset? ('before' | 'after') ('start' | 'end')? |
    ('starts' | 'ends') 'within' quantityLiteral 'of' ('start' | 'end') |
    'meets' (quantityOffset? ('before' | 'after'))? |
    'overlaps' (quantityOffset? ('before' | 'after'))? |
    'starts' |
    'started by' |
    'ends' |
    'ended by';

quantityOffset:
    'within'? quantityLiteral;

term:
    literal |
    IDENTIFIER |
    intervalSelector |
    tupleSelector |
    listSelector |
    '(' query ')';

intervalSelector: // TODO: Consider this as an alternative syntax for intervals... (would need to be moved up to expression to make it work)
    //expression ( '..' | '*.' | '.*' | '**' ) expression;
    'interval' ('['|'(') expression ',' expression (']'|')');

tupleSelector:
    'tuple' '{' tupleElementSelector (',' tupleElementSelector)* '}';

tupleElementSelector:
    IDENTIFIER ':' expression;

listSelector:
    ('list' ('<' typeSpecifier '>')?)? '{' expression (',' expression)* '}';

literal:
    NULL |
    BOOLEAN |
    STRING |
    quantityLiteral;

quantityLiteral:
    QUANTITY unit?;

unit:
    'years' | // NOTE: Using plurals here because that's the most common case, we could add singulars, but that would allow "within 5 day"
    'months' |
    'weeks' |
    'days' |
    'hours' |
    'minutes' |
    'seconds' |
    'milliseconds' |
    'u'STRING; // UCUM syntax for units of measure

/*
 * Lexer Rules
 */

IDENTIFIER: [A-Za-z]+;

NULL: 'null';

BOOLEAN: 'true' | 'false';

QUANTITY: [0-9]+('.'[0-9]+)?;

STRING: '"' ( ~[\\"]  )* '"';

WS: (' ' | '\r' | '\t') -> channel(HIDDEN);

NEWLINE: ('\n') -> channel(HIDDEN);

COMMENT: '/*' .*? '*/' -> skip;

LINE_COMMENT:   '//' ~[\r\n]* -> skip;
