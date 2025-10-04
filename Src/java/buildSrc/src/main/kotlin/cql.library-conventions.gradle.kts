plugins {
    id("ru.vyarus.animalsniffer")
    id("cql.kotlin-conventions")
}

dependencies {
    // Various libraries for Android signatures are available, Jackson uses this one
    signature("com.toasttab.android:gummy-bears-api-${project.findProperty("android.api.level")}:0.12.0@signature")
}

tasks.animalsnifferTest {
    enabled = false
}