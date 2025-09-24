package org.cqframework.cql.cql2elm.ucum

actual val defaultLazyUcumService =
    lazy<UcumService> { error("No default UCUM service available.") }
