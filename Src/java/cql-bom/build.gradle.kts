plugins {
    `java-platform`
    id("cql.maven-publishing-conventions")
}

group = rootProject.group
version = rootProject.version

fun selectPublication(project: Project, publications: Iterable<MavenPublication>): MavenPublication? {
    val publicationList = publications.toList()
    val publicationsByName = publicationList.associateBy { it.name }
    val preferredNames = listOf(
        "jvm",
        "maven"
    )
    return preferredNames.firstNotNullOfOrNull { publicationsByName[it] }
        ?: publicationList.firstOrNull { it.artifactId == project.name }
        ?: publicationList.firstOrNull { it.pom.packaging == "pom" }
}

gradle.projectsEvaluated {
    val addedCoordinates = mutableSetOf<String>()
    rootProject.subprojects
        .filter { it != project }
        .forEach { subproject ->
            val publishing = subproject.extensions.findByType(PublishingExtension::class.java) ?: return@forEach
            val publications = publishing.publications.filterIsInstance<MavenPublication>()
            val publication = selectPublication(subproject, publications) ?: return@forEach
            val coordinate = "${publication.groupId}:${publication.artifactId}:${publication.version}"
            if (addedCoordinates.add(coordinate)) {
                project.dependencies.constraints.add("api", coordinate)
            }
    }
}