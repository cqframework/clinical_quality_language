plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // For compiling code snippets in tests
    testImplementation(project(":cql-to-elm"))
    testImplementation(kotlin("test"))
}

// Assembles the documentation site into build/site/
val buildDocs by tasks.registering {
    group = "documentation"
    description = "Assembles the documentation site into build/site/"

    val docsDir = projectDir
    val outputDir = layout.buildDirectory.dir("site")
    val modulesDir = rootDir

    inputs.dir(docsDir)
    inputs.files(fileTree(modulesDir) { include("*/README.md") })
    outputs.dir(outputDir)

    doLast {
        val siteDir = outputDir.get().asFile
        siteDir.deleteRecursively()
        siteDir.mkdirs()

        // Extract snippets from test sources
        val snippets = mutableMapOf<String, String>()
        val snippetDir = docsDir.resolve("src/test/kotlin")
        val beginPattern = Regex("""//\s*BEGIN:(\S+)""")
        val endPattern = Regex("""//\s*END:(\S+)""")

        if (snippetDir.exists()) {
            snippetDir.walkTopDown()
                .filter { it.isFile && it.extension in listOf("kt", "java") }
                .forEach { file ->
                    val lines = file.readLines()
                    var currentSnippet: String? = null
                    val snippetLines = mutableListOf<String>()

                    for (line in lines) {
                        val beginMatch = beginPattern.find(line)
                        val endMatch = endPattern.find(line)

                        when {
                            beginMatch != null -> {
                                currentSnippet = beginMatch.groupValues[1]
                                snippetLines.clear()
                            }
                            endMatch != null && currentSnippet == endMatch.groupValues[1] -> {
                                val trimmed = snippetLines
                                    .dropWhile { it.isBlank() }
                                    .dropLastWhile { it.isBlank() }
                                // Strip common leading whitespace
                                val minIndent = trimmed
                                    .filter { it.isNotBlank() }
                                    .minOfOrNull { line -> line.takeWhile { it.isWhitespace() }.length } ?: 0
                                snippets[currentSnippet!!] = trimmed
                                    .map { if (it.length >= minIndent) it.drop(minIndent) else it }
                                    .joinToString("\n")
                                currentSnippet = null
                            }
                            currentSnippet != null -> {
                                snippetLines.add(line)
                            }
                        }
                    }
                }
        }

        if (snippets.isNotEmpty()) {
            println("Extracted ${snippets.size} snippet(s): ${snippets.keys.joinToString(", ")}")
        }

        // Process and copy cross-cutting docs (excluding build files and src)
        val snippetPlaceholder = Regex("""<!--\s*SNIPPET:(\S+)\s*-->""")

        fun processMarkdown(content: String): String {
            return snippetPlaceholder.replace(content) { match ->
                val snippetName = match.groupValues[1]
                val snippet = snippets[snippetName]
                if (snippet != null) {
                    "```kotlin\n$snippet\n```"
                } else {
                    System.err.println("Warning: Snippet '$snippetName' not found")
                    match.value
                }
            }
        }

        docsDir.listFiles()?.filter {
            it.isFile && it.extension == "md"
        }?.forEach { file ->
            val processed = processMarkdown(file.readText())
            siteDir.resolve(file.name).writeText(processed)
        }

        // Copy module markdown files and collect for index
        val modulesOutDir = siteDir.resolve("modules")
        modulesOutDir.mkdirs()
        val moduleLinks = mutableListOf<Triple<String, String, String>>() // module, docName, path

        val excludedDirs = setOf("build", "buildSrc", "docs", "gradle", "config", "kotlin-js-store")
        modulesDir.listFiles()?.filter {
            it.isDirectory && !it.name.startsWith(".") && it.name !in excludedDirs
        }?.sortedBy { it.name }?.forEach { moduleDir ->
            // Find all markdown files in the module root
            val mdFiles = moduleDir.listFiles()?.filter { it.isFile && it.extension == "md" } ?: emptyList()
            if (mdFiles.isNotEmpty()) {
                val moduleOutDir = modulesOutDir.resolve(moduleDir.name)
                moduleOutDir.mkdirs()
                mdFiles.sortedBy { it.name }.forEach { mdFile ->
                    val processed = processMarkdown(mdFile.readText())
                    moduleOutDir.resolve(mdFile.name).writeText(processed)
                }
                // Use README.md as the module link, or first file if no README
                val indexFile = mdFiles.find { it.name.equals("README.md", ignoreCase = true) } ?: mdFiles.first()
                moduleLinks.add(Triple(moduleDir.name, indexFile.nameWithoutExtension, "modules/${moduleDir.name}/${indexFile.name}"))
            }
        }

        // Generate index.md with navigation
        val crossCuttingDocs = siteDir.listFiles()
            ?.filter { it.isFile && it.extension == "md" && it.name != "index.md" }
            ?.sortedBy { it.name }
            ?.map { it.nameWithoutExtension to it.name }
            ?: emptyList()

        val indexContent = buildString {
            appendLine("# CQL Developer Documentation")
            appendLine()
            appendLine("## Guides")
            appendLine()
            for ((name, path) in crossCuttingDocs) {
                appendLine("- [$name]($path)")
            }
            if (moduleLinks.isNotEmpty()) {
                appendLine()
                appendLine("## Modules")
                appendLine()
                for ((moduleName, _, path) in moduleLinks) {
                    appendLine("- [$moduleName]($path)")
                }
            }
        }
        siteDir.resolve("index.md").writeText(indexContent)

        println("Documentation site built at: ${siteDir.absolutePath}")
    }
}
