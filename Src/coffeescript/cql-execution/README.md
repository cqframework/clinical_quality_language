# CQL Execution Framework

The CQL Execution Framework is a set of [CoffeeScript](http://coffeescript.org/) libraries that
can execute CQL artifacts expressed as JSON ELM. At this point, only a small subset of
functionality has been implemented (and a very simple JSON-based patient format is used).

# Project Configuration

To use this project, you must perform the following steps:

1. Install [Node.js](http://nodejs.org/)
2. Install [CoffeeScript](http://coffeescript.org/) (optional)
3. Execute the following from the _cql-execution_ directory: `npm install`

# To Run the CQL Execution Unit Tests

Execute `npm test`.

CoffeeScript's _cake_ command can also be used (if installed): `cake test`.  Alternately, you can
use the _cake_ installed in your local node_modules: `./node_modules/.bin/cake test`.

# To Develop Tests

Many of the tests require JSON ELM data.  It is much easier to write CQL rather than JSON ELM, so
test authors should create test data by adding new CQL to _test/cql-test-data.txt_.  Some
conventions are followed to make testing easier.  The following is an example of some test data:

    # And
    define AllTrue = true and true
    define AllFalse = false and false
    define SomeTrue = true and false

The `# And` indicates the name of the test suite it applies to ("And").  The group of statements
that follows the `# And` represents the CQL Library that will be supplied as test data to the "And"
test suite.

To convert the CQL to CoffeeScript containing the JSON ELM representation, execute
`cake build-test-data`. This will use the java _cql-to-js_ project to generate the
_test/cql-test-data.coffee_ file containing the following exported variable declaration:

    ### And
    library TestSnippet version '1'
    using QUICK
    context PATIENT
    define AllTrue = true and true
    define AllFalse = false and false
    define SomeTrue = true and false
    ###
    
    module.exports.And = {
       "library" : {
          "identifier" : {
             "id" : "TestSnippet",
             "version" : "1"
          },
          "dataModels" : {
             "modelReference" : [ {
                "referencedModel" : {
                   "value" : "http://org.hl7.fhir"
                }
             } ]
          },
          "statements" : {
             "def" : [ {
                "name" : "AllTrue",
                "context" : "PATIENT",
                "expression" : {
                   "type" : "And",
                   "operand" : [ {
                      "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                      "value" : "true",
                      "type" : "Literal"
                   }, {
                      "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                      "value" : "true",
                      "type" : "Literal"
                   } ]
                }
             }, {
                "name" : "AllFalse",
                "context" : "PATIENT",
                "expression" : {
                   "type" : "And",
                   "operand" : [ {
                      "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                      "value" : "false",
                      "type" : "Literal"
                   }, {
                      "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                      "value" : "false",
                      "type" : "Literal"
                   } ]
                }
             }, {
                "name" : "SomeTrue",
                "context" : "PATIENT",
                "expression" : {
                   "type" : "And",
                   "operand" : [ {
                      "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                      "value" : "true",
                      "type" : "Literal"
                   }, {
                      "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                      "value" : "false",
                      "type" : "Literal"
                   } ]
                }
             } ]
          }
       }
    }

Notice that since the CQL didn't declare a library name/version, a data model, or a context,
default values were inserted into the CQL at generation time.  Now this CQL can be used in a test
defined in _test/cql-exec-test.coffee_.  For example:

    describe 'And', ->
      @beforeEach ->
        setup @
    
      it 'should have type: And', ->
        @allTrue.type.should.equal 'And'
    
      it 'should execute allTrue as true', ->
        @allTrue.exec(@ctx).should.be.true
    
      it 'should execute allFalse as false', ->
        @allFalse.exec(@ctx).should.be.false
    
      it 'should execute someTrue as false', ->
        @someTrue.exec(@ctx).should.be.false

The test suite above uses [Mocha](http://visionmedia.github.io/mocha/) and
[Should.js](https://github.com/shouldjs/should.js).  The `setup @` sets up the test case by
creating `@lib` (representing the `CqlLibrary` instance of the test data), creating `@ctx`
(representing a `Context` for execution), and creating local variables for each defined concept
(in this case, `@allTrue`, `@allFalse`, and `@someTrue`).

# Watching For Changes

Rather than continually having to run `cake build-test-data` after every modification to the test
data text file, you can setup a process to _watch_ for changes and regenerate the
`cql-test-data.coffee` file every time it detects changes in the source text file.  Simply
execute `cake watch-test-data`.
