val xjc: Configuration by configurations.creating

dependencies {
    xjc(libs.jaxb2.commons.basics.ant)
    xjc(libs.jaxb2.commons.basics)
    xjc(libs.jaxb2.commons.fluent.api)
    // Eclipse has taken over all Java EE reference components
    // https://www.infoworld.com/article/3310042/eclipse-takes-over-all-java-ee-reference-components.html
    // https://wiki.eclipse.org/Jakarta_EE_Maven_Coordinates
    xjc(libs.jakarta.xml.bind.api)
    xjc(libs.jaxb.xjc)
    xjc(libs.jaxb.runtime)
    xjc(libs.moxy)
    xjc(libs.slf4j.simple)
    xjc(libs.ant)
}
