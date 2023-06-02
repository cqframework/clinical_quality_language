library CqlLibraryTest

codesystem "TestCodeSystem": 'http://terminology.hl7.org/CodeSystem/dummy-code'
code "TestCode": 'testcode' from "TestCodeSystem" display 'TESTCODE'

define "Code":
    "TestCode"