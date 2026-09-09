plugins {
    id("base")
    alias(libs.plugins.node.gradle)
}

node {
    version.set("24.18.0")
    download.set(true)
}
