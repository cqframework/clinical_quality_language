import ru.vyarus.gradle.plugin.animalsniffer.AnimalSniffer

plugins {
    id("java-library")
    id("ru.vyarus.animalsniffer")
    id("cql.java-conventions")
    id("cql.kotlin-conventions")
}

dependencies {
    // Various libraries for Android signatures are available, Jackson uses this one
    signature("com.toasttab.android:gummy-bears-api-${project.findProperty("android.api.level")}:0.5.0@signature")
}

tasks.animalsnifferTest {
    enabled = false
}