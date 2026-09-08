plugins { id("cql.library-conventions") }

dependencies {
    api(project(":cql-to-elm"))
    api(libs.ucum.java)
}
