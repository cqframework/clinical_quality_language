// bug fix for the hapi-bom
configurations.all {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "org.eclipse.jetty") {
                useVersion("11.0.20")
                because("jetty 12 is java 17")
            }
        }
    }
}
