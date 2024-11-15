import ru.vyarus.gradle.plugin.animalsniffer.AnimalSniffer

plugins {
    id("java-library")
    id("ru.vyarus.animalsniffer")
    id("cql.java-conventions")
}

dependencies {
    // Various libraries for Android signatures are available, Jackson uses this one
    signature("com.toasttab.android:gummy-bears-api-${project.findProperty("android.api.level")}:0.10.0:coreLib2@signature")
}

tasks.animalsnifferTest {
    enabled = false
}