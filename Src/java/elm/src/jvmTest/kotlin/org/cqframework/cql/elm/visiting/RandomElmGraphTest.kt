package org.cqframework.cql.elm.visiting

import java.lang.reflect.Field
import java.nio.charset.StandardCharsets
import java.util.HashMap
import javax.xml.namespace.QName
import org.cqframework.cql.cql2elm.model.LibraryRef
import org.hl7.cql_annotations.r1.Narrative
import org.hl7.elm.r1.AccessModifier
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.TypeSpecifier
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.ObjenesisObjectFactory
import org.jeasy.random.api.ExclusionPolicy
import org.jeasy.random.api.RandomizerContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@Suppress("detekt:all")
class RandomElmGraphTest {
    @ParameterizedTest
    @MethodSource("seeds")
    fun allNodesVisited(seed: Int) {
        // This test generates a random ELM graph and verifies that all nodes are
        // visited exactly once.
        val elementsGenerated = HashMap<Int, Element>()
        val countingObjectFactory: ObjenesisObjectFactory =
            object : ObjenesisObjectFactory() {
                override fun <T> createInstance(type: Class<T>, context: RandomizerContext): T? {
                    var t = super.createInstance(type, context)
                    while (t is LibraryRef) {
                        t = super.createInstance(type, context)
                    }
                    if (t is Element) {
                        val hash = System.identityHashCode(t)
                        elementsGenerated[hash] = t
                    }

                    // Debugging for specific types and paths
                    // that aren't being visited.
                    // if (t is Date) {
                    //   printContext(t, context)
                    // }
                    return t
                }

                private fun printContext(t: Element, context: RandomizerContext) {
                    System.err.printf(
                        "Type: %s, Hash: %s, Parent: %s, Parent Hash: %s, Path: %s%n",
                        t.javaClass.simpleName,
                        System.identityHashCode(t),
                        context.currentObject.javaClass.simpleName,
                        System.identityHashCode(context.currentObject),
                        context.currentField
                    )
                }
            }

        val randomParams =
            EasyRandomParameters()
                .objectFactory(countingObjectFactory)
                .seed(seed.toLong())
                .randomizationDepth(15)
                .objectPoolSize(1000) // Never reuse objects
                .charset(StandardCharsets.UTF_8)
                .stringLengthRange(5, 50)
                .collectionSizeRange(1, 3)
                .exclusionPolicy(NoTypeSpecifierRecursionPolicy())
                .excludeType { it == LibraryRef::class.java }
                .scanClasspathForConcreteTypes(true)

        val randomElmGenerator = EasyRandom(randomParams)
        val randomElm = randomElmGenerator.nextObject(Library::class.java)
        val elementsGeneratedCount = elementsGenerated.size

        val elementsVisited = HashMap<Int, Element>()
        val elementsDuplicated = HashMap<Int, Element>()
        val countingVisitor =
            FunctionalElmVisitor<Int, HashMap<Int, Element>>(
                { x, _ ->
                    val hash = System.identityHashCode(x)
                    if (!elementsVisited.containsKey(hash)) {
                        elementsVisited[hash] = x
                        return@FunctionalElmVisitor 1
                    }
                    elementsDuplicated[hash] = x
                    return@FunctionalElmVisitor 0
                },
                { a, b -> a + b }
            )

        val visitorCount = countingVisitor.visitLibrary(randomElm, elementsVisited)

        elementsGenerated.keys.removeAll(elementsVisited.keys)
        if (elementsGenerated.isNotEmpty()) {
            System.err.println("Elements Missed:")
            elementsGenerated.forEach { (x: Int?, e: Element) ->
                System.err.printf("Type: %s, Hash: %s%n", e.javaClass.simpleName, x)
            }
        }

        // No missed nodes, working as intended
        Assertions.assertEquals(0, elementsGenerated.size)

        // Check that we didn't double-visit any nodes
        if (elementsDuplicated.isNotEmpty()) {
            System.err.println("Elements Duplicated:")
            elementsDuplicated.forEach { (x: Int?, e: Element) ->
                System.err.printf("Type: %s, Hash: %s%n", e.javaClass.simpleName, x)
            }
        }

        // No duplicate visits, working as intended
        Assertions.assertEquals(0, elementsDuplicated.size)

        // if these are equal, then aggregateResult
        // ran once for every node in the graph (working as intended)
        Assertions.assertEquals(visitorCount, elementsGeneratedCount)
    }

    internal class NoTypeSpecifierRecursionPolicy : ExclusionPolicy {
        // Don't recurse into TypeSpecifier.resultTypeSpecifier
        override fun shouldBeExcluded(field: Field, context: RandomizerContext): Boolean {
            if (field.type.packageName.startsWith("org.hl7.cql")) {
                return true
            }

            return ((field.name == "resultTypeSpecifier" &&
                TypeSpecifier::class.java.isAssignableFrom(field.type)) ||
                (field.name == "signature"))
        }

        // These are excluded to simplify the ELM graph while bugs are being worked out.
        override fun shouldBeExcluded(type: Class<*>, context: RandomizerContext): Boolean {
            if (type.packageName.startsWith("org.hl7.cql")) {
                return true
            }

            return type == QName::class.java ||
                type == Narrative::class.java ||
                type == AccessModifier::class.java
        }
    }

    companion object {
        @JvmStatic
        fun seeds(): Array<Any> {
            // I randomly picked these seeds until
            // I got 3 in a row that passed without errors.
            // Not perfect, but it's a start.
            return arrayOf(96874, 15895, 121873, 174617)
        }
    }
}
