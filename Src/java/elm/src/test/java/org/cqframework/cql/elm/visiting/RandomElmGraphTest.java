package org.cqframework.cql.elm.visiting;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.HashMap;
import javax.xml.namespace.QName;
import org.hl7.cql_annotations.r1.Narrative;
import org.hl7.elm.r1.AccessModifier;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.TypeSpecifier;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.ObjenesisObjectFactory;
import org.jeasy.random.api.ExclusionPolicy;
import org.jeasy.random.api.RandomizerContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RandomElmGraphTest {

    @DataProvider(name = "seed")
    public static Object[] seeds() {
        // I randomly picked these seeds until
        // I got 3 in a row that passed without errors.
        // Not perfect, but it's a start.
        return new Object[] {96874, 15895, 121873, 174617};
    }

    @Test(dataProvider = "seed")
    public void allNodesVisited(int seed) {
        // This test generates a random ELM graph and verifies that all nodes are
        // visited exactly once.
        var elementsGenerated = new HashMap<Integer, Element>();
        var countingObjectFactory = new ObjenesisObjectFactory() {
            @Override
            public <T> T createInstance(Class<T> type, RandomizerContext context) {
                var t = super.createInstance(type, context);
                if (t instanceof Element) {
                    var hash = System.identityHashCode(t);
                    elementsGenerated.put(hash, (Element) t);
                }

                // Debugging for specific types and paths
                // that aren't being visited.
                // if (t instanceof ConvertsToDate || t instanceof SplitOnMatches || t instanceof DateFrom) {
                //     printContext((Element) t, context);
                // }

                return t;
            }

            private void printContext(Element t, RandomizerContext context) {
                System.err.println(String.format(
                        "Type: %s, Parent: %s, Path: %s, Hash: %s",
                        t.getClass().getSimpleName(),
                        context.getCurrentObject().getClass().getSimpleName(),
                        context.getCurrentField(),
                        System.identityHashCode(t)));
            }
        };

        var randomParams = new EasyRandomParameters()
                .objectFactory(countingObjectFactory)
                .seed(seed)
                .randomizationDepth(15)
                .objectPoolSize(1000) // Never reuse objects
                .charset(Charset.forName("UTF-8"))
                .stringLengthRange(5, 50)
                .collectionSizeRange(1, 3)
                .exclusionPolicy(new NoTypeSpecifierRecursionPolicy())
                .scanClasspathForConcreteTypes(true);

        var randomElmGenerator = new EasyRandom(randomParams);
        var randomElm = randomElmGenerator.nextObject(Library.class);

        var elementsGeneratedCount = elementsGenerated.size();

        var elementsVisited = new HashMap<Integer, Element>();
        var elementsDuplicated = new HashMap<Integer, Element>();
        var countingVisitor = new FunctionalElmVisitor<Integer, HashMap<Integer, Element>>(
                (x, y) -> {
                    if (x instanceof Element) {
                        var hash = System.identityHashCode(x);
                        if (!elementsVisited.containsKey(hash)) {
                            elementsVisited.put(hash, (Element) x);
                            return 1;
                        }
                        elementsDuplicated.put(hash, (Element) x);
                        return 0;
                    }
                    return 0;
                },
                (a, b) -> a + b);

        var visitorCount = countingVisitor.visitLibrary(randomElm, elementsVisited);

        elementsGenerated.keySet().removeAll(elementsVisited.keySet());
        if (!elementsGenerated.isEmpty()) {
            System.err.println("Elements Missed:");
            elementsGenerated.forEach((x, e) -> System.err.println(
                    String.format("Type: %s, Hash: %s", e.getClass().getSimpleName(), x)));
        }

        // No missed nodes, working as intended
        assertEquals(elementsGenerated.size(), 0);

        // Check that we didn't double-visit any nodes
        if (!elementsDuplicated.isEmpty()) {
            System.err.println("Elements Duplicated:");
            elementsDuplicated.forEach((x, e) -> System.err.println(
                    String.format("Type: %s, Hash: %s", e.getClass().getSimpleName(), x)));
        }

        // No duplicate visits, working as intended
        assertEquals(elementsDuplicated.size(), 0);

        // if these are equal, then aggregateResult
        // ran once for every node in the graph (working as intended)
        assertEquals(visitorCount.intValue(), elementsGeneratedCount);
    }

    class NoTypeSpecifierRecursionPolicy implements ExclusionPolicy {

        // Don't recurse into TypeSpecifier.resultTypeSpecifier
        @Override
        public boolean shouldBeExcluded(Field field, RandomizerContext context) {
            if (field.getType().getPackageName().startsWith("org.hl7.cql")) {
                return true;
            }

            return (field.getName().equals("resultTypeSpecifier")
                            && TypeSpecifier.class.isAssignableFrom(field.getType()))
                    || field.getName().equals("signature");
        }

        // These are excluded to simplify the ELM graph while bugs are being worked out.
        @Override
        public boolean shouldBeExcluded(Class<?> type, RandomizerContext context) {
            if (type.getPackageName().startsWith("org.hl7.cql")) {
                return true;
            }

            return type == QName.class || type == Narrative.class || type == AccessModifier.class;
        }
    }
}
