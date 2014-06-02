grammar cql;

/*
 * Parser Rules
 */

logic:
	usingDefinition*
	contextDefinition?
	includeDefinition*
	parameterDefinition*
	conceptDefinition*
	statement+;

/*
 * Definitions
 */

usingDefinition: 'using' IDENTIFIER ('version' STRING)?;

contextDefinition: 'context' IDENTIFIER;

includeDefinition: 'include' IDENTIFIER ('version' STRING)?;

parameterDefinition: 'parameter' IDENTIFIER (':' typeSpecifier)? ('default' expression)?;

conceptDefinition: 'concept' STRING '=' expression;

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

letStatement: 'let' IDENTIFIER '=' setExpression;

operatorDefinition:
    'define' 'operator' IDENTIFIER '(' (operandDefinition (',' operandDefinition)*)? ')' operatorBody;

operandDefinition: IDENTIFIER ':' typeSpecifier;

operatorBody:
    '{' letStatement* returnStatement '}';

returnStatement:
    'return' expression;

retrieveDefinition:
    'define' 'retrieve' existenceModifier? '[' topicType (',' activityType)? (':' concept)? ']' operatorBody;

/*
 * Expressions
 */

//setExpression:
//    expression |
//    retrieve |
//    setExpression 'with' setExpression |
//    setExpression 'where' expression |
//    'combine' '(' setExpression (',' setExpression)+ ')' |
//    'union' '(' setExpression (',' setExpression)+ ')' |
//    'intersect' '(' setExpression (',' setExpression)+ ')' |
//    setExpression 'except' setExpression;

setExpression:
    querySource queryInclusionClause* ('where' expression)? |
    'union' '(' setExpression (',' setExpression)+ ')' |
    'intersect' '(' setExpression (',' setExpression)+ ')' |
    setExpression 'except' setExpression;

querySource:
    (retrieve | IDENTIFIER) alias?;

alias: IDENTIFIER;

queryInclusionClause:
    'with' querySource 'where' expression |
    'combine' querySource 'where' expression
;

retrieve: existenceModifier? '[' topicType (',' activityType)? (':' concept)? ']';

existenceModifier: 'no' | 'unknown';

topicType: IDENTIFIER;

activityType: IDENTIFIER;

concept: STRING;

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
    expression '^' expression |
    expression ('*' | '/' | 'div' | 'mod') expression |
    expression ('+' | '-') expression |
    expression ('<=' | '<' | '>' | '>=') expression |
    expression intervalOperatorPhrase expression |
    expression ('=' | '<>') expression |
    expression 'and' expression |
    expression ('or' | 'in' | 'like') expression |
    'if' expression 'then' expression 'else' expression |
	'case' expression? caseExpressionItem+ 'else' expression 'end' |
	'coalesce' '(' expression (',' expression)+ ')';

caseExpressionItem:
    'when' expression 'then' expression;

intervalOperatorPhrase:
    ('starts' | 'ends')? 'concurrent with' ('start' | 'end')? |
    'properly'? 'includes' ('start' | 'end')? |
    ('starts' | 'ends')? 'properly'? 'during' |
    ('starts' | 'ends')? quantityOffset? ('before' | 'after') ('start' | 'end')? | // TODO: within 3 days of start...
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
    '('setExpression')';

intervalSelector: // TODO: Consider this as an alternative syntax for intervals... (would need to be moved up to expression to make it work)
    //expression ( '..' | '*.' | '.*' | '**' ) expression;
    'interval' ('['|'(') expression ',' expression (']'|')');

tupleSelector:
    'tuple' '{' tupleElementSelector (',' tupleElementSelector)* '}';

tupleElementSelector:
    IDENTIFIER ':' expression;

listSelector:
    'list'('<' typeSpecifier '>')? '{' expression (',' expression)* '}';

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
