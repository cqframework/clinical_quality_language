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
    functionDefinition |
    retrieveDefinition;

letStatement: 'let' IDENTIFIER '=' expression;

functionDefinition:
    'define' 'function' IDENTIFIER '(' (operandDefinition (',' operandDefinition)*)? ')' functionBody;

operandDefinition: IDENTIFIER ':' typeSpecifier;

functionBody:
    '{' returnStatement '}';

returnStatement:
    'return' expression;

retrieveDefinition:
    'define' 'retrieve' existenceModifier? '[' topic (',' modality)? (':' valuesetPathIdentifier 'in' valuesetIdentifier)? (',' duringPathIdentifier 'during' duringIdentifier)? ']' functionBody;

valuesetPathIdentifier: IDENTIFIER;

valuesetIdentifier: IDENTIFIER;

duringPathIdentifier: IDENTIFIER;

duringIdentifier: IDENTIFIER;

/*
 * Expressions
 */

querySource:
    retrieve | IDENTIFIER | '(' expression ')';

aliasedQuerySource:
    querySource alias;

alias: IDENTIFIER;

queryInclusionClause:
    'with' aliasedQuerySource 'where' expression |
    'combine' aliasedQuerySource 'where' expression
;

retrieve: existenceModifier? '[' topic (',' modality)? (':' (IDENTIFIER 'in')? valueset)? (',' IDENTIFIER? 'during' expression)? ']';

existenceModifier: 'no' | 'unknown';

topic: IDENTIFIER;

modality: IDENTIFIER;

valueset: STRING | IDENTIFIER;

expression:
    expressionTerm |
    retrieve |
    aliasedQuerySource queryInclusionClause* ('where' expression)? ('return' expression)? ('sort' 'by' sortByItem (',' sortByItem)*)? |
    expression 'is' 'not'? ( 'null' | 'true' | 'false' ) |
    expression ('is' | 'as') typeSpecifier |
    ('not' | 'exists') expression |
    expression 'between' expressionTerm 'and' expressionTerm |
    ('years' | 'months' | 'days' | 'hours' | 'minutes' | 'seconds' | 'milliseconds') 'between' expressionTerm 'and' expressionTerm |
    expression ('<=' | '<' | '>' | '>=') expression |
    expression intervalOperatorPhrase expression |
    expression ('=' | '<>') expression |
    expression 'and' expression |
    expression ('or' | 'xor' | 'in' | 'contains' | 'like') expression;

expressionTerm:
    term |
    expressionTerm '.' IDENTIFIER |
    expressionTerm '[' expression ']' |
    expressionTerm '(' (expression (',' expression)*)? ')' |
    expressionTerm '(' IDENTIFIER 'from' expression ')' |
    'convert' expression 'to' typeSpecifier |
    ('+' | '-') expressionTerm |
    ('start' | 'end') 'of' expressionTerm |
    ('date' | 'time' | 'year' | 'month' | 'day' | 'hour' | 'minute' | 'second' | 'millisecond') 'of' expressionTerm |
    'duration' 'in' ('years' | 'months' | 'days' | 'hours' | 'minutes' | 'seconds' | 'milliseconds') 'of' expressionTerm |
    expressionTerm '^' expressionTerm |
    expressionTerm ('*' | '/' | 'div' | 'mod') expressionTerm |
    expressionTerm ('+' | '-') expressionTerm |
    'if' expression 'then' expression 'else' expression |
	'case' expression? caseExpressionItem+ 'else' expression 'end' |
	'coalesce' '(' expression (',' expression)+ ')' |
	'with' expression alias? 'return' expression |
	('distinct' | 'collapse' | 'expand') expression |
    ('union' | 'intersect') '(' expression (',' expression)+ ')' |
    expressionTerm ('union' | 'intersect' | 'except') expressionTerm |
	'foreach' IDENTIFIER 'in' expression 'return' expression;

caseExpressionItem:
    'when' expression 'then' expression;

sortByItem:
    qualifiedIdentifier ('asc' | 'desc')?;

qualifiedIdentifier:
    IDENTIFIER '.' IDENTIFIER;

intervalOperatorPhrase:
    ('starts' | 'ends')? 'concurrent with' ('start' | 'end')? |
    'properly'? 'includes' ('start' | 'end')? |
    ('starts' | 'ends')? 'properly'? 'during' |
    ('starts' | 'ends')? quantityOffset? ('before' | 'after') ('start' | 'end')? |
    ('starts' | 'ends')? 'within' quantityLiteral 'of' ('start' | 'end')? |
    'meets' (quantityOffset? ('before' | 'after'))? |
    'overlaps' (quantityOffset? ('before' | 'after'))? |
    'starts' |
    'started by' |
    'ends' |
    'ended by';

quantityOffset:
    'within'? quantityLiteral;

term:
    IDENTIFIER |
    literal |
    intervalSelector |
    tupleSelector |
    listSelector |
    '(' expression ')';

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
    nullLiteral |
    booleanLiteral |
    stringLiteral |
    quantityLiteral;

nullLiteral:
    'null';

booleanLiteral:
    'true' | 'false';

stringLiteral:
    STRING;

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

QUANTITY: [0-9]+('.'[0-9]+)?;

STRING: '"' ( ~[\\"]  )* '"';

WS: (' ' | '\r' | '\t') -> channel(HIDDEN);

NEWLINE: ('\n') -> channel(HIDDEN);

COMMENT: '/*' .*? '*/' -> skip;

LINE_COMMENT:   '//' ~[\r\n]* -> skip;
