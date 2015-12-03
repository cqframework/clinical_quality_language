fs = require 'fs'

{spawn, exec} = require 'child_process'

build = (src, dest, watch = false) ->
  args = ['-c', '-m', '-o', dest, src]
  if watch then args.unshift '-w'

  coffee = spawn 'coffee', args
  coffee.stderr.on 'data', (data) ->
    process.stderr.write data.toString()
  coffee.stdout.on 'data', (data) ->
    console.log data.toString()

buildTestData = (watch = false) ->
  args = if watch then [':cql-to-elm:watchTestData'] else [':cql-to-elm:generateTestData']

  gradle = spawn './gradlew', args, cwd: "../../java/"
  gradle.stderr.on 'data', (data) ->
    process.stderr.write data.toString()
  gradle.stdout.on 'data', (data) ->
    console.log data.toString()

task 'build', 'Build lib/ and lib-test/ from src/ and test/', ->
  build('src', 'lib')
  build('src', '../../java/cql-execution/src/main/resources/org/cqframework/cql/execution/javascript')
  build('test', 'lib-test')

task 'build-test-data', 'Build test/data/cql-test-data.coffee from test/data/cql-test-data.txt', ->
  buildTestData()

task "build-all", "Build src/, test/ and test/data/cql-test-data.txt", ->
  invoke 'build'
  invoke 'build-test-data'

task 'watch', 'Watch src/ and test/ for changes', ->
  build('src', 'lib', true)
  build('test', 'lib-test', true)

task 'watch-test-data', 'Watch test/data/cql-test-data.txt for changes', ->
  buildTestData(true)

task "watch-all", "Watch src/, test/, and test/data/cql-test-data.txt for changes", ->
  invoke 'watch'
  invoke 'watch-test-data'

task "test", "run tests", ->
  invoke 'build'
  exec "NODE_ENV=test
    ./node_modules/.bin/mocha
    --compilers coffee:coffee-script/register
    --require coffee-script
    --recursive
    --colors
  ", {maxBuffer: 2048 * 1024 }, (err, output) ->
    throw err if err
    console.log output

task "debug-test", "run tests", ->
  invoke 'build'

  console.log 'To debug, you must install and launch node-inspector:'
  console.log '1) $ npm install -g node-inspector'
  console.log '2) $ node-inspector'
  console.log '3) Visit http://127.0.0.1:8080/?ws=127.0.0.1:8080&port=5858'
  console.log '4) Subsequent tests may require you to reload the page in your browser'

  exec "NODE_ENV=test
    ./node_modules/.bin/mocha
    --recursive
    --colors
    --debug-brk
    ./lib-test/
  ", { maxBuffer: 2048 * 1024 }, (err, output) ->
    throw err if err
    console.log output
