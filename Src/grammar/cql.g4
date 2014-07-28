grammar cql;

/*
 * Parser Rules
 */

logic
    :
    libraryDefinition?
    usingDefinition*
	includeDefinition*
	(parameterDefinition | valuesetDefinition)*
	statement+
	;

/*
 * Definitions
 */

libraryDefinition
    : 'library' IDENTIFIER ('version' STRING)?
    ;

usingDefinition
    : 'using' IDENTIFIER ('version' STRING)?
    ;

includeDefinition
    : 'include' IDENTIFIER ('version' STRING)? 'as' IDENTIFIER
    ;

parameterDefinition
    : 'parameter' IDENTIFIER (':' typeSpecifier)? ('default' expression)?
    ;

valuesetDefinition
    : 'valueset' VALUESET '=' 'ValueSet' '(' VALUESET ')'   #valuesetDefinitionByConstructor
    | 'valueset' VALUESET '=' expression                    #valuesetDefinitionByExpression
    ;

/*
 * Type Specifiers
 */

typeSpecifier
    : atomicTypeSpecifier
    | listTypeSpecifier
    | intervalTypeSpecifier
    | tupleTypeSpecifier
    ;

atomicTypeSpecifier
    : IDENTIFIER // TODO: specify atomic type names as part of the grammar?
    ;

listTypeSpecifier
    : 'list' '<' typeSpecifier '>'
    ;

intervalTypeSpecifier
    : 'interval' '<' typeSpecifier '>'
    ;

tupleTypeSpecifier
    : 'tuple' '{' tupleElementDefinition (',' tupleElementDefinition)* '}'
    ;

tupleElementDefinition
    : IDENTIFIER ':' typeSpecifier
    ;

/*
 * Statements
 */

statement
    : letStatement
    | contextDefinition
    | functionDefinition
    | retrieveDefinition
    ;

letStatement
    : 'let' IDENTIFIER '=' expression
    ;

contextDefinition
    : 'context' IDENTIFIER
    ;

functionDefinition
    : 'define' 'function' IDENTIFIER '(' (operandDefinition (',' operandDefinition)*)? ')' functionBody
    ;

operandDefinition
    : IDENTIFIER ':' typeSpecifier
    ;

functionBody
    : '{' returnStatement '}'
    ;

returnStatement
    : 'return' expression
    ;

retrieveDefinition
    : 'define' 'retrieve' existenceModifier? '[' topic (',' modality)? (':' valuesetPathIdentifier 'in' valuesetIdentifier)? (',' duringPathIdentifier 'during' duringIdentifier)? ']' functionBody
    ;

valuesetPathIdentifier
    : IDENTIFIER
    ;

valuesetIdentifier
    : IDENTIFIER
    ;

duringPathIdentifier
    : IDENTIFIER
    ;

duringIdentifier
    : IDENTIFIER
    ;

/*
 * Expressions
 */

querySource
    : retrieve
    | qualifiedIdentifier
    | '(' expression ')'
    ;

aliasedQuerySource
    : querySource alias
    ;

alias
    : IDENTIFIER
    ;

queryInclusionClause
    : 'with' aliasedQuerySource 'where' expression
    | 'without' aliasedQuerySource 'where' expression
    //| 'combine' aliasedQuerySource 'where' expression // TODO: Determine whether combine should be allowed
    ;

retrieve
    : existenceModifier? '[' topic (',' modality)? (':' (valuesetPathIdentifier 'in')? valueset)? (',' duringPathIdentifier? 'during' expression)? ']'
    ;

existenceModifier
    : 'no'
    | 'unknown'
    ;

topic
    : qualifiedIdentifier
    ;

modality
    : IDENTIFIER
    ;

valueset
    : (qualifier '.')? (VALUESET | IDENTIFIER)
    ;

qualifier
    : IDENTIFIER
    ;

query
    : aliasedQuerySource queryInclusionClause* whereClause? returnClause? sortClause?
    ;

whereClause
    : 'where' expression
    ;

returnClause
    : 'return' expression
    ;

sortClause
    : 'sort' ( sortDirection | ('by' sortByItem (',' sortByItem)*) )
    ;

sortDirection // TODO: use full words instead of abbreviations?
    : 'asc'
    | 'desc'
    ;

sortByItem
    : qualifiedIdentifier sortDirection?
    ;

qualifiedIdentifier
    : (qualifier '.')? IDENTIFIER
    ;

expression
    : expressionTerm                                                     # termExpression
    | retrieve                                                           # retrieveExpression
    | query                                                              # queryExpression
    | expression 'is' 'not'? ( 'null' | 'true' | 'false' )               # booleanExpression
    | expression ('is' | 'as') typeSpecifier                             # typeExpression
    | ('not' | 'exists') expression                                      # existenceExpression
    | expression 'properly'? 'between' expressionTerm 'and' expressionTerm
                                                                         # rangeExpression
    | ('years' | 'months' | 'days' | 'hours' | 'minutes' | 'seconds' | 'milliseconds') 'between' expressionTerm 'and' expressionTerm
                                                                         # timeRangeExpression
    | expression ('<=' | '<' | '>' | '>=') expression                    # inequalityExpression
    | expression intervalOperatorPhrase expression                       # timingExpression
    | expression ('=' | '<>') expression                                 # equalityExpression
    | expression ('in' | 'contains' | 'like') expression                 # membershipExpression
    | expression 'and' expression                                        # andExpression
    | expression ('or' | 'xor') expression                               # orExpression
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
    | 'width' 'of' expressionTerm                                        # widthExpressionTerm
    | 'successor' 'of' expressionTerm                                    # successorExpressionTerm
    | 'predecessor' 'of' expressionTerm                                  # predecessorExpressionTerm
    | expressionTerm '^' expressionTerm                                  # powerExpressionTerm
    | expressionTerm ('*' | '/' | 'div' | 'mod') expressionTerm          # multiplicationExpressionTerm
    | expressionTerm ('+' | '-') expressionTerm                          # additionExpressionTerm
    | 'if' expression 'then' expression 'else' expression                # ifThenElseExpressionTerm
    | 'case' expression? caseExpressionItem+ 'else' expression 'end'     # caseExpressionTerm
    | 'coalesce' '(' expression (',' expression)+ ')'                    # coalesceExpressionTerm
    | ('distinct' | 'collapse' | 'expand') expression                    # aggregateExpressionTerm
    | expressionTerm ('union' | 'intersect' | 'except') expressionTerm   # inFixSetExpressionTerm
    ;

caseExpressionItem
    : 'when' expression 'then' expression
    ;

intervalOperatorPhrase
    : ('starts' | 'ends')? 'concurrent with' ('start' | 'end')?                             #concurrentWithIntervalOperatorPhrase
    | 'properly'? 'includes' ('start' | 'end')?                                             #includesIntervalOperatorPhrase
    | ('starts' | 'ends')? 'properly'? ('during' | 'included in')                           #includedInIntervalOperatorPhrase
    | ('starts' | 'ends')? quantityOffset? ('before' | 'after') ('start' | 'end')?          #beforeOrAfterIntervalOperatorPhrase
    | ('starts' | 'ends')? 'properly'? 'within' quantityLiteral 'of' ('start' | 'end')?     #withinIntervalOperatorPhrase
    | 'meets' (quantityOffset? ('before' | 'after'))?                                       #meetsIntervalOperatorPhrase
    | 'overlaps' (quantityOffset? ('before' | 'after'))?                                    #overlapsIntervalOperatorPhrase
    | 'starts'                                                                              #startsIntervalOperatorPhrase
    | 'started by'                                                                          #startedByIntervalOperatorPhrase
    | 'ends'                                                                                #endsIntervalOperatorPhrase
    | 'ended by'                                                                            #endedByIntervalOperatorPhrase
    ;

quantityOffset
    : 'within'? quantityLiteral
    ;

term
    : IDENTIFIER            #identifierTerm
    | literal               #literalTerm
    | intervalSelector      #intervalSelectorTerm
    | tupleSelector         #tupleSelectorTerm
    | listSelector          #listSelectorTerm
    | '(' expression ')'    #parenthesizedTerm
    ;

intervalSelector
    : // TODO: Consider this as an alternative syntax for intervals... (would need to be moved up to expression to make it work)
    //expression ( '..' | '*.' | '.*' | '**' ) expression;
    'interval' ('['|'(') expression ',' expression (']'|')')
    ;

tupleSelector
    : 'tuple'? '{' (':' | (tupleElementSelector (',' tupleElementSelector)*)) '}'
    ;

tupleElementSelector
    : IDENTIFIER ':' expression
    ;

listSelector
    : ('list' ('<' typeSpecifier '>')?)? '{' expression? (',' expression)* '}'
    ;

literal
    : nullLiteral
    | booleanLiteral
    | stringLiteral
    | valuesetLiteral
    | quantityLiteral
    ;

nullLiteral
    : 'null'
    ;

booleanLiteral
    : 'true'
    | 'false'
    ;

stringLiteral
    : STRING
    ;

valuesetLiteral
    : VALUESET
    ;

quantityLiteral
    : QUANTITY unit?
    ;

unit // NOTE: Using plurals here because that's the most common case, we could add singulars, but that would allow "within 5 day"
    : 'years'
    | 'months'
    | 'weeks'
    | 'days'
    | 'hours'
    | 'minutes'
    | 'seconds'
    | 'milliseconds'
    | 'u'STRING // UCUM syntax for units of measure
    ;

/*
 * Lexer Rules
 */

IDENTIFIER
    : ([A-Za-z] | '_')([A-Za-z0-9] | '_')*
    ;

QUANTITY
    : [0-9]+('.'[0-9]+)?
    ;

VALUESET
    : '"' ( ~[\\"] )* '"'
    ;

STRING
    : ('\'') ( ~[\\'] )* ('\'')
    ;

WS
    : (' ' | '\r' | '\t') -> channel(HIDDEN)
    ;

NEWLINE
    : ('\n') -> channel(HIDDEN)
    ;

COMMENT
    : '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;
