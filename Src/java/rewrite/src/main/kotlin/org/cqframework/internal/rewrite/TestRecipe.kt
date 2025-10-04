package org.cqframework.internal.rewrite

import org.openrewrite.Recipe

class TestRecipe : Recipe() {
    override fun getDisplayName() = "Replace Foo() with Bar()"

    override fun getDescription() = "Simple example recipe."
}
