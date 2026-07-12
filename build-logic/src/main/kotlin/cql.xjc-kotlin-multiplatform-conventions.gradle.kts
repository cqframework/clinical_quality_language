plugins {
    id("cql.kotlin-multiplatform-conventions")
    id("cql.xjc-common-conventions")
}

tasks.withType<XjcTask>().configureEach {
    outputDir.set(project.layout.buildDirectory.dir("generated/sources/$name/jvmMain/java"))
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                api("codes.rafael.jaxb2_commons:jaxb2-basics:3.0.0")
                api("codes.rafael.jaxb2_commons:jaxb2-fluent-api:3.0.0")
                api("jakarta.xml.bind:jakarta.xml.bind-api:4.0.1")
                api("org.glassfish.jaxb:jaxb-runtime:4.0.3")
                api("org.eclipse.persistence:org.eclipse.persistence.moxy:4.0.2")
            }
        }
    }
}
