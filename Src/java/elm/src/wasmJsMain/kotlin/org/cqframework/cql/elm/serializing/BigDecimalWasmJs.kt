package org.cqframework.cql.elm.serializing

actual class BigDecimal {
    actual constructor(value: String) {
        throw UnsupportedOperationException("Not supported in WASM")
    }

    actual constructor(value: Double) {
        throw UnsupportedOperationException("Not supported in WASM")
    }

    actual fun toPlainString(): String {
        throw UnsupportedOperationException("Not supported in WASM")
    }

    actual fun toDouble(): Double {
        throw UnsupportedOperationException("Not supported in WASM")
    }
}