plugins {
    id("cql.library-conventions")
}

dependencies {
    api(project(":elm"))
    api(project(":cql-to-elm"))
    api("org.apache.commons:commons-text:1.10.0")

    testImplementation(project(":model-jackson"))
    testImplementation(project(":elm-jackson"))
    testImplementation("org.mockito:mockito-core:5.4.0")
}

tasks.jacocoTestReport {
    sourceDirectories.setFrom(files(
            "${projectDir}/../elm/src/main/java",
            "${projectDir}/../cql-to-elm/src/main/java",
            "${projectDir}/../engine/src/main/java",
    ))

    classDirectories.setFrom(files(
            "${projectDir}/../elm/build/classes/java/main",
            "${projectDir}/../cql-to-elm/build/classes/java/main",
            "${projectDir}/../engine/build/classes/java/main",
    ))
}