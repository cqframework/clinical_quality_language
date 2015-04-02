grammar cql;

/*
 * Parser Rules
 */

logic
    :
    libraryDefinition?
    usingDefinition*
	includeDefinition*
	codesystemDefinition*
	valuesetDefinition*
	parameterDefinition*
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

accessModifier
    : 'public'
    | 'private'
    ;

parameterDefinition
    : accessModifier? 'parameter' identifier (typeSpecifier)? ('default' expression)?
    ;

codesystemDefinition
    : accessModifier? 'codesystem' identifier ':' codesystemId ('version' versionSpecifier)?
    ;

valuesetDefinition
    : accessModifier? 'valueset' identifier ':' valuesetId ('version' versionSpecifier)? codesystems?
    ;

codesystems
    : 'codesystems' '(' codesystemIdentifier (',' codesystemIdentifier)* ')'
    ;

codesystemIdentifier
    : (libraryIdentifier '.')? identifier
    ;

libraryIdentifier
    : identifier
    ;

codesystemId
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
    : 'List' '<' typeSpecifier '>'
    ;

intervalTypeSpecifier
    : 'Interval' '<' typeSpecifier '>'
    ;

tupleTypeSpecifier
    : 'Tuple' '{' tupleElementDefinition (',' tupleElementDefinition)* '}'
    ;

tupleElementDefinition
    : identifier typeSpecifier
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
    : 'define' accessModifier? identifier ':' expression
    ;

contextDefinition
    : 'context' identifier
    ;

functionDefinition
    : 'define' accessModifier? 'function' identifier '(' (operandDefinition (',' operandDefinition)*)? ')' functionBody
    ;

operandDefinition
    : identifier typeSpecifier
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
    : '[' namedTypeSpecifier (':' (valuesetPathIdentifier 'in')? valueset)? ']'
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
    : 'from' aliasedQuerySource (',' aliasedQuerySource)*
    ;

defineClause
    : 'define' defineClauseItem (',' defineClauseItem)*
    ;

defineClauseItem
    : identifier ':' expression
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
    : expressionTerm                                                                                # termExpression
    | retrieve                                                                                      # retrieveExpression
    | query                                                                                         # queryExpression
    | expression 'is' 'not'? ('null' | 'true' | 'false')                                            # booleanExpression
    | expression ('is' | 'as') typeSpecifier                                                        # typeExpression
    | 'cast' expression 'as' typeSpecifier                                                          # castExpression
    | 'not' expression                                                                              # notExpression
    | 'exists' expression                                                                           # existenceExpression
    | expression 'properly'? 'between' expressionTerm 'and' expressionTerm                          # betweenExpression
    | pluralDateTimePrecision 'between' expressionTerm 'and' expressionTerm                         # durationBetweenExpression
    | 'difference' 'in' pluralDateTimePrecision 'between' expressionTerm 'and' expressionTerm       # differenceBetweenExpression
    | expression ('<=' | '<' | '>' | '>=') expression                                               # inequalityExpression
    | expression intervalOperatorPhrase expression                                                  # timingExpression
    | expression ('=' | '<>' | 'matches' ) expression                                               # equalityExpression
    | expression ('in' | 'contains') dateTimePrecisionSpecifier? expression                         # membershipExpression
    | expression 'and' expression                                                                   # andExpression
    | expression ('or' | 'xor') expression                                                          # orExpression
    | expression ('union' | 'intersect' | 'except') expression                                      # inFixSetExpression
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
    | (qualifier '.')? identifier '(' (expression (',' expression)*)? ')'# invocationExpressionTerm
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
    : ('starts' | 'ends' | 'occurs')? 'same' dateTimePrecision? (relativeQualifier | 'as') ('start' | 'end')?               #concurrentWithIntervalOperatorPhrase
    | 'properly'? 'includes' dateTimePrecisionSpecifier? ('start' | 'end')?                                                 #includesIntervalOperatorPhrase
    | ('starts' | 'ends' | 'occurs')? 'properly'? ('during' | 'included in') dateTimePrecisionSpecifier?                    #includedInIntervalOperatorPhrase
    | ('starts' | 'ends' | 'occurs')? quantityOffset? ('before' | 'after') dateTimePrecisionSpecifier? ('start' | 'end')?   #beforeOrAfterIntervalOperatorPhrase
    | ('starts' | 'ends' | 'occurs')? 'properly'? 'within' quantityLiteral 'of' ('start' | 'end')?                          #withinIntervalOperatorPhrase
    | 'meets' ('before' | 'after')? dateTimePrecisionSpecifier?                                                             #meetsIntervalOperatorPhrase
    | 'overlaps' ('before' | 'after')? dateTimePrecisionSpecifier?                                                          #overlapsIntervalOperatorPhrase
    | 'starts' dateTimePrecisionSpecifier?                                                                                  #startsIntervalOperatorPhrase
    | 'ends' dateTimePrecisionSpecifier?                                                                                    #endsIntervalOperatorPhrase
    ;

term
    : identifier            #identifierTerm
    | literal               #literalTerm
    | intervalSelector      #intervalSelectorTerm
    | tupleSelector         #tupleSelectorTerm
    | instanceSelector      #instanceSelectorTerm
    | listSelector          #listSelectorTerm
    | codeSelector          #codeSelectorTerm
    | conceptSelector       #conceptSelectorTerm
    | '(' expression ')'    #parenthesizedTerm
    ;

intervalSelector
    : // TODO: Consider this as an alternative syntax for intervals... (would need to be moved up to expression to make it work)
    //expression ( '..' | '*.' | '.*' | '**' ) expression;
    'Interval' ('['|'(') expression ',' expression (']'|')')
    ;

tupleSelector
    : 'Tuple'? '{' (':' | (tupleElementSelector (',' tupleElementSelector)*)) '}'
    ;

tupleElementSelector
    : identifier ':' expression
    ;

instanceSelector
    : namedTypeSpecifier '{' (':' | (instanceElementSelector (',' instanceElementSelector)*)) '}'
    ;

instanceElementSelector
    : identifier ':' expression
    ;

listSelector
    : ('List' ('<' typeSpecifier '>')?)? '{' (expression (',' expression)*)? '}'
    ;

displayClause
    : 'display' stringLiteral
    ;

codeSelector
    : 'Code' stringLiteral 'from' codesystemIdentifier displayClause?
    ;

conceptSelector
    : 'Concept' '{' codeSelector (',' codeSelector)* '}' displayClause?
    ;

literal
    : nullLiteral
    | booleanLiteral
    | stringLiteral
    | dateTimeLiteral
    | timeLiteral
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

dateTimeLiteral
    : DATETIME
    ;

timeLiteral
    : TIME
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
    // Include here any keyword that should not be a reserved word
    | 'display'
    | 'version'
    | 'Code'
    | 'Concept'
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

DATETIME
    : '@'
        [0-9][0-9][0-9][0-9] // year
        (
            '-'[0-9][0-9] // month
            (
                '-'[0-9][0-9] // day
                (
                    'T'
                        [0-9][0-9] (':'[0-9][0-9] (':'[0-9][0-9] ('.'[0-9]+)?)?)?
                        (('+' | '-') [0-9][0-9]':'[0-9][0-9])? // timezone
                )?
             )?
         )?
         'Z'? // UTC specifier
    ;

TIME
    : '@'
        'T'
            [0-9][0-9] (':'[0-9][0-9] (':'[0-9][0-9] ('.'[0-9]+)?)?)?
            ('Z' | (('+' | '-') [0-9][0-9]':'[0-9][0-9]))? // timezone
    ;

// These versions limit each field to only potentially valid combinations of digits.
// However, it still doesn't suffice to provide actual valid date enforcement, and
// when it doesn't match the rule, the parser will turn it into a subtraction expression in most cases,
// which is never what we want, so I think we should go with the more lenient (and simpler) expressions
// above, and rely on the translator to actually perform value validation.
//DATETIME
//    : '@'
//        [0-9][0-9][0-9][0-9] // year
//        (
//            '-'(('0'[1-9]) | ('1'[0-2])) // month
//            (
//                '-'(('0'[1-9]) | ([1-2][0-9]) | ('3'[0-1])) // day
//                (
//                    'T'
//                        (
//                            ((([0-1][0-9])|('2'[0-3])) (':'([0-5][0-9]) (':'([0-5][0-9]) ('.'[0-9]+)?)?)?
//                                | ('24:00:00'('.''0'+)?))
//                        )
//                        (('+' | '-') (((([0-1][0-9]) | ('2'[0-3]))':'([0-5][0-9])) | '14:00'))? // timezone
//                )?
//             )?
//         )?
//         'Z'? // UTC specifier
//    ;

//TIME
//    : '@'
//        'T'
//            ((([0-1][0-9])|('2'[0-3])) (':'([0-5][0-9]) (':'([0-5][0-9]) ('.'[0-9]+)?)?)?
//            | ('24:00:00'('.''0'+)?))
//        ('Z' | (('+' | '-') (((([0-1][0-9]) | ('2'[0-3]))':'([0-5][0-9])) | '14:00')))? // timezone
//    ;

QUOTEDIDENTIFIER
    : '"' ( ~[\\"] | '""' )* '"'
    ;

STRING
    : ('\'') ( ~[\\'] | '\'\'' )* ('\'')
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

