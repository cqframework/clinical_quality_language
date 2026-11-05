package org.cqframework.cql.cql2elm

import kotlinx.io.files.Path

interface PathAware {
    fun setPath(path: Path)
}
