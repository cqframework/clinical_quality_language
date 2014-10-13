fs = require 'fs'

{print} = require 'sys'
{spawn, exec} = require 'child_process'

build = (callback) ->
  coffee = spawn 'coffee', ['-c', '-o', 'lib', 'src']
  coffee.stderr.on 'data', (data) ->
    process.stderr.write data.toString()
  coffee.stdout.on 'data', (data) ->
    print data.toString()
  coffee.on 'exit', (code) ->
    callback?() if code is 0

buildTestData = (callback) ->
  gradle = spawn './gradlew', [':cql-to-js:generateTestData'], cwd: "../../java/"
  gradle.stderr.on 'data', (data) ->
    process.stderr.write data.toString()
  gradle.stdout.on 'data', (data) ->
    print data.toString()
  gradle.on 'exit', (code) ->
    callback?() if code is 0

task 'build', 'Build lib/ from src/', ->
  build()

task 'watch', 'Watch src/ for changes', ->
  coffee = spawn 'coffee', ['-w', '-c', '-o', 'lib', 'src']
  coffee.stderr.on 'data', (data) ->
    process.stderr.write data.toString()
  coffee.stdout.on 'data', (data) ->
    print data.toString()

task 'build-test-data', 'Build test/cql-test-data.coffee from test/cql-test-data.txt', ->
  buildTestData()

task 'watch-test-data', 'Watch test/cql-test-data.txt for changes', ->
  gradle = spawn './gradlew', [':cql-to-js:watchTestData'], cwd: "../../java/"
  gradle.stderr.on 'data', (data) ->
    process.stderr.write data.toString()
  gradle.stdout.on 'data', (data) ->
    print data.toString()
 
task "watch-all", "Watch src/ and test/cql-test-data.txt for changes", ->
  invoke 'watch'
  invoke 'watchTestData'

task "test", "run tests", ->
  invoke 'build'
  exec "NODE_ENV=test 
    ./node_modules/.bin/mocha 
    --compilers coffee:coffee-script/register
    --require coffee-script
    --colors
  ", (err, output) ->
    throw err if err
    console.log output