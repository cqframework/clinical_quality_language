package internal.rewrite

import org.junit.jupiter.api.Test
import org.openrewrite.kotlin.Assertions.kotlin
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
                "Converts String.format calls to Kotlin string interpolation using template expressions."
        )
    }

    @Test
    fun `converts simple string format with single argument`() {
        rewriteRun(
            kotlin(
                """
                fun test() {
                    val name = "John"
                    val message = String.format("Hello %s", name)
                }
                """
                    .trimIndent(),
                $$"""
                fun test() {
                    val name = "John"
                    val message = "Hello $name"
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `converts string format with multiple arguments`() {
        rewriteRun(
            kotlin(
                """
                fun test() {
                    val name = "John"
                    val age = 25
                    val message = String.format("Hello %s, you are %d years old", name, age)
                }
                """
                    .trimIndent(),
                $$"""
                fun test() {
                    val name = "John"
                    val age = 25
                    val message = "Hello $name, you are $age years old"
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `converts string format with hex formatting`() {
        rewriteRun(
            kotlin(
                """
                fun test() {
                    val red = 255
                    val green = 128
                    val blue = 64
                    val color = String.format("#%02x%02x%02x", red, green, blue)
                }
                """
                    .trimIndent(),
                $$"""
                fun test() {
                    val red = 255
                    val green = 128
                    val blue = 64
                    val color = "#${red.toString(16).padStart(2, '0')}${green.toString(16).padStart(2, '0')}${blue.toString(16).padStart(2, '0')}"
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `converts string format with field access - like SystemDataProvider accessor pattern`() {
        rewriteRun(
            kotlin(
                """
                fun test() {
                    val path = "fieldName"
                    val accessorMethodName = String.format("%s%s%s", "get", path.substring(0, 1).uppercase(), path.substring(1))
                }
                """
                    .trimIndent(),
                $$"""
                fun test() {
                    val path = "fieldName"
                    val accessorMethodName = "${"get"}${path.substring(0, 1).uppercase()}${path.substring(1)}"
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `converts string format with class names - like SystemDataProvider error messages`() {
        rewriteRun(
            kotlin(
                """
                fun test() {
                    val path = "fieldName"
                    val className = "MyClass"
                    val errorMessage = String.format(
                        "Could not determine field for path %s of type %s",
                        path,
                        className
                    )
                }
                """
                    .trimIndent(),
                $$"""
                fun test() {
                    val path = "fieldName"
                    val className = "MyClass"
                    val errorMessage = "Could not determine field for path $path of type $className"
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `converts string format with method invocations`() {
        rewriteRun(
            kotlin(
                """
                fun test() {
                    val clazz: Class<String> = String::class.java
                    val message = String.format("Could not create an instance of class %s.", clazz.name)
                }
                """
                    .trimIndent(),
                $$"""
                fun test() {
                    val clazz: Class<String> = String::class.java
                    val message = "Could not create an instance of class ${clazz.name}."
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `converts string format with package and type names`() {
        rewriteRun(
            kotlin(
                """
                fun test() {
                    val packageName = "org.example"
                    val typeName = "MyType"
                    val message = String.format("Could not resolve type %s.%s.", packageName, typeName)
                }
                """
                    .trimIndent(),
                $$"""
                fun test() {
                    val packageName = "org.example"
                    val typeName = "MyType"
                    val message = "Could not resolve type ${packageName}.${typeName}."
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `converts string format with float formatting`() {
        rewriteRun(
            kotlin(
                """
                fun test() {
                    val depth = 5
                    val x = 3.14159
                    val idString = String.format("%d-%f", depth, x)
                }
                """
                    .trimIndent(),
                $$"""
                fun test() {
                    val depth = 5
                    val x = 3.14159
                    val idString = "${depth}-${x}"
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `handles percent literals correctly`() {
        rewriteRun(
            kotlin(
                """
                fun test() {
                    val percentage = 85
                    val message = String.format("Progress: %d%%", percentage)
                }
                """
                    .trimIndent(),
                $$"""
                fun test() {
                    val percentage = 85
                    val message = "Progress: ${percentage}%"
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `handles newline formatting correctly`() {
        rewriteRun(
            kotlin(
                """
                fun test() {
                    val line1 = "First line"
                    val line2 = "Second line"
                    val message = String.format("%s%n%s", line1, line2)
                }
                """
                    .trimIndent(),
                $$"""
                fun test() {
                    val line1 = "First line"
                    val line2 = "Second line"
                    val message = "${line1}\\n${line2}"
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `converts uppercase hex formatting`() {
        rewriteRun(
            kotlin(
                """
                fun test() {
                    val value = 255
                    val hexString = String.format("Value: %X", value)
                }
                """
                    .trimIndent(),
                $$"""
                fun test() {
                    val value = 255
                    val hexString = "Value: ${value.toString(16).uppercase()}"
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `does not convert non-String format calls`() {
        rewriteRun(
            kotlin(
                """
                fun test() {
                    val formatter = java.util.Formatter()
                    val result = formatter.format("Hello %s", "World")
                }
                """
                    .trimIndent()
            )
        )
    }

    @Test
    fun `converts complex SystemDataProvider-like error message`() {
        rewriteRun(
            kotlin(
                """
                fun test() {
                    val path = "fieldName"
                    val clazz: Class<String> = String::class.java
                    val errorMessage = String.format(
                        "Errors occurred attempting to invoke the accessor function for property %s of type %s",
                        path,
                        clazz.simpleName
                    )
                }
                """
                    .trimIndent(),
                $$"""
                fun test() {
                    val path = "fieldName"
                    val clazz: Class<String> = String::class.java
                    val errorMessage = "Errors occurred attempting to invoke the accessor function for property $path of type ${clazz.simpleName}"
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `converts SystemDataProvider-like accessor method name generation`() {
        rewriteRun(
            kotlin(
                """
                fun getReadAccessor(path: String) {
                    val accessorMethodName = String.format("%s%s%s", "get", path[0].uppercase(), path.substring(1))
                }
                """
                    .trimIndent(),
                $$"""
                fun getReadAccessor(path: String) {
                    val accessorMethodName = "${"get"}${path[0].uppercase()}${path.substring(1)}"
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `converts SystemDataProvider-like set accessor method name generation`() {
        rewriteRun(
            kotlin(
                """
                fun getWriteAccessor(path: String) {
                    val accessorMethodName = String.format("%s%s%s", "set", path[0].uppercase(), path.substring(1))
                }
                """
                    .trimIndent(),
                $$"""
                fun getWriteAccessor(path: String) {
                    val accessorMethodName = "${"set"}${path[0].uppercase()}${path.substring(1)}"
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `converts SystemDataProvider-like Class forName pattern`() {
        rewriteRun(
            kotlin(
                """
                fun resolveType(packageName: String, typeName: String): Class<*> {
                    return Class.forName(String.format("%s.%s", packageName, typeName))
                }
                """
                    .trimIndent(),
                $$"""
                fun resolveType(packageName: String, typeName: String): Class<*> {
                    return Class.forName("${packageName}.${typeName}")
                }
                """
                    .trimIndent(),
            )
        )
    }

    @Test
    fun `converts SystemDataProvider-like instance creation error message`() {
        rewriteRun(
            kotlin(
                """
                fun createInstance(clazz: Class<*>) {
                    throw IllegalArgumentException(
                        String.format("Could not create an instance of class %s.", clazz.name)
                    )
                }
                """
                    .trimIndent(),
                $$"""
                fun createInstance(clazz: Class<*>) {
                    throw IllegalArgumentException(
                        "Could not create an instance of class ${clazz.name}."
                    )
                }
                """
                    .trimIndent(),
            )
        )
    }
}
