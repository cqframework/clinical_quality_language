grammar fluentpath;

// Grammar rules

//prog: line (line)*;
//line: ID ( '(' expr ')') ':' expr '\r'? '\n';

// Don't see the value of unary +, but would be confusing for CQL users not to have it
// So, we'll stick to != ?  Will CQL align to that?
// BR: Is there any reason not to support <>? I'm concerned that it's such a common operator, it will be significant backwards compatibility issue
expression
        : term                                                      #termExpression
        // BR: Removed the + here because it is grammatically equivalent but makes it easier to deal with in the tree
        | expression '.' invocation                                 #invocationExpression
        | expression '[' expression ']'                             #indexerExpression
        | ('+' | '-') expression                                    #polarityExpression
        | expression '^' expression                                 #powerExpression
        | expression ('*' | '/' | 'div' | 'mod') expression         #multiplicativeExpression
        | expression ('+' | '-' ) expression                        #additiveExpression
        | expression '|' expression                                 #unionExpression
        // | expression 'between' expression 'and' expression         #betweenExpression
        | expression ('<=' | '<' | '>' | '>=') expression           #inequalityExpression
        | expression ('is' | 'as') typeSpecifier                    #typeExpression
        // BR: What about "cast X as Y"? Needed in FluentPath?
        | expression ('=' | '~' | '!=' | '!~' | '<>') expression    #equalityExpression
        | expression 'and' expression                               #andExpression
        | expression ('or' | 'xor') expression                      #orExpression
        | expression 'implies' expression                           #impliesExpression
        //| (IDENTIFIER)? '=>' expression                             #lambdaExpression
        ;

term
        : invocation                                            #invocationTerm
        | literal                                               #literalTerm
        | externalConstant                                      #externalConstantTerm
        | '(' expression ')'                                    #parenthesizedTerm
        ;

literal
        : EMPTY                                                 #nullLiteral
        | BOOL                                                  #booleanLiteral
        | STRING                                                #stringLiteral
        | NUMBER                                                #numberLiteral
        | DATETIME                                              #dateTimeLiteral
        | TIME                                                  #timeLiteral
        | quantity                                              #quantityLiteral
        ;

externalConstant
        // BR: Why do we need the "%" here? Is it to introduce a separate namespace?
        : '%' identifier
        ;

invocation                          // Terms that can be used after the function/member invocation '.'
        : identifier                                            #memberInvocation
        | function                                              #functionInvocation
        // BR: Why do we need the "$" here? Isn't it just an identifier?
        | '$this'                                               #thisInvocation
        ;

function
        : identifier '(' paramList? ')'
        ;

paramList
        : expression (',' expression)*
        ;

quantity
        : NUMBER unit?
        ;

unit
        : dateTimePrecision
        | pluralDateTimePrecision
        | STRING // UCUM syntax for units of measure
        ;

dateTimePrecision
        : 'year' | 'month' | 'week' | 'day' | 'hour' | 'minute' | 'second' | 'millisecond'
        ;

pluralDateTimePrecision
        : 'years' | 'months' | 'weeks' | 'days' | 'hours' | 'minutes' | 'seconds' | 'milliseconds'
        ;

typeSpecifier
        : qualifiedIdentifier
        ;

qualifiedIdentifier
        : (identifier)* '.' identifier
        ;

identifier
        : IDENTIFIER
        | QUOTEDIDENTIFIER
        ;


/****************************************************************
    Lexical rules
*****************************************************************/

EMPTY
        // BR: CQL uses curly braces for list selectors, can FluentPath do the same?
        : '{' '}'
        ;                      // To create an empty array (and avoid a NULL literal)

BOOL
        : 'true'
        | 'false'
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

IDENTIFIER
        : ([A-Za-z] | '_')([A-Za-z0-9] | '_')*            // Added _ to support CQL (FHIR could constrain it out)
        ;

QUOTEDIDENTIFIER
        : '"' (ESC | ~[\\"])* '"'
        ;

STRING
        : '\'' (ESC | ~[\'])* '\''
        ;

// Also allows leading zeroes now (just like CQL and XSD)
NUMBER
        : [0-9]+('.' [0-9])?
        ;

// Pipe whitespace to the HIDDEN channel to support retrieving source text through the parser.
WS
        : [ \r\n\t]+ -> channel(HIDDEN)
        ;

COMMENT
        : '/*' .*? '*/' -> channel(HIDDEN)
        ;

LINE_COMMENT
        : '//' ~[\r\n]* -> channel(HIDDEN)
        ;

fragment ESC
        : '\\' (["'\\/fnrt] | UNICODE)    // allow \", \', \\, \/, \f, etc. and \uXXX
        ;

fragment UNICODE
        : 'u' HEX HEX HEX HEX
        ;

fragment HEX
        : [0-9a-fA-F]
        ;
