# CQL Execution Framework

The CQL Execution Framework is a set of [CoffeeScript](http://coffeescript.org/) libraries that
can execute CQL artifacts expressed as JSON ELM. At this point, only a small subset of
functionality has been implemented (and a very simple JSON-based patient format is used).

# Project Configuration

To use this project, you should perform the following steps:

1. Install [Node.js](http://nodejs.org/)
2. Install [CoffeeScript](http://coffeescript.org/)
3. Execute the following from the _cql-execution_ directory: `npm install`

# To Execute a Measure

Please note that the CQL Execution Framework has very limited support right now.  It will not
execute most measures.  You should check to see what is implemented before expecting it to work!
For a working example, see `src/example`.

Once the project is complete, it will be packaged in a way that makes execution easier.  For now,
there are some manual steps involved.  First, you must create a JSON representation of the ELM.
For easiest integration, we will generate a coffee file using cql-to-js:

1. Install the [Java 7 SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
2. `cd ${branch}/Src/java` (replacing `${branch}` with the path to your git branch)
3. `./gradlew :cql-to-js:installApp`
4. `./cql-to-js/build/install/cql-to-js/bin/cql-to-js --coffee --input ${path_to_cql} --output ${branch}/Src/coffeescript/cql-execution/src/`

The above example put the measure into the coffeescript src directory to make things easy, but it
doesn't _have_ to go there.  If you put it elsewhere, you'll need to compile it to javascript and
modify the examples below with the new path (where applicable).

Next, create a coffeescript file to execute the measure.  This file will need to contain (or 
`require`) JSON patient representations for testing as well.  For ease of use, let's put the file
in the `coffeescript/cql-execution/src` directory:

    { Library, Context } = require './cql-exec'
    { PatientSource } = require './cql-patient'
    measure = require './age'
    
    lib = new Library(measure)
    psource = new PatientSource [ {
        "id": 1,
        "name": "John Smith",
        "gender": "M",
        "birthdate" : "1980-02-17T06:15",
      }, {
        "id": 2,
        "name": "Sally Smith",
        "gender": "F",
        "birthdate" : "2007-08-02T11:47",
      } ]
    ctx = new Context(lib, psource)
    
    result = lib.exec(ctx)
    console.log JSON.stringify(result, undefined, 2)

In the above file, we've assumed the JSON ELM coffeescript file for the measure is called
`my-measure.coffeescript` and is in the same directory as the file (and `cql-exec` library).  We've
also assumed a couple of very simple patients.  Let's call the file we just created
`my-measure-exec.coffeescript`.

Now we must compile it to javascript in the `${branch}/Src/coffeescript/cql-execution/lib`
directory.  There is a simple Cakefile build script for this (cake is installed with coffeescript):

1. `cd ${build}/Src/coffeescript/cql-execution`
2. `cake build`

Now we can execute the measure using Node.js:

1. `cd ${build}/Src/coffeescript/cql-execution/lib`
2. `node my-measure-exec`

If all is well, it should print the result object to standard out.

# To Run the CQL Execution Unit Tests

Execute `npm test` or `cake test`.

# To Develop Tests

Many of the tests require JSON ELM data.  It is much easier to write CQL rather than JSON ELM, so
test authors should create test data by adding new CQL to _test/data/cql-test-data.txt_.  Some
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
_test/data/cql-test-data.coffee_ file containing the following exported variable declaration:

    ### And
    library TestSnippet version '1'
    using QUICK
    context Patient
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
                "context" : "Patient",
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
                "context" : "Patient",
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
                "context" : "Patient",
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
