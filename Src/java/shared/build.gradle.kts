plugins {
    id("cql.kotlin-multiplatform-conventions")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("com.ionspin.kotlin:bignum:0.3.10")
            }
        }
    }
}