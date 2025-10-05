package internal.rewrite

import org.junit.jupiter.api.Test
import org.openrewrite.test.RecipeSpec
import org.openrewrite.test.RewriteTest

class StringFormatToInterpolationRecipeTest : RewriteTest {

    override fun defaults(spec: RecipeSpec) {
        spec.recipe(StringFormatToInterpolationRecipe())
    }

    @Test
    fun `test recipe compiles`() {
        // Simple test to verify the recipe compiles correctly
        val recipe = StringFormatToInterpolationRecipe()
        assert(recipe.displayName == "Convert String.format to Kotlin string interpolation")
        assert(
            recipe.description ==
                "Converts String.format calls to Kotlin string interpolation using template expressions"
        )
    }
}
