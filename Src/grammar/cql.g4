grammar cql;

/*
 * Parser Rules
 */

logic:
    libraryDefinition
	usingDefinition*
	contextDefinition?
	includeDefinition*
	(parameterDefinition | valuesetDefinition)*
	statement+;

/*
 * Definitions
 */

libraryDefinition: 'library' IDENTIFIER ('version' STRING)?;

usingDefinition: 'using' IDENTIFIER ('version' STRING)?;

contextDefinition: 'context' IDENTIFIER;

includeDefinition: 'include' IDENTIFIER ('version' STRING)?;

parameterDefinition: 'parameter' IDENTIFIER (':' typeSpecifier)? ('default' expression)?;

valuesetDefinition: 'valueset' VALUESET '=' expression;

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
    retrieve | optionallyQualifiedIdentifier | '(' expression ')';

aliasedQuerySource:
    querySource alias;

alias: IDENTIFIER;

queryInclusionClause:
    'with' aliasedQuerySource 'where' expression |
    'combine' aliasedQuerySource 'where' expression
;

retrieve: existenceModifier? '[' topic (',' modality)? (':' (IDENTIFIER 'in')? valueset)? (',' IDENTIFIER? 'during' expression)? ']';

existenceModifier: 'no' | 'unknown';

topic: optionallyQualifiedIdentifier;

modality: IDENTIFIER;

valueset: (IDENTIFIER '.')? (VALUESET | IDENTIFIER);

expression
    : expressionTerm                                                     # termExpression
    | retrieve                                                           # retrieveExpression
    | aliasedQuerySource queryInclusionClause* ('where' expression)? ('return' expression)? ('sort' 'by' sortByItem (',' sortByItem)*)?
                                                                         # queryExpression
    | expression 'is' 'not'? ( 'null' | 'true' | 'false' )               # booleanExpression
    | expression ('is' | 'as') typeSpecifier                             # typeExpression
    | ('not' | 'exists') expression                                      # existenceExpression
    | expression 'between' expressionTerm 'and' expressionTerm           # rangeExpression
    | ('years' | 'months' | 'days' | 'hours' | 'minutes' | 'seconds' | 'milliseconds') 'between' expressionTerm 'and' expressionTerm
                                                                         # timeRangeExpression
    | expression ('<=' | '<' | '>' | '>=') expression                    # inequalityExpression
    | expression intervalOperatorPhrase expression                       # timingExpression
    | expression ('=' | '<>') expression                                 # equalityExpression
    | expression 'and' expression                                        # andExpression
    | expression ('or' | 'xor') expression                               # orExpression
    | expression ('in' | 'contains' | 'like') expression                 # membershipExpression
    | expression 'like' expression                                       # likeExpression
    ;

expressionTerm
    : term                                                               # termExpressionTerm
    | expressionTerm '.' (IDENTIFIER | VALUESET)                         # accessorExpressionTerm
    | expressionTerm '[' expression ']'                                  # indexedExpressionTerm
    | expressionTerm '(' (expression (',' expression)*)? ')'             # methodExpressionTerm
    | 'convert' expression 'to' typeSpecifier                            # conversionExpressionTerm
    | ('+' | '-') expressionTerm                                         # polarityExpressionTerm
    | ('start' | 'end') 'of' expressionTerm                              # timeBoundaryExpressionTerm
    | ('date' | 'time' | 'year' | 'month' | 'day' | 'hour' | 'minute' | 'second' | 'millisecond') 'of' expressionTerm
                                                                         # timeUnitExpressionTerm
    | 'duration' 'in' ('years' | 'months' | 'days' | 'hours' | 'minutes' | 'seconds' | 'milliseconds') 'of' expressionTerm
                                                                         # durationExpressionTerm
    | expressionTerm '^' expressionTerm                                  # powerExpressionTerm
    | expressionTerm ('*' | '/' | 'div' | 'mod') expressionTerm          # multiplicationExpressionTerm
    | expressionTerm ('+' | '-') expressionTerm                          # additionExpressionTerm
    | 'if' expression 'then' expression 'else' expression                # ifThenElseExpressionTerm
    | 'case' expression? caseExpressionItem+ 'else' expression 'end'     # caseExpressionTerm
    | 'coalesce' '(' expression (',' expression)+ ')'                    # coalesceExpressionTerm
    | ('distinct' | 'collapse' | 'expand') expression                    # aggregateExpressionTerm
    | ('union' | 'intersect') '(' expression (',' expression)+ ')'       # prefixSetExpressionTerm
    | expressionTerm ('union' | 'intersect' | 'except') expressionTerm   # inFixSetExpressionTerm
    ;

caseExpressionItem:
    'when' expression 'then' expression;

sortByItem:
    qualifiedIdentifier ('asc' | 'desc')?;

qualifiedIdentifier:
    IDENTIFIER '.' IDENTIFIER;

optionallyQualifiedIdentifier:
    (IDENTIFIER '.')? IDENTIFIER;

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
    valuesetLiteral |
    quantityLiteral;

nullLiteral:
    'null';

booleanLiteral:
    'true' | 'false';

stringLiteral:
    STRING;

valuesetLiteral:
    VALUESET;

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

IDENTIFIER: ([A-Za-z] | '_')([A-Za-z0-9] | '_')*;

QUANTITY: [0-9]+('.'[0-9]+)?;

VALUESET: '"' ( ~[\\"] )* '"';

STRING: ('\'') ( ~[\\'] )* ('\'');

WS: (' ' | '\r' | '\t') -> channel(HIDDEN);

NEWLINE: ('\n') -> channel(HIDDEN);

COMMENT: '/*' .*? '*/' -> skip;

LINE_COMMENT:   '//' ~[\r\n]* -> skip;
