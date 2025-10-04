plugins {
    id("cql.library-conventions")
}

dependencies {
    api(project(":elm"))
    api(project(":cql-to-elm"))
    api(project(":ucum"))
    api("org.apache.commons:commons-text:1.10.0")

    testImplementation("org.mockito:mockito-core:5.4.0")
}

tasks.jacocoTestReport {
    sourceDirectories.setFrom(files(
            "${projectDir}/../elm/src/commonMain/kotlin",
            "${projectDir}/../cql-to-elm/src/commonMain/kotlin",
            "${projectDir}/../engine/src/main/kotlin",
    ))

    classDirectories.setFrom(files(
            "${projectDir}/../elm/build/classes/kotlin/jvm/main",
            "${projectDir}/../cql-to-elm/build/classes/kotlin/jvm/main",
            "${projectDir}/../engine/build/classes/kotlin/main",
    ))
}