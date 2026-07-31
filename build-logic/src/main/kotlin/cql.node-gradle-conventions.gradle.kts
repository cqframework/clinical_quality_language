plugins {
    id("base")
    id("com.github.node-gradle.node")
}

node {
    version.set("24.18.0")
    download.set(true)
}
