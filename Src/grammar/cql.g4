grammar cql;

/*
 * Parser Rules
 */

logic
    :
    libraryDefinition?
    usingDefinition*
	includeDefinition*
	parameterDefinition*
	valuesetDefinition*
	statement+
	;

/*
 * Definitions
 */

libraryDefinition
    : 'library' identifier ('version' versionSpecifier)?
    ;

usingDefinition
    : 'using' identifier ('version' versionSpecifier)?
    ;

includeDefinition
    : 'include' identifier ('version' versionSpecifier)? 'called' localIdentifier
    ;

localIdentifier
    : identifier
    ;

parameterDefinition
    : 'parameter' identifier (':' typeSpecifier)? ('default' expression)?
    ;

valuesetDefinition
    : 'valueset' identifier '=' valuesetId ('version' versionSpecifier)? codeSystemVersions?
    ;

codeSystemVersions
    : 'code systems' '(' codeSystemVersion (',' codeSystemVersion)* ')'
    ;

codeSystemVersion
    : codeSystemId 'version' versionSpecifier
    ;

codeSystemId
    : STRING
    ;

valuesetId
    : STRING
    ;

versionSpecifier
    : STRING
    ;

/*
 * Type Specifiers
 */

typeSpecifier
    : namedTypeSpecifier
    | listTypeSpecifier
    | intervalTypeSpecifier
    | tupleTypeSpecifier
    ;

namedTypeSpecifier
    : (modelIdentifier '.')? identifier
    ;

modelIdentifier
    : identifier
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
    : identifier ':' typeSpecifier
    ;

/*
 * Statements
 */

statement
    : expressionDefinition
    | contextDefinition
    | functionDefinition
    ;

expressionDefinition
    : 'define' identifier '=' expression
    ;

contextDefinition
    : 'context' identifier
    ;

functionDefinition
    : 'define' 'function' identifier '(' (operandDefinition (',' operandDefinition)*)? ')' functionBody
    ;

operandDefinition
    : identifier ':' typeSpecifier
    ;

functionBody
    : '{' returnStatement '}'
    ;

returnStatement
    : 'return' expression
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
    : identifier
    ;

queryInclusionClause
    : withClause
    | withoutClause
    ;

withClause
    : 'with' aliasedQuerySource 'such that' expression
    ;

withoutClause
    : 'without' aliasedQuerySource 'such that' expression
    ;

retrieve
    : '[' topic (':' (valuesetPathIdentifier 'in')? valueset)? ']'
    ;

topic
    : namedTypeSpecifier
    ;

valuesetPathIdentifier
    : identifier
    ;

valueset
    : qualifiedIdentifier
    ;

qualifier
    : identifier
    ;

query
    : sourceClause defineClause? queryInclusionClause* whereClause? returnClause? sortClause?
    ;

sourceClause
    : singleSourceClause
    | multipleSourceClause
    ;

singleSourceClause
    : aliasedQuerySource
    ;

multipleSourceClause
    : 'foreach' aliasedQuerySource (',' aliasedQuerySource)*
    ;

defineClause
    : 'define' defineClauseItem (',' defineClauseItem)*
    ;

defineClauseItem
    : identifier '=' expression
    ;

whereClause
    : 'where' expression
    ;

returnClause
    : 'return' ('all' | 'distinct')? expression
    ;

sortClause
    : 'sort' ( sortDirection | ('by' sortByItem (',' sortByItem)*) )
    ;

sortDirection
    : 'asc' | 'ascending'
    | 'desc' | 'descending'
    ;

sortByItem
    : expressionTerm sortDirection?
    ;

qualifiedIdentifier
    : (qualifier '.')* identifier
    ;

expression
    : expressionTerm                                                         # termExpression
    | retrieve                                                               # retrieveExpression
    | query                                                                  # queryExpression
    | expression 'is' 'not'? ('null' | 'true' | 'false')                     # booleanExpression
    | expression ('is' | 'as') typeSpecifier                                 # typeExpression
    | 'cast' expression 'as' typeSpecifier                                   # castExpression
    | 'not' expression                                                       # notExpression
    | 'exists' expression                                                    # existenceExpression
    | expression 'properly'? 'between' expressionTerm 'and' expressionTerm   # rangeExpression
    | pluralDateTimePrecision 'between' expressionTerm 'and' expressionTerm  # timeRangeExpression
    | expression ('<=' | '<' | '>' | '>=') expression                        # inequalityExpression
    | expression intervalOperatorPhrase expression                           # timingExpression
    | expression ('=' | '<>') expression                                     # equalityExpression
    | expression ('in' | 'contains') dateTimePrecisionSpecifier? expression  # membershipExpression
    | expression 'and' expression                                            # andExpression
    | expression ('or' | 'xor') expression                                   # orExpression
    | expression ('union' | 'intersect' | 'except') expression               # inFixSetExpression
    ;

dateTimePrecision
    : 'year' | 'month' | 'day' | 'hour' | 'minute' | 'second' | 'millisecond'
    ;

dateTimeComponent
    : dateTimePrecision
    | 'date'
    | 'time'
    | 'timezone'
    ;

pluralDateTimePrecision
    : 'years' | 'months' | 'days' | 'hours' | 'minutes' | 'seconds' | 'milliseconds'
    ;

expressionTerm
    : term                                                               # termExpressionTerm
    | expressionTerm '.' identifier                                      # accessorExpressionTerm
    | expressionTerm '[' expression ']'                                  # indexedExpressionTerm
    | expressionTerm '(' (expression (',' expression)*)? ')'             # methodExpressionTerm
    | 'convert' expression 'to' typeSpecifier                            # conversionExpressionTerm
    | ('+' | '-') expressionTerm                                         # polarityExpressionTerm
    | ('start' | 'end') 'of' expressionTerm                              # timeBoundaryExpressionTerm
    | dateTimeComponent 'from' expressionTerm                            # timeUnitExpressionTerm
    | 'duration' 'in' pluralDateTimePrecision 'of' expressionTerm        # durationExpressionTerm
    | 'width' 'of' expressionTerm                                        # widthExpressionTerm
    | 'successor' 'of' expressionTerm                                    # successorExpressionTerm
    | 'predecessor' 'of' expressionTerm                                  # predecessorExpressionTerm
    | 'singleton' 'from' expressionTerm                                  # elementExtractorExpressionTerm
    | expressionTerm '^' expressionTerm                                  # powerExpressionTerm
    | expressionTerm ('*' | '/' | 'div' | 'mod') expressionTerm          # multiplicationExpressionTerm
    | expressionTerm ('+' | '-') expressionTerm                          # additionExpressionTerm
    | 'if' expression 'then' expression 'else' expression                # ifThenElseExpressionTerm
    | 'case' expression? caseExpressionItem+ 'else' expression 'end'     # caseExpressionTerm
    | 'coalesce' '(' expression (',' expression)+ ')'                    # coalesceExpressionTerm
    | ('distinct' | 'collapse' | 'expand') expression                    # aggregateExpressionTerm
    ;

caseExpressionItem
    : 'when' expression 'then' expression
    ;

dateTimePrecisionSpecifier
    : dateTimePrecision 'of'
    ;

relativeQualifier
    : 'or before'
    | 'or after'
    ;

offsetRelativeQualifier
    : 'or more'
    | 'or less'
    ;

quantityOffset
    : quantityLiteral offsetRelativeQualifier?
    ;

intervalOperatorPhrase
    : ('starts' | 'ends')? 'same' dateTimePrecision? (relativeQualifier | 'as') ('start' | 'end')?   #concurrentWithIntervalOperatorPhrase
    | 'properly'? 'includes' dateTimePrecisionSpecifier? ('start' | 'end')?                          #includesIntervalOperatorPhrase
    | ('starts' | 'ends')? 'properly'? ('during' | 'included in') dateTimePrecisionSpecifier?        #includedInIntervalOperatorPhrase
    | ('starts' | 'ends')? quantityOffset? ('before' | 'after') ('start' | 'end')?                   #beforeOrAfterIntervalOperatorPhrase
    | ('starts' | 'ends')? 'properly'? 'within' quantityLiteral 'of' ('start' | 'end')?              #withinIntervalOperatorPhrase
    | 'meets' ('before' | 'after')? dateTimePrecisionSpecifier?                                      #meetsIntervalOperatorPhrase
    | 'overlaps' ('before' | 'after')? dateTimePrecisionSpecifier?                                   #overlapsIntervalOperatorPhrase
    | 'starts' dateTimePrecisionSpecifier?                                                           #startsIntervalOperatorPhrase
    | 'ends' dateTimePrecisionSpecifier?                                                             #endsIntervalOperatorPhrase
    ;

term
    : identifier            #identifierTerm
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
    : identifier ':' expression
    ;

listSelector
    : ('list' ('<' typeSpecifier '>')?)? '{' expression? (',' expression)* '}'
    ;

literal
    : nullLiteral
    | booleanLiteral
    | stringLiteral
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

quantityLiteral
    : QUANTITY unit?
    ;

unit
    : dateTimePrecision
    | pluralDateTimePrecision
    | STRING // UCUM syntax for units of measure
    ;

identifier
    : IDENTIFIER | QUOTEDIDENTIFIER
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

QUOTEDIDENTIFIER
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
