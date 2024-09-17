plugins {
    id("cql.library-conventions")
    id("cql.xjc-conventions")
}

dependencies {
    api(project(":model"))
    testImplementation("org.jeasy:easy-random-core:5.0.0")
    testImplementation("com.tngtech.archunit:archunit:1.2.1")
}

tasks.register<XjcTask>("generateAnnotation") {
    schema = "${projectDir}/../../cql-lm/schema/elm/cqlannotations.xsd"
    extraArgs = listOf("-npa")
}

tasks.register<XjcTask>("generateElm") {
    schema = "${projectDir}/../../cql-lm/schema/elm/library.xsd"
    extraArgs = listOf("-npa", "-XautoInheritance", "-XautoInheritance-xmlTypesExtend=org.cqframework.cql.elm.tracking.Trackable")
}

tasks.compileKotlin {
    dependsOn("generateAnnotation", "generateElm")
}